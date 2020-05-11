package org.pesmypetcare.webservice.dao.communitymanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.entity.appmanager.ImageEntity;
import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.Message;
import org.pesmypetcare.webservice.entity.communitymanager.MessageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;
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
public class ForumDaoImpl implements ForumDao {
    private static final String TAGS_FIELD = "tags";
    private static final String FORUMS_FIELD = "forums";
    private static final String FORUM_FIELD = "forum";
    private final DateTimeFormatter timeFormatter;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private StorageDao storageDao;
    @Autowired
    private FirestoreDocument documentAdapter;
    @Autowired
    private FirestoreCollection collectionAdapter;

    public ForumDaoImpl() {
        timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", new Locale("es", "ES"));
    }

    @Override
    public boolean forumNameInUse(String parentGroup, String forumName) throws DatabaseAccessException {
        return documentAdapter.documentExists(Path.ofDocument(Collections.forumsNames, parentGroup, forumName));
    }

    @Override
    public void createForum(String parentGroup, ForumEntity forumEntity)
        throws DatabaseAccessException, DocumentException {
        forumEntity.setCreationDate(timeFormatter.format(LocalDateTime.now()));
        WriteBatch batch = documentAdapter.batch();
        String parentId = documentAdapter.getStringFromDocument(Path.ofDocument(Collections.groupsNames, parentGroup),
            "group");
        DocumentReference forumRef = documentAdapter.createDocument(Path.ofCollection(Collections.forums, parentId),
            forumEntity, batch);
        String name = forumEntity.getName();
        saveForumName(parentGroup, name, forumRef.getId(), batch);
        //String creator = forumEntity.getCreator();
        //saveUserAsMember(userDao.getUid(creator), creator, forumRef, batch);
        //userDao.addForumSubscription(creator, parentGroup, name, batch);
        List<String> tags = forumEntity.getTags();
        for (String tag : tags) {
            addForumToTag(tag, name, batch);
        }
        documentAdapter.commitBatch(batch);
    }

    @Override
    public void deleteForum(String parentGroup, String forumName) throws DatabaseAccessException, DocumentException {
        String groupId = groupDao.getGroupId(parentGroup);
        String forumId = getForumId(parentGroup, forumName);
        WriteBatch batch = documentAdapter.batch();
        deleteForumFromAllTags(forumName, batch);
        documentAdapter.deleteDocument(Path.ofDocument(Collections.forums, groupId, forumId), batch);
        documentAdapter.deleteDocument(Path.ofDocument(Collections.forumsNames, parentGroup, forumName), batch);
        documentAdapter.commitBatch(batch);
    }

    @Override
    public ForumEntity getForum(String parentGroup, String forumName)
        throws DatabaseAccessException, DocumentException {
        String groupId = groupDao.getGroupId(parentGroup);
        String forumId = getForumId(parentGroup, forumName);
        return documentAdapter.getDocumentDataAsObject(Path.ofDocument(Collections.forums, groupId, forumId),
            ForumEntity.class);
    }

    @Override
    public List<ForumEntity> getAllForumsFromGroup(String groupName) throws DatabaseAccessException, DocumentException {
        String groupId = groupDao.getGroupId(groupName);
        List<DocumentSnapshot> snapshots = collectionAdapter.listAllCollectionDocumentSnapshots(
            Path.ofCollection(Collections.forums, groupId));
        List<ForumEntity> forumList = new ArrayList<>();
        for (DocumentSnapshot forumSnapshot : snapshots) {
            forumList.add(forumSnapshot.toObject(ForumEntity.class));
        }
        return forumList;
    }

    @Override
    public void updateName(String parentGroup, String currentName, String newName)
        throws DatabaseAccessException, DocumentException {
        if (forumNameInUse(parentGroup, newName)) {
            throw new DatabaseAccessException("invalid-request", "The name is already in use");
        }
        WriteBatch batch = documentAdapter.batch();
        String forumId = getForumId(parentGroup, currentName);
        String groupId = groupDao.getGroupId(parentGroup);
        documentAdapter.updateDocumentFields(Path.ofDocument(Collections.forums, groupId, forumId), "name", newName,
            batch);
        changeNameInTags(currentName, newName, batch);
        documentAdapter.deleteDocument(Path.ofDocument(Collections.forumsNames, parentGroup, currentName), batch);
        saveForumName(parentGroup, newName, forumId, batch);
        documentAdapter.commitBatch(batch);
    }

    @Override
    public void updateTags(String parentGroup, String forumName, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException, DocumentException {
        String groupId = groupDao.getGroupId(parentGroup);
        String forumId = getForumId(parentGroup, forumName);
        WriteBatch batch = documentAdapter.batch();
        if (deletedTags != null) {
            removeDeletedTags(groupId, forumId, forumName, deletedTags, batch);
        }
        if (newTags != null) {
            addNewTags(groupId, forumId, forumName, newTags, batch);
        }
        documentAdapter.commitBatch(batch);
    }

    @Override
    public void postMessage(String parentGroup, String forumName, Message message)
        throws DatabaseAccessException, DocumentException {
        String groupId = groupDao.getGroupId(parentGroup);
        String forumId = getForumId(parentGroup, forumName);
        MessageEntity messageEntity = message.getMessage();
        messageEntity.setPublicationDate(timeFormatter.format(LocalDateTime.now()));
        WriteBatch batch = documentAdapter.batch();
        DocumentReference docRef = documentAdapter.createDocument(
            Path.ofCollection(Collections.messages, groupId, forumId), messageEntity, batch);
        ImageEntity imageEntity = message.getImage();
        if (imageEntity != null) {
            String imagePath = storageDao.uploadPostImage(parentGroup, forumName, imageEntity);
            documentAdapter.updateDocumentFields(batch, Path.ofDocument(Collections.messages, groupId, forumId,
                docRef.getId()), "imagePath", imagePath);
        }
        documentAdapter.commitBatch(batch);
    }

    @Override
    public void deleteMessage(String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException {
        String groupId = groupDao.getGroupId(parentGroup);
        String forumId = getForumId(parentGroup, forumName);
        try {
            ApiFuture<QuerySnapshot> querySnapshot = collectionAdapter.getDocumentsWhereEqualTo(
                Path.ofCollection(Collections.messages, groupId, forumId), "creator", creator, "publicationDate", date);
            WriteBatch batch = documentAdapter.batch();
            try {
                for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                    String imagePath = document.getString("imagePath");
                    if (imagePath != null) {
                        storageDao.deleteImageByName(imagePath);
                    }
                    batch.delete(document.getReference());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new DatabaseAccessException("message-deletion-failed", "Failure when deleting the message");
            }
            documentAdapter.commitBatch(batch);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the forum name in the collection of names of its parent group.
     *
     * @param parentGroup The name of the parent group
     * @param name The forum name
     * @param id The forum id
     * @param batch The batch where to write
     */
    private void saveForumName(String parentGroup, String name, String id, WriteBatch batch) {
        Map<String, Object> docData = new HashMap<>();
        docData.put(FORUM_FIELD, id);
        documentAdapter.createDocumentWithId(Path.ofCollection(Collections.forumsNames, parentGroup), name, docData,
            batch);
    }

    /**
     * Adds a forum to the list of forums of a tag.
     *
     * @param tag The tag
     * @param name The forum name
     * @param batch The batch where to write
     * @throws DatabaseAccessException When the access to the database fails
     */
    private void addForumToTag(String tag, String name, WriteBatch batch) throws DatabaseAccessException {
        Map<String, Object> data = new HashMap<>();
        data.put(FORUMS_FIELD, FieldValue.arrayUnion(name));
        String path = Path.ofDocument(Collections.tags, tag);
        try {
            documentAdapter.getDocumentSnapshot(path);
            documentAdapter.setDocumentFields(path, data, batch);
        } catch (DocumentException e) {
            documentAdapter.updateDocumentFields(path, data, batch);
        }
    }

    /**
     * Gets the forum ID.
     *
     * @param groupName The group name
     * @param forumName The forum name
     * @return The forum ID
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private String getForumId(String groupName, String forumName) throws DatabaseAccessException, DocumentException {
        return documentAdapter.getStringFromDocument(Path.ofDocument(Collections.forumsNames, groupName, forumName),
            FORUM_FIELD);
    }

    /**
     * Deletes the forum from all the forum lists of tags it uses.
     *
     * @param forum The forum name
     * @param batch The batch where to write
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     */
    private void deleteForumFromAllTags(String forum, WriteBatch batch) throws DatabaseAccessException {
        try {
            ApiFuture<QuerySnapshot> querySnapshot = collectionAdapter.getDocumentsWhereArrayContains(
                Path.ofCollection(Collections.tags), FORUMS_FIELD, forum);
            try {
                Map<String, Object> data = new HashMap<>();
                for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                    data.put(FORUMS_FIELD, FieldValue.arrayRemove(forum));
                    batch.update(document.getReference(), data);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new DatabaseAccessException("retrieval-failed", "Failure when retrieving the tags data");
            }
        } catch (IllegalArgumentException ignore) {
        }
    }

    /**
     * Updates the forum name in all the forum lists of tags it uses.
     *
     * @param currentName The current name
     * @param newName The new name
     * @param batch The batch where to write
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     */
    private void changeNameInTags(String currentName, String newName, WriteBatch batch) throws DatabaseAccessException {
        try {
            ApiFuture<QuerySnapshot> querySnapshot = collectionAdapter.getDocumentsWhereArrayContains(
                Path.ofCollection(Collections.tags), FORUMS_FIELD, currentName);
            try {
                for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                    deleteForumFromTag(document.getId(), currentName, batch);
                    addForumToTag(document.getId(), newName, batch);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new DatabaseAccessException("retrieval-failed", "Failure when retrieving the tags data");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an entry in the tag collection for the forum.
     *
     * @param tag The tag
     * @param forum The forum name
     * @param batch The batch of writes to which it belongs
     */
    private void deleteForumFromTag(String tag, String forum, WriteBatch batch) {
        documentAdapter.updateDocumentFields(Path.ofDocument(Collections.tags, tag), FORUMS_FIELD,
            FieldValue.arrayRemove(forum), batch);
    }

    /**
     * Adds a list of new tags to a forum.
     *
     * @param groupId The parent group ID
     * @param forumId The forum ID
     * @param forumName The forum name
     * @param newTags The list of tags to add
     * @param batch The batch where to write
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     */
    private void addNewTags(String groupId, String forumId, String forumName, List<String> newTags, WriteBatch batch)
        throws DatabaseAccessException {
        documentAdapter.updateDocumentFields(Path.ofDocument(Collections.forums, groupId, forumId), TAGS_FIELD,
            FieldValue.arrayUnion(newTags.toArray()), batch);
        for (String tag : newTags) {
            addForumToTag(tag, forumName, batch);
        }
    }

    /**
     * Deletes a list of new tags to a forum.
     *
     * @param groupId The parent group ID
     * @param forumId The forum ID
     * @param forumName The forum name
     * @param deletedTags The list of tags to delete
     * @param batch The batch where to write
     */
    private void removeDeletedTags(String groupId, String forumId, String forumName, List<String> deletedTags,
                                   WriteBatch batch) {
        documentAdapter.updateDocumentFields(Path.ofDocument(Collections.forums, groupId, forumId), TAGS_FIELD,
            FieldValue.arrayRemove(deletedTags.toArray()), batch);
        for (String tag : deletedTags) {
            deleteForumFromTag(tag, forumName, batch);
        }
    }
}
