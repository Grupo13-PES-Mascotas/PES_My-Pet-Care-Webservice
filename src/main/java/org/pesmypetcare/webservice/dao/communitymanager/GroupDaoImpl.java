package org.pesmypetcare.webservice.dao.communitymanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.dao.usermanager.UserDao;
import org.pesmypetcare.webservice.dao.usermanager.UserDaoImpl;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.entity.communitymanager.TagEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * @author Santiago Del Rey
 */
@Repository
public class GroupDaoImpl implements GroupDao {
    private CollectionReference groups;
    private CollectionReference groupsNames;
    private CollectionReference tags;
    private Firestore db;
    private WriteBatch batch;

    @Autowired
    private UserDao userDao;

    public GroupDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
        groups = db.collection("groups");
        groupsNames = db.collection("groups_names");
        tags = db.collection("tags");
    }

    @Override
    public void createGroup(GroupEntity entity) throws DatabaseAccessException {
        batch = db.batch();
        DocumentReference groupRef = groups.document();
        batch.set(groupRef, entity);
        String name = entity.getName();
        saveGroupName(name, groupRef.getId(), batch);
        String creator = entity.getCreator();
        saveUserAsMember(creator, groupRef, batch);
        userDao.addGroupSubscription(creator, groupRef.getId(), name, batch);
        List<String> tags = entity.getTags();
        for (String tag : tags) {
            addGroupToTag(tag, name, batch);
        }
        batch.commit();
    }

    @Override
    public void deleteGroup(String name) throws DatabaseAccessException {
        String id = getGroupId(name);
        batch = db.batch();
        deleteGroupFromAllTags(id);
        deleteCollection(groups.document(id).collection("members"), batch);
        DocumentReference groupRef = groups.document(id);
        batch.delete(groupRef);
        DocumentReference namesRef = groupsNames.document(name);
        batch.delete(namesRef);
        batch.commit();
    }

    @Override
    public GroupEntity getGroup(String name) throws DatabaseAccessException {
        String id = getGroupId(name);
        DocumentSnapshot snapshot = getDocumentSnapshot(groups, id);
        return getGroupEntityAndChangeCreatorUidForUsername(snapshot);
    }

    @Override
    public List<GroupEntity> getAllGroups() throws DatabaseAccessException {
        ApiFuture<QuerySnapshot> future = groups.get();
        try {
            List<GroupEntity> groupList = new ArrayList<>();
            List<QueryDocumentSnapshot> groupDocs = future.get().getDocuments();
            for (QueryDocumentSnapshot group : groupDocs) {
                groupList.add(getGroupEntityAndChangeCreatorUidForUsername(group));
            }
            return groupList;
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("list-error", "The groups could not be retrieved");
        }
    }

    @Override
    public void updateField(String name, String field, String newValue) throws DatabaseAccessException {
        String id = getGroupId(name);
        groups.document(id).update(field, newValue);
        if ("name".equals(field)) {
            if (groupNameInUse(newValue)) {
                throw new DatabaseAccessException("invalid-group-name", "The name is already in use");
            }
            batch = db.batch();
            changeNameInTags(name, newValue, batch);
            DocumentReference namesRef = groupsNames.document(name);
            batch.delete(namesRef);
            saveGroupName(newValue, id, batch);
            batch.commit();
        }
    }

    @Override
    public boolean groupNameInUse(String name) throws DatabaseAccessException {
        DocumentSnapshot snapshot = getDocumentSnapshot(groupsNames, name);
        return snapshot.exists();
    }

    @Override
    public void subscribe(String group, String username) throws DatabaseAccessException {
        String userUid = userDao.getUid(username);
        String groupId = getGroupId(group);
        DocumentReference groupRef = groups.document(groupId);
        batch = db.batch();
        saveUserAsMember(userUid, groupRef, batch);
        userDao.addGroupSubscription(userUid, groupId, group, batch);
        batch.commit();
    }

    @Override
    public void unsubscribe(String group, String username) throws DatabaseAccessException {
        String userUid = userDao.getUid(username);
        String groupId = getGroupId(group);
        DocumentReference groupRef = groups.document(groupId);
        batch = db.batch();
        deleteUserFromMember(userUid, groupRef, batch);
        userDao.deleteGroupSubscription(userUid, groupId, batch);
        batch.commit();
    }

    private void deleteUserFromMember(String userUid, DocumentReference groupRef, WriteBatch batch) {
        DocumentReference memberRef = groupRef.collection("members").document(userUid);
        batch.delete(memberRef);
    }

    @Override
    public void updateTags(String group, List<String> newTags, List<String> deletedTags) throws DatabaseAccessException {
        String id = getGroupId(group);
        DocumentReference groupRef = groups.document(id);
        Map<String, Object> data = new HashMap<>();
        batch = db.batch();
        if (deletedTags != null) {
            data.put("tags", FieldValue.arrayRemove(deletedTags.toArray()));
            batch.update(groupRef, data);
            for (String tag : deletedTags) {
                deleteGroupFromTag(tag, group, batch);
            }
        }
        if (newTags != null) {
            data.put("tags", FieldValue.arrayUnion(newTags.toArray()));
            batch.update(groupRef, data);
            for (String tag : newTags) {
                addGroupToTag(tag, group, batch);
            }
        }
        batch.commit();
    }

    @Override
    public Map<String, TagEntity> getAllTags() throws DatabaseAccessException {
        ApiFuture<QuerySnapshot> future = tags.get();
        try {
            Map<String, TagEntity> tagsMap = new HashMap<>();
            List<QueryDocumentSnapshot> tagsDocs = future.get().getDocuments();
            for (QueryDocumentSnapshot tag : tagsDocs) {
                TagEntity tagEntity = tag.toObject(TagEntity.class);
                tagsMap.put(tag.getId(), tagEntity);
            }
            return tagsMap;
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("list-error", "The groups could not be retrieved");
        }
    }

    /**
     * Saves the user as a member of the group.
     *
     * @param userUid The user's uid
     * @param groupRef The group document reference
     * @param batch The batch of writes to which it belongs
     */
    private void saveUserAsMember(String userUid, DocumentReference groupRef, WriteBatch batch) {
        Map<String, Boolean> data = new HashMap<>();
        data.put("exists", true);
        DocumentReference memberRef = groupRef.collection("members").document(userUid);
        batch.set(memberRef, data);
    }

    /**
     * Gets a document snapshot from a collection.
     * @param collection The collection to which the document belongs
     * @param documentName The document name
     * @return The document snapshot
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private DocumentSnapshot getDocumentSnapshot(CollectionReference collection,
                                                 String documentName) throws DatabaseAccessException {
        ApiFuture<DocumentSnapshot> future = collection.document(documentName).get();
        DocumentSnapshot snapshot;
        try {
            snapshot = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("retrieval-failed", e.getMessage());
        }
        return snapshot;
    }

    /**
     * Saves the group name in database.
     * @param name The group name
     * @param groupId The group id to which the name belongs
     * @param batch The batch of writes to which it belongs
     */
    private void saveGroupName(String name, String groupId, WriteBatch batch) {
        DocumentReference namesRef = groupsNames.document(name);
        Map<String, String> docData = new HashMap<>();
        docData.put("group", groupId);
        batch.set(namesRef, docData);
    }

    /**
     * Creates an entry in the tag collection for the group.
     * @param tag The tag
     * @param groupId The group id
     * @param batch The batch of writes to which it belongs
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void addGroupToTag(String tag, String groupId, WriteBatch batch) throws DatabaseAccessException {
        DocumentSnapshot snapshot = getDocumentSnapshot(tags, tag);
        DocumentReference tagRef = tags.document(tag);
        Map<String, Object> data = new HashMap<>();
        data.put("groups", FieldValue.arrayUnion(groupId));
        if (!snapshot.exists()) {
            batch.set(tagRef, data);
        } else {
            batch.update(tagRef, data);
        }
    }

    /**
     * Deletes an entry in the tag collection for the group.
     * @param tag The tag
     * @param group The group name
     * @param batch The batch of writes to which it belongs
     */
    private void deleteGroupFromTag(String tag, String group, WriteBatch batch) {
        DocumentReference tagRef = tags.document(tag);
        Map<String, Object> data = new HashMap<>();
        data.put("groups", FieldValue.arrayRemove(group));
        batch.update(tagRef, data);
    }

    /**
     * Deletes a group from all its tags.
     *
     * @param group The group name
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void deleteGroupFromAllTags(String group) throws DatabaseAccessException {
        Query query = tags.whereArrayContains("groups", group);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                batch.delete(document.getReference());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("retrieval-failed", "Failure when retrieving the tags data");
        }
    }

    /**
     * Updates the group name in all its tags.
     * @param oldName The old name
     * @param newName The new name
     * @param batch The batch of writes to which it belongs
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void changeNameInTags(String oldName, String newName, WriteBatch batch) throws DatabaseAccessException {
        Query query = tags.whereArrayContains("groups", oldName);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                deleteGroupFromTag(document.getId(), oldName, batch);
                addGroupToTag(document.getId(), newName, batch);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("retrieval-failed", "Failure when retrieving the tags data");
        }
    }

    private void changeNameInSubscription(String oldName, String newName, WriteBatch batch) throws DatabaseAccessException {
        Query query = db.collectionGroup("subscriptions").whereEqualTo("group", oldName);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                deleteGroupFromTag(document.getId(), oldName, batch);
                addGroupToTag(document.getId(), newName, batch);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("retrieval-failed", "Failure when retrieving the tags data");
        }
    }

    /**
     * Deletes the specified collection.
     * @param collection The collection to delete
     * @param batch The batch of writes to which it belongs
     */
    private void deleteCollection(CollectionReference collection, WriteBatch batch) {
        Iterable<DocumentReference> membersDocRefs = collection.listDocuments();
        for (DocumentReference docRef : membersDocRefs) {
            batch.delete(docRef);
        }
    }

    /**
     * Gets a group entity from its document snapshot and changes its creator uid for the username.
     * @param snapshot The group document snapshot
     * @return The group entity with the creator username instead of the uid
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private GroupEntity getGroupEntityAndChangeCreatorUidForUsername(DocumentSnapshot snapshot)
        throws DatabaseAccessException {
        UserDao userDao  = new UserDaoImpl();
        GroupEntity group = snapshot.toObject(GroupEntity.class);
        String uid = Objects.requireNonNull(group).getCreator();
        group.setCreator(userDao.getField(uid, UserDaoImpl.USERNAME_FIELD));
        return group;
    }

    /**
     * Gets the group id.
     * @param name The group name
     * @return The group id
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private String getGroupId(String name) throws DatabaseAccessException {
        DocumentSnapshot snapshot = getDocumentSnapshot(groupsNames, name);
        return (String) snapshot.get("group");
    }
}
