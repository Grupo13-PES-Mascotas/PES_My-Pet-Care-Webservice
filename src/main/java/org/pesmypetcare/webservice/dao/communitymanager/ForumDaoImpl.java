package org.pesmypetcare.webservice.dao.communitymanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.dao.usermanager.UserDao;
import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.Message;
import org.pesmypetcare.webservice.entity.communitymanager.MessageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.error.InvalidOperationException;
import org.pesmypetcare.webservice.thirdpartyservices.FirebaseFactory;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;
import org.pesmypetcare.webservice.utilities.UTCLocalConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static final String LIKED_BY_FIELD = "likedBy";
    private static final String REPORTED_BY_FIELD = "reportedBy";
    private static final String BANNED_FIELD = "banned";
    private static final String GROUP_FIELD = "group";
    private static final int COUNTER = 3;
    private FirebaseMessaging firebaseMessaging;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private StorageDao storageDao;
    @Autowired
    private FirestoreDocument documentAdapter;
    @Autowired
    private FirestoreCollection collectionAdapter;

    public ForumDaoImpl() {
        firebaseMessaging = FirebaseFactory.getInstance().getFirebaseMessaging();
    }

    @Override
    public boolean forumNameInUse(String parentGroup, String forumName) throws DatabaseAccessException {
        return documentAdapter.documentExists(Path.ofDocument(Collections.forum_names, parentGroup, forumName));
    }

    @Override
    public void createForum(String parentGroup, ForumEntity forumEntity)
        throws DatabaseAccessException, DocumentException {
        forumEntity.setCreationDate(UTCLocalConverter.getCurrentUTC());
        WriteBatch batch = documentAdapter.batch();
        String parentId = documentAdapter
            .getStringFromDocument(Path.ofDocument(Collections.groups_names, parentGroup), GROUP_FIELD);
        DocumentReference forumRef = documentAdapter
            .createDocument(Path.ofCollection(Collections.forums, parentId), forumEntity, batch);
        String name = forumEntity.getName();
        saveForumName(parentGroup, name, forumRef.getId(), batch);
        //String creator = forumEntity.getCreator();
        //saveUserAsMember(userDao.getUid(creator), creator, forumRef, batch);
        //userDao.addForumSubscription(creator, parentGroup, name, batch);
        List<String> tags = forumEntity.getTags();
        if (tags != null) {
            for (String tag : tags) {
                addForumToTag(tag, name, batch);
            }
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
        documentAdapter.deleteDocument(Path.ofDocument(Collections.forum_names, parentGroup, forumName), batch);
        documentAdapter.commitBatch(batch);
    }

    @Override
    public ForumEntity getForum(String parentGroup, String forumName)
        throws DatabaseAccessException, DocumentException {
        String groupId = groupDao.getGroupId(parentGroup);
        String forumId = getForumId(parentGroup, forumName);
        return documentAdapter
            .getDocumentDataAsObject(Path.ofDocument(Collections.forums, groupId, forumId), ForumEntity.class);
    }

    @Override
    public List<ForumEntity> getAllForumsFromGroup(String groupName) throws DatabaseAccessException, DocumentException {
        String groupId = groupDao.getGroupId(groupName);
        List<DocumentSnapshot> snapshots = collectionAdapter
            .listAllCollectionDocumentSnapshots(Path.ofCollection(Collections.forums, groupId));
        List<ForumEntity> forumList = new ArrayList<>();
        for (DocumentSnapshot forumSnapshot : snapshots) {
            forumList.add(forumSnapshot.toObject(ForumEntity.class));
        }
        return forumList;
    }

    @Override
    public void updateName(UserToken userToken, String parentGroup, String currentName, String newName)
        throws DatabaseAccessException, DocumentException {
        if (forumNameInUse(parentGroup, newName)) {
            throw new DocumentException("document-already-exists", "The forum name is already in use");
        }
        String forumId = getForumId(parentGroup, currentName);
        String groupId = groupDao.getGroupId(parentGroup);
        checkIfUserIsForumCreator(userToken, forumId, groupId);
        WriteBatch batch = documentAdapter.batch();
        documentAdapter
            .updateDocumentFields(Path.ofDocument(Collections.forums, groupId, forumId), "name", newName, batch);
        changeNameInTags(currentName, newName, batch);
        documentAdapter.deleteDocument(Path.ofDocument(Collections.forum_names, parentGroup, currentName), batch);
        saveForumName(parentGroup, newName, forumId, batch);
        documentAdapter.commitBatch(batch);
    }

    @Override
    public void updateTags(UserToken userToken, String parentGroup, String forumName, List<String> newTags,
                           List<String> deletedTags) throws DatabaseAccessException, DocumentException {
        String groupId = groupDao.getGroupId(parentGroup);
        String forumId = getForumId(parentGroup, forumName);
        checkIfUserIsForumCreator(userToken, forumId, groupId);
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
        MessageEntity messageEntity = new MessageEntity(message);
        WriteBatch batch = documentAdapter.batch();
        documentAdapter.createDocument(Path.ofCollection(Collections.messages, groupId, forumId), messageEntity, batch);
        documentAdapter.commitBatch(batch);
        sendNotificationToSubscribers(groupId, parentGroup, forumName, message);
    }

    @Override
    public void deleteMessage(String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException {
        DocumentSnapshot messageSnapshot = getForumMessage(parentGroup, forumName, creator, date);
        WriteBatch batch = documentAdapter.batch();
        String imagePath = messageSnapshot.getString("imagePath");
        if (imagePath != null) {
            storageDao.deleteImageByName(imagePath);
        }
        batch.delete(messageSnapshot.getReference());
        documentAdapter.commitBatch(batch);
    }

    @Override
    public void reportMessage(String reporter, String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException, InvalidOperationException {
        if (creator.equals(reporter)) {
            throw new InvalidOperationException("409", "A message creator can't report its message");
        }
        DocumentSnapshot messageSnapshot = getForumMessage(parentGroup, forumName, creator, date);
        WriteBatch batch = documentAdapter.batch();
        batch.update(messageSnapshot.getReference(), REPORTED_BY_FIELD, FieldValue.arrayUnion(reporter));
        ArrayList<String> messages = (ArrayList<String>) messageSnapshot.get(REPORTED_BY_FIELD);
        if (messages != null && messages.contains(reporter)) {
            throw new InvalidOperationException("409", "This user already reported the message");
        }
        if (messages != null && messages.size() > COUNTER - 1) {
            batch.update(messageSnapshot.getReference(), BANNED_FIELD, true);
        }
        documentAdapter.commitBatch(batch);
    }

    @Override
    public void unbanMessage(UserToken token, String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException {
        String forumId = getForumId(parentGroup, forumName);
        String groupId = groupDao.getGroupId(parentGroup);
        checkIfUserIsForumCreator(token, forumId, groupId);
        DocumentSnapshot messageSnapshot = getForumMessage(parentGroup, forumName, creator, date);
        WriteBatch batch = documentAdapter.batch();
        batch.update(messageSnapshot.getReference(), REPORTED_BY_FIELD, new ArrayList<String>());
        batch.update(messageSnapshot.getReference(), BANNED_FIELD, false);
        documentAdapter.commitBatch(batch);
    }

    @Override
    public List<String> getAllPostImagesPaths(String group, String forum)
        throws DatabaseAccessException, DocumentException {
        String groupId = groupDao.getGroupId(group);
        String forumId = getForumId(group, forum);
        List<DocumentSnapshot> messages = collectionAdapter
            .listAllCollectionDocumentSnapshots(Path.ofCollection(Collections.messages, groupId, forumId));
        List<String> imagesPaths = new ArrayList<>();
        for (DocumentSnapshot message : messages) {
            String imagePath = message.getString("imagePath");
            if (imagePath != null) {
                imagesPaths.add(imagePath);
            }
        }
        return imagesPaths;
    }

    @Override
    public void addUserToLikedByOfMessage(String username, String parentGroup, String forumName, String creator,
                                          String date) throws DatabaseAccessException, DocumentException {
        DocumentSnapshot messageSnapshot = getForumMessage(parentGroup, forumName, creator, date);
        WriteBatch batch = documentAdapter.batch();
        batch.update(messageSnapshot.getReference(), LIKED_BY_FIELD, FieldValue.arrayUnion(username));
        documentAdapter.commitBatch(batch);
    }

    @Override
    public void removeUserFromLikedByOfMessage(String username, String parentGroup, String forumName, String creator,
                                               String date) throws DatabaseAccessException, DocumentException {
        DocumentSnapshot messageSnapshot = getForumMessage(parentGroup, forumName, creator, date);
        WriteBatch batch = documentAdapter.batch();
        batch.update(messageSnapshot.getReference(), LIKED_BY_FIELD, FieldValue.arrayRemove(username));
        documentAdapter.commitBatch(batch);
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
        documentAdapter
            .createDocumentWithId(Path.ofCollection(Collections.forum_names, parentGroup), name, docData, batch);
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
        if (documentAdapter.documentExists(path)) {
            documentAdapter.updateDocumentFields(path, data, batch);
        } else {
            documentAdapter.setDocumentFields(path, data, batch);
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
        return documentAdapter
            .getStringFromDocument(Path.ofDocument(Collections.forum_names, groupName, forumName), FORUM_FIELD);
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
            ApiFuture<QuerySnapshot> querySnapshot = collectionAdapter
                .getDocumentsWhereArrayContains(Path.ofCollection(Collections.tags), FORUMS_FIELD, forum);
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
            ApiFuture<QuerySnapshot> querySnapshot = collectionAdapter
                .getDocumentsWhereArrayContains(Path.ofCollection(Collections.tags), FORUMS_FIELD, currentName);
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
        documentAdapter
            .updateDocumentFields(Path.ofDocument(Collections.tags, tag), FORUMS_FIELD, FieldValue.arrayRemove(forum),
                batch);
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

    /**
     * Sends a multicast notification to all the users subscribed to the group notifications.
     *
     * @param groupId The ID of the group where the forum belong
     * @param groupName The name of the group where the forum belongs
     * @param forumName The name of the forum where the message belongs
     * @param message The message to be notified
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    private void sendNotificationToSubscribers(String groupId, String groupName, String forumName, Message message)
        throws DatabaseAccessException, DocumentException {
        List<String> deviceTokens = (List<String>) documentAdapter
            .getDocumentField(Path.ofDocument(Collections.groups, groupId), "notification-tokens");
        String userUid = userDao.getUid(message.getCreator());
        String userFcmToken = userDao.getFcmToken(userUid);
        if (deviceTokens != null) {
            if (!deviceTokens.isEmpty()) {
                try {
                    deviceTokens.remove(userFcmToken);
                    MulticastMessage multicastMessage = buildMulticastMessage(groupName, forumName, message,
                        deviceTokens);
                    firebaseMessaging.sendMulticast(multicastMessage);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Builds a notification multicast message.
     *
     * @param groupName The name of the group where the forum belongs
     * @param forumName The name of the forum where the message belongs
     * @param message The message to be notified
     * @param deviceTokens The list of device tokes to which send the notification
     * @return The multicast message
     */
    private MulticastMessage buildMulticastMessage(String groupName, String forumName, Message message,
                                                   List<String> deviceTokens) {
        Map<String, String> notificationData = new HashMap<>();
        notificationData.put(GROUP_FIELD, groupName);
        notificationData.put(FORUM_FIELD, forumName);
        notificationData.put("creator", message.getCreator());
        return MulticastMessage.builder().putAllData(notificationData).addAllTokens(deviceTokens).build();
    }

    /**
     * Gets a forum message.
     *
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's username
     * @param date The publication date
     * @return The document snapshot of the message
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    private DocumentSnapshot getForumMessage(String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException {
        String groupId = groupDao.getGroupId(parentGroup);
        String forumId = getForumId(parentGroup, forumName);
        try {
            List<QueryDocumentSnapshot> messagesQuery = collectionAdapter
                .getDocumentsWhereEqualTo(Path.ofCollection(Collections.messages, groupId, forumId), "creator",
                    creator, "publicationDate", date).get().getDocuments();
            if (messagesQuery.size() == 0) {
                throw new DatabaseAccessException("404", "Message Not Found");
            }
            return messagesQuery.get(0);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("retrieval-failed", "Failure when retrieving the message");
        }
    }

    /**
     * Checks if a user is the creator of a the forum.
     * @param token The token that contains the user information
     * @param forumId The forum ID
     * @param groupId The group ID
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    private void checkIfUserIsForumCreator(UserToken token, String forumId, String groupId)
        throws DatabaseAccessException, DocumentException {
        String forumCreator = documentAdapter
            .getStringFromDocument(Path.ofDocument(Collections.forums, groupId, forumId), "creator");
        if (!token.getUsername().equals(forumCreator)) {
            throw new BadCredentialsException("The user is not the creator of this forum.");
        }
    }
}
