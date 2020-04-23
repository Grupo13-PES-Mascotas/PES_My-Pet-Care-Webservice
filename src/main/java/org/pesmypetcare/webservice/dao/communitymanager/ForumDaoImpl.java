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
import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * @author Santiago Del Rey
 */
@Repository
public class ForumDaoImpl implements ForumDao {
    private final DateTimeFormatter timeFormatter;
    private CollectionReference groups;
    private CollectionReference groupsNames;
    private CollectionReference tags;
    private Firestore db;

    @Autowired
    private UserDao userDao;
    @Autowired
    private GroupDao groupDao;

    public ForumDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
        groups = db.collection("groups");
        groupsNames = db.collection("groups_names");
        tags = db.collection("tags");
        timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", new Locale("es", "ES"));
    }

    @Override
    public boolean forumNameInUse(String parentGroup, String forumName) throws DatabaseAccessException {
        CollectionReference forumsNames = groupsNames.document(parentGroup).collection("forums");
        DocumentSnapshot snapshot = getDocumentSnapshot(forumsNames, forumName);
        return snapshot.exists();
    }

    @Override
    public void createForum(String parentGroup, ForumEntity forumEntity) throws DatabaseAccessException {
        forumEntity.setCreationDate(timeFormatter.format(LocalDateTime.now()));
        WriteBatch batch = db.batch();
        DocumentSnapshot parentSnapshot = getDocumentSnapshot(groupsNames, parentGroup);
        if (parentSnapshot.exists()) {
            String parentId = (String) parentSnapshot.get("group");
            DocumentReference forumRef = groups.document(Objects.requireNonNull(parentId)).collection("forums").document();
            batch.set(forumRef, forumEntity);
            String name = forumEntity.getName();
            saveForumName(parentGroup, name, forumRef.getId(), batch);
            //String creator = forumEntity.getCreator();
            //saveUserAsMember(userDao.getUid(creator), creator, forumRef, batch);
            //userDao.addForumSubscription(creator, parentGroup, name, batch);
            List<String> tags = forumEntity.getTags();
            for (String tag : tags) {
                addForumToTag(tag, name, batch);
            }
            batch.commit();
        }
    }

    @Override
    public void deleteForum(String parentGroup, String forumName) throws DatabaseAccessException {
        String groupId = groupDao.getGroupId(parentGroup);
        String forumId = getForumId(parentGroup, forumName);
        WriteBatch batch = db.batch();
        deleteForumFromAllTags(forumName, batch);
        DocumentReference forumRef = groups.document(groupId).collection("forums").document(forumId);
        batch.delete(forumRef);
        DocumentReference namesRef = groupsNames.document(parentGroup).collection("forums").document(forumName);
        batch.delete(namesRef);
        batch.commit();
    }

    @Override
    public ForumEntity getForum(String parentGroup, String forumName) throws DatabaseAccessException {
        String groupId = groupDao.getGroupId(parentGroup);
        String forumId = getForumId(parentGroup, forumName);
        CollectionReference forums = groups.document(groupId).collection("forums");
        DocumentSnapshot snapshot = getDocumentSnapshot(forums, forumId);
        return snapshot.toObject(ForumEntity.class);
    }

    @Override
    public List<ForumEntity> getAllForumsFromGroup(String parentGroup) throws DatabaseAccessException {
        String groupId = groupDao.getGroupId(parentGroup);
        ApiFuture<QuerySnapshot> future = groups.document(groupId).collection("forums").get();
        try {
            List<ForumEntity> forumList = new ArrayList<>();
            List<QueryDocumentSnapshot> forumDocs = future.get().getDocuments();
            for (QueryDocumentSnapshot forumSnapshot : forumDocs) {
                forumList.add(forumSnapshot.toObject(ForumEntity.class));
            }
            return forumList;
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("list-error", "The forums could not be retrieved");
        }
    }

    @Override
    public void updateName(String parentGroup, String currentName, String newName) throws DatabaseAccessException {
        if (forumNameInUse(parentGroup, newName)) {
            throw new DatabaseAccessException("invalid-forum-name", "The name is already in use");
        }
        WriteBatch batch = db.batch();
        String forumId = getForumId(parentGroup, currentName);
        String groupId = groupDao.getGroupId(parentGroup);
        DocumentReference group = groups.document(groupId).collection("forums").document(forumId);
        Map<String, Object> data= new HashMap<>();
        data.put("name", newName);
        batch.update(group, data);
        changeNameInTags(currentName, newName, batch);
        DocumentReference namesRef = groupsNames.document(parentGroup).collection("forums").document(currentName);
        batch.delete(namesRef);
        saveForumName(parentGroup, newName, forumId, batch);
        batch.commit();
    }

    @Override
    public void updateTags(String parentGroup, String forumName, List<String> newTags, List<String> deletedTags) throws DatabaseAccessException {
        String groupId = groupDao.getGroupId(parentGroup);
        String forumId = getForumId(parentGroup, forumName);
        DocumentReference forumRef = groups.document(groupId).collection("forums").document(forumId);
        WriteBatch batch = db.batch();
        if (deletedTags != null) {
            removeDeletedTags(forumName, deletedTags, forumRef, batch);
        }
        if (newTags != null) {
            addNewTags(forumName, newTags, forumRef, batch);
        }
        batch.commit();
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

    private void saveForumName(String parentGroup, String name, String id, WriteBatch batch) {
        DocumentReference namesRef = groupsNames.document(parentGroup).collection("forums").document(name);
        Map<String, Object> docData = new HashMap<>();
        docData.put("forum", id);
        batch.set(namesRef, docData);
    }

    private void saveUserAsMember(String userUid, String username, DocumentReference forumRef, WriteBatch batch) {
        DocumentReference memberRef = forumRef.collection("members").document(userUid);
        Map<String, Object> data = new HashMap<>();
        data.put("user", username);
        batch.set(memberRef, data);
        data.put("date", timeFormatter.format(LocalDateTime.now()));
        batch.set(memberRef, data);
    }

    private void addForumToTag(String tag, String name, WriteBatch batch) throws DatabaseAccessException {
        DocumentSnapshot snapshot = getDocumentSnapshot(tags, tag);
        DocumentReference tagRef = tags.document(tag);
        Map<String, Object> data = new HashMap<>();
        data.put("forums", FieldValue.arrayUnion(name));
        if (!snapshot.exists()) {
            batch.set(tagRef, data);
        } else {
            batch.update(tagRef, data);
        }
    }

    private String getForumId(String groupName, String forumName) throws DatabaseAccessException {
        CollectionReference groupRef = groupsNames.document(groupName).collection("forums");
        DocumentSnapshot snapshot = getDocumentSnapshot(groupRef, forumName);
        return  (String) snapshot.get("forum");
    }

    private void deleteForumFromAllTags(String forum, WriteBatch batch) throws DatabaseAccessException {
        Query query = tags.whereArrayContains("forums", forum);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            Map<String, Object> data = new HashMap<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                data.put("forums", FieldValue.arrayRemove(forum));
                batch.update(document.getReference(), data);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("retrieval-failed", "Failure when retrieving the tags data");
        }
    }

    private void changeNameInTags(String currentName, String newName, WriteBatch batch) throws DatabaseAccessException {
        Query query = tags.whereArrayContains("forums", currentName);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                deleteForumFromTag(document.getId(), currentName, batch);
                addForumToTag(document.getId(), newName, batch);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("retrieval-failed", "Failure when retrieving the tags data");
        }
    }

    /**
     * Deletes an entry in the tag collection for the forum.
     * @param tag The tag
     * @param forum The forum name
     * @param batch The batch of writes to which it belongs
     */
    private void deleteForumFromTag(String tag, String forum, WriteBatch batch) {
        DocumentReference tagRef = tags.document(tag);
        Map<String, Object> data = new HashMap<>();
        data.put("forums", FieldValue.arrayRemove(forum));
        batch.update(tagRef, data);
    }

    private void addNewTags(String forumName, List<String> newTags, DocumentReference forumRef, WriteBatch batch) throws DatabaseAccessException {
        Map<String, Object> data = new HashMap<>();
        data.put("tags", FieldValue.arrayUnion(newTags.toArray()));
        batch.update(forumRef, data);
        for (String tag : newTags) {
            addForumToTag(tag, forumName, batch);
        }
    }

    private void removeDeletedTags(String forumName, List<String> deletedTags, DocumentReference forumRef,
                                   WriteBatch batch) {
        Map<String, Object> data = new HashMap<>();
        data.put("tags", FieldValue.arrayRemove(deletedTags.toArray()));
        batch.update(forumRef, data);
        for (String tag : deletedTags) {
            deleteForumFromTag(tag, forumName, batch);
        }
    }
}
