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
import org.pesmypetcare.webservice.entity.communitymanager.Group;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.entity.communitymanager.TagEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.thirdpartyservices.FirebaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Santiago Del Rey
 */
@Repository
public class GroupDaoImpl implements GroupDao {
    private final DateTimeFormatter timeFormatter;
    private CollectionReference groups;
    private CollectionReference groupsNames;
    private CollectionReference tags;
    private Firestore db;

    @Autowired
    private UserDao userDao;

    public GroupDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
        groups = db.collection("groups");
        groupsNames = db.collection("groups_names");
        tags = db.collection("tags");
        timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", new Locale("es", "ES"));
    }

    @Override
    public void createGroup(GroupEntity entity) throws DatabaseAccessException {
        entity.setCreationDate(timeFormatter.format(LocalDateTime.now()));
        WriteBatch batch = db.batch();
        DocumentReference groupRef = groups.document();
        batch.set(groupRef, entity);
        String name = entity.getName();
        saveGroupName(name, groupRef.getId(), batch);
        String creator = entity.getCreator();
        saveUserAsMember(userDao.getUid(creator), creator, groupRef, batch);
        userDao.addGroupSubscription(creator, name, batch);
        List<String> tags = entity.getTags();
        for (String tag : tags) {
            addGroupToTag(tag, name, batch);
        }
        batch.commit();
    }

    @Override
    public void deleteGroup(String name) throws DatabaseAccessException {
        String id = getGroupId(name);
        WriteBatch batch = db.batch();
        deleteGroupFromAllTags(name, batch);
        deleteAllMembers(id, name, batch);
        DocumentReference groupRef = groups.document(id);
        batch.delete(groupRef);
        DocumentReference namesRef = groupsNames.document(name);
        batch.delete(namesRef);
        batch.commit();
    }

    @Override
    public Group getGroup(String name) throws DatabaseAccessException {
        String id = getGroupId(name);
        DocumentSnapshot snapshot = getDocumentSnapshot(groups, id);
        Group group = snapshot.toObject(Group.class);
        addMembersIfGroupNotNull(id, group);
        return group;
    }

    @Override
    public List<Group> getAllGroups() throws DatabaseAccessException {
        ApiFuture<QuerySnapshot> future = groups.get();
        try {
            List<Group> groupList = new ArrayList<>();
            List<QueryDocumentSnapshot> groupDocs = future.get().getDocuments();
            for (QueryDocumentSnapshot groupSnapshot : groupDocs) {
                Group group = groupSnapshot.toObject(Group.class);
                addMembersIfGroupNotNull(groupSnapshot.getId(), group);
                groupList.add(group);
            }
            return groupList;
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("list-error", "The groups could not be retrieved");
        }
    }

    @Override
    public void updateField(String name, String field, String newValue) throws DatabaseAccessException {
        String id = getGroupId(name);
        WriteBatch batch = db.batch();
        DocumentReference group = groups.document(id);
        Map<String, Object> data = new HashMap<>();
        data.put(field, newValue);
        batch.update(group, data);
        if ("name".equals(field)) {
            if (groupNameInUse(newValue)) {
                throw new DatabaseAccessException("invalid-group-name", "The name is already in use");
            }
            changeNameInTags(name, newValue, batch);
            changeNameInSubscription(name, newValue, batch);
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
        WriteBatch batch = db.batch();
        saveUserAsMember(userUid, username, groupRef, batch);
        userDao.addGroupSubscription(username, group, batch);
        batch.commit();
    }

    @Override
    public void unsubscribe(String group, String username) throws DatabaseAccessException {
        String userUid = userDao.getUid(username);
        String groupId = getGroupId(group);
        DocumentReference groupRef = groups.document(groupId);
        WriteBatch batch = db.batch();
        deleteUserFromMember(userUid, groupRef, batch);
        userDao.deleteGroupSubscription(userUid, group, batch);
        batch.commit();
    }

    @Override
    public void updateTags(String group, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException {
        String id = getGroupId(group);
        DocumentReference groupRef = groups.document(id);
        Map<String, Object> data = new HashMap<>();
        WriteBatch batch = db.batch();
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

    @Override
    public String getGroupId(String name) throws DatabaseAccessException {
        DocumentSnapshot snapshot = getDocumentSnapshot(groupsNames, name);
        return (String) snapshot.get("group");
    }

    /**
     * Saves the user as a member of the group.
     *
     * @param userUid The user's uid
     * @param username The user's username
     * @param groupRef The group document reference
     * @param batch The batch of writes to which it belongs
     */
    private void saveUserAsMember(String userUid, String username, DocumentReference groupRef, WriteBatch batch) {
        DocumentReference memberRef = groupRef.collection("members").document(userUid);
        Map<String, Object> data = new HashMap<>();
        data.put("user", username);
        batch.set(memberRef, data);
        data.put("date", timeFormatter.format(LocalDateTime.now()));
        batch.set(memberRef, data);
    }

    /**
     * Deletes a user membership.
     *
     * @param userUid The user uid
     * @param groupRef The group reference
     * @param batch The batch of writes to which it belongs
     */
    private void deleteUserFromMember(String userUid, DocumentReference groupRef, WriteBatch batch) {
        DocumentReference memberRef = groupRef.collection("members").document(userUid);
        batch.delete(memberRef);
    }

    /**
     * Adds the member list if the group is not null.
     *
     * @param id The group id
     * @param group The group
     */
    private void addMembersIfGroupNotNull(String id, Group group) {
        if (group != null) {
            Map<String, String> members = getGroupMembers(id);
            group.setMembers(members);
        }
    }

    /**
     * Gets the group members.
     *
     * @param id The group id
     * @return The map with the user and its subscription date
     */
    private Map<String, String> getGroupMembers(String id) {
        Map<String, String> members = new HashMap<>();
        ApiFuture<QuerySnapshot> future = groups.document(id).collection("members").get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                members.put((String) doc.get("user"), (String) doc.get("date"));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return members;
    }

    /**
     * Gets a document snapshot from a collection.
     *
     * @param collection The collection to which the document belongs
     * @param documentName The document name
     * @return The document snapshot
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private DocumentSnapshot getDocumentSnapshot(CollectionReference collection, String documentName)
        throws DatabaseAccessException {
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
     *
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
     *
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
     *
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
     * @param batch The batch of writes to which it belongs
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void deleteGroupFromAllTags(String group, WriteBatch batch) throws DatabaseAccessException {
        Query query = tags.whereArrayContains("groups", group);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            Map<String, Object> data = new HashMap<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                data.put("groups", FieldValue.arrayRemove(group));
                batch.update(document.getReference(), data);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("retrieval-failed", "Failure when retrieving the tags data");
        }
    }

    /**
     * Deletes the all group from members.
     *
     * @param id The group id
     * @param name The group name
     * @param batch The batch of writes to which it belongs
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void deleteAllMembers(String id, String name, WriteBatch batch) throws DatabaseAccessException {
        Iterable<DocumentReference> documents = groups.document(id).collection("members").listDocuments();
        for (DocumentReference doc : documents) {
            batch.delete(doc);
        }
        Query query = db.collection("users").whereArrayContains("groupSubscriptions", name);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            Map<String, Object> data = new HashMap<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                data.put("groupSubscriptions", FieldValue.arrayRemove(name));
                batch.update(document.getReference(), data);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("retrieval-failed", "Failure when retrieving the subscriptions data");
        }
    }

    /**
     * Updates the group name in all its tags.
     *
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

    /**
     * Changes the name on its subscribers collection.
     *
     * @param oldName The old name
     * @param newName The new name
     * @param batch The batch of writes to which it belongs
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void changeNameInSubscription(String oldName, String newName, WriteBatch batch)
        throws DatabaseAccessException {
        Query query = db.collection("users").whereArrayContains("groupSubscriptions", oldName);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            Map<String, Object> data = new HashMap<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                DocumentReference ref = document.getReference();
                data.put("groupSubscriptions", FieldValue.arrayRemove(oldName));
                batch.update(ref, data);
                data.put("groupSubscriptions", FieldValue.arrayUnion(newName));
                batch.update(ref, data);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("update-failed", "Failure when updating name in subscriptions");
        }
    }

    /**
     * Deletes the specified collection.
     *
     * @param collection The collection to delete
     * @param batch The batch of writes to which it belongs
     */
    private void deleteCollection(CollectionReference collection, WriteBatch batch) {
        Iterable<DocumentReference> membersDocRefs = collection.listDocuments();
        for (DocumentReference docRef : membersDocRefs) {
            batch.delete(docRef);
        }
    }
}
