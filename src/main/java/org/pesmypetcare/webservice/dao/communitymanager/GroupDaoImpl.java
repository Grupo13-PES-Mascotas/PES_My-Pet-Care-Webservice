package org.pesmypetcare.webservice.dao.communitymanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.dao.usermanager.UserDao;
import org.pesmypetcare.webservice.entity.communitymanager.Group;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.entity.communitymanager.TagEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;
import org.pesmypetcare.webservice.utilities.UTCLocalConverter;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GroupDaoImpl implements GroupDao {
    private static final String FIELD_GROUP = "group";
    private static final String FIELD_GROUPS = "groups";
    private static final String FIELD_TAGS = "tags";
    private static final String GROUP_SUBSCRIPTIONS_FIELDS = "groupSubscriptions";
    private static final String NOTIFICATIONS_FIELD = "notification-tokens";
    private static final String FCM_FIELD = "FCM";
    private static final String DATE_FIELD = "date";

    @Autowired
    private UserDao userDao;
    @Autowired
    private StorageDao storageDao;
    @Autowired
    private FirestoreDocument documentAdapter;
    @Autowired
    private FirestoreCollection collectionAdapter;

    @Override
    public void createGroup(GroupEntity entity) throws DatabaseAccessException, DocumentException {
        entity.setCreationDate(UTCLocalConverter.getCurrentUTC());
        WriteBatch batch = documentAdapter.batch();
        DocumentReference groupRef = documentAdapter
            .createDocument(Path.ofCollection(Collections.groups), entity, batch);
        String name = entity.getName();
        String groupId = groupRef.getId();
        saveGroupName(name, groupId, batch);
        String creator = entity.getCreator();
        String userUid = userDao.getUid(creator);
        saveUserAsMember(groupId, userUid, creator, batch);
        userDao.addGroupSubscription(creator, name, batch);
        List<String> tags = entity.getTags();
        if (tags != null) {
            for (String tag : tags) {
                addGroupToTag(tag, name, batch);
            }
        }
        addUserToGroupNotifications(userUid, groupId, batch);
        documentAdapter.commitBatch(batch);
    }

    @Override
    public void deleteGroup(String name) throws DatabaseAccessException, DocumentException {
        String id = getGroupId(name);
        WriteBatch batch = documentAdapter.batch();
        deleteGroupFromAllTags(name, batch);
        deleteAllMembers(name, batch);
        deleteGroupIcon(id);
        documentAdapter.deleteDocument(Path.ofDocument(Collections.groups, id), batch);
        documentAdapter.deleteDocument(Path.ofDocument(Collections.groups_names, name), batch);
        documentAdapter.commitBatch(batch);
    }

    @Override
    public Group getGroup(String name) throws DatabaseAccessException, DocumentException {
        String id = getGroupId(name);
        Group group = documentAdapter.getDocumentDataAsObject(Path.ofDocument(Collections.groups, id), Group.class);
        addMembersIfGroupNotNull(id, group);
        return group;
    }

    @Override
    public List<Group> getAllGroups() throws DatabaseAccessException {
        List<DocumentSnapshot> documentSnapshots = collectionAdapter
            .listAllCollectionDocumentSnapshots(Path.ofCollection(Collections.groups));
        List<Group> groupList = new ArrayList<>();
        for (DocumentSnapshot document : documentSnapshots) {
            Group group = document.toObject(Group.class);
            addMembersIfGroupNotNull(document.getId(), group);
            groupList.add(group);
        }
        return groupList;
    }

    @Override
    public void updateField(String name, String field, Object newValue)
        throws DatabaseAccessException, DocumentException {
        String id = getGroupId(name);
        WriteBatch batch = documentAdapter.batch();
        documentAdapter.updateDocumentFields(batch, Path.ofDocument(Collections.groups, id), field, newValue);
        if ("name".equals(field)) {
            if (groupNameInUse((String) newValue)) {
                throw new DocumentException("document-already-exists", "The group name is already in use");
            }
            changeNameInTags(name, (String) newValue, batch);
            changeNameInSubscription(name, (String) newValue, batch);
            documentAdapter.deleteDocument(Path.ofDocument(Collections.groups_names, name), batch);
            saveGroupName((String) newValue, id, batch);
        }
        documentAdapter.commitBatch(batch);
    }

    @Override
    public boolean groupNameInUse(String name) throws DatabaseAccessException {
        return documentAdapter.documentExists(Path.ofDocument(Collections.groups_names, name));
    }

    @Override
    public void subscribe(String group, String username) throws DatabaseAccessException, DocumentException {
        String userUid = userDao.getUid(username);
        String groupId = getGroupId(group);
        WriteBatch batch = documentAdapter.batch();
        saveUserAsMember(groupId, userUid, username, batch);
        userDao.addGroupSubscription(username, group, batch);
        addUserToGroupNotifications(userUid, groupId, batch);
        documentAdapter.commitBatch(batch);
    }

    @Override
    public void unsubscribe(String group, String username) throws DatabaseAccessException, DocumentException {
        String userUid = userDao.getUid(username);
        String groupId = getGroupId(group);
        WriteBatch batch = documentAdapter.batch();
        deleteUserFromMember(groupId, userUid, batch);
        userDao.deleteGroupSubscription(userUid, group, batch);
        removeUserFromGroupNotifications(userUid, groupId, batch);
        documentAdapter.commitBatch(batch);
    }

    @Override
    public void updateTags(String group, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException, DocumentException {
        String id = getGroupId(group);
        WriteBatch batch = documentAdapter.batch();
        if (deletedTags != null) {
            deleteGroupTags(group, id, deletedTags, batch);
        }
        if (newTags != null) {
            addTagsToGroup(group, id, newTags, batch);
        }
        documentAdapter.commitBatch(batch);
    }

    @Override
    public Map<String, TagEntity> getAllTags() throws DatabaseAccessException {
        List<DocumentSnapshot> tags = collectionAdapter
            .listAllCollectionDocumentSnapshots(Path.ofCollection(Collections.tags));
        Map<String, TagEntity> tagsMap = new HashMap<>();
        for (DocumentSnapshot tag : tags) {
            tagsMap.put(tag.getId(), tag.toObject(TagEntity.class));
        }
        return tagsMap;
    }

    @Override
    public String getGroupId(String name) throws DatabaseAccessException, DocumentException {
        return documentAdapter.getStringFromDocument(Path.ofDocument(Collections.groups_names, name), FIELD_GROUP);
    }

    /**
     * Saves the user as a member of the group.
     *
     * @param groupId The group ID
     * @param userUid The user's uid
     * @param username The user's username
     * @param batch The batch where to write
     */
    private void saveUserAsMember(String groupId, String userUid, String username, WriteBatch batch) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", username);
        data.put(DATE_FIELD, UTCLocalConverter.getCurrentUTC());
        documentAdapter.createDocumentWithId(Path.ofCollection(Collections.members, groupId), userUid, data, batch);
    }

    /**
     * Adds a user to the group notification list.
     * @param userUid The user's UID
     * @param groupId The group ID
     * @param batch The batch where to write
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the group does not exist
     */
    private void addUserToGroupNotifications(String userUid, String groupId, WriteBatch batch)
        throws DatabaseAccessException, DocumentException {
        String token = documentAdapter.getStringFromDocument(Path.ofDocument(Collections.users, userUid), FCM_FIELD);
        documentAdapter
            .updateDocumentFields(batch, Path.ofDocument(Collections.groups, groupId), NOTIFICATIONS_FIELD,
                FieldValue.arrayUnion(token));
    }

    /**
     * Removes a user from the group notification list.
     * @param userUid The user's UID
     * @param groupId The group ID
     * @param batch The batch where to write
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the group does not exist
     */
    private void removeUserFromGroupNotifications(String userUid, String groupId, WriteBatch batch)
        throws DatabaseAccessException, DocumentException {
        String token = documentAdapter.getStringFromDocument(Path.ofDocument(Collections.users, userUid), FCM_FIELD);
        documentAdapter
            .updateDocumentFields(batch, Path.ofDocument(Collections.groups, groupId), NOTIFICATIONS_FIELD,
                FieldValue.arrayRemove(token));
    }

    /**
     * Deletes a user membership.
     *
     * @param groupId The group reference
     * @param userUid The user uid
     * @param batch The batch where to write
     */
    private void deleteUserFromMember(String groupId, String userUid, WriteBatch batch) {
        documentAdapter.deleteDocument(Path.ofDocument(Collections.members, groupId, userUid), batch);
    }

    /**
     * Deletes the group icon from the storage.
     *
     * @param id The group ID
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the group does not exist
     */
    private void deleteGroupIcon(String id) throws DatabaseAccessException, DocumentException {
        String path = (String) documentAdapter
            .getDocumentField(Path.ofDocument(Collections.groups, id), FieldPath.of("icon", "path"));
        if (path != null) {
            storageDao.deleteImageByName(path);
        }
    }

    /**
     * Adds the member list if the group is not null.
     *
     * @param id The group id
     * @param group The group
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void addMembersIfGroupNotNull(String id, Group group) throws DatabaseAccessException {
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
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private Map<String, String> getGroupMembers(String id) throws DatabaseAccessException {
        Map<String, String> members = new HashMap<>();
        List<DocumentSnapshot> membersList = collectionAdapter
            .listAllCollectionDocumentSnapshots(Path.ofCollection(Collections.members, id));
        for (DocumentSnapshot member : membersList) {
            members.put(member.getString("user"), member.getString(DATE_FIELD));
        }
        return members;
    }

    /**
     * Saves the group name in database.
     *
     * @param name The group name
     * @param groupId The group id to which the name belongs
     * @param batch The batch where to write
     */
    private void saveGroupName(String name, String groupId, WriteBatch batch) {
        Map<String, String> docData = new HashMap<>();
        docData.put(FIELD_GROUP, groupId);
        documentAdapter.createDocumentWithId(Path.ofCollection(Collections.groups_names), name, docData, batch);
    }

    /**
     * Creates an entry in the tag collection for the group.
     *
     * @param tag The tag
     * @param groupId The group id
     * @param batch The batch where to write
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void addGroupToTag(String tag, String groupId, WriteBatch batch) throws DatabaseAccessException {
        if (documentAdapter.documentExists(Path.ofDocument(Collections.tags, tag))) {
            documentAdapter.updateDocumentFields(batch, Path.ofDocument(Collections.tags, tag), FIELD_GROUPS,
                FieldValue.arrayUnion(groupId));
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put(FIELD_GROUPS, FieldValue.arrayUnion(groupId));
            documentAdapter.createDocumentWithId(Path.ofCollection(Collections.tags), tag, data, batch);
        }
    }

    /**
     * Deletes an entry in the tag collection for the group.
     *
     * @param tag The tag
     * @param groupId The group id
     * @param batch The batch where to write
     */
    private void deleteGroupFromTag(String tag, String groupId, WriteBatch batch) {
        documentAdapter.updateDocumentFields(batch, Path.ofDocument(Collections.tags, tag), FIELD_GROUPS,
            FieldValue.arrayRemove(groupId));
    }

    /**
     * Deletes a group from all its tags.
     *
     * @param group The group name
     * @param batch The batch where to write
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void deleteGroupFromAllTags(String group, WriteBatch batch) throws DatabaseAccessException {
        ApiFuture<QuerySnapshot> querySnapshot = collectionAdapter
            .getDocumentsWhereArrayContains(Path.ofCollection(Collections.tags), FIELD_GROUPS, group);
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                batch.update(document.getReference(), FIELD_GROUPS, FieldValue.arrayRemove(group));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("retrieval-failed", "Failure when retrieving the tags data");
        }
    }

    /**
     * Deletes the group from all its members.
     *
     * @param name The group name
     * @param batch The batch where to write
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void deleteAllMembers(String name, WriteBatch batch) throws DatabaseAccessException {
        ApiFuture<QuerySnapshot> querySnapshot = collectionAdapter
            .getDocumentsWhereArrayContains(Path.ofCollection(Collections.users), GROUP_SUBSCRIPTIONS_FIELDS, name);
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                batch.update(document.getReference(), GROUP_SUBSCRIPTIONS_FIELDS, FieldValue.arrayRemove(name));
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
     * @param batch The batch where to write
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void changeNameInTags(String oldName, String newName, WriteBatch batch) throws DatabaseAccessException {
        ApiFuture<QuerySnapshot> querySnapshot = collectionAdapter
            .getDocumentsWhereArrayContains(Path.ofCollection(Collections.tags), FIELD_GROUPS, oldName);
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
     * @param batch The batch where to write
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void changeNameInSubscription(String oldName, String newName, WriteBatch batch)
        throws DatabaseAccessException {
        ApiFuture<QuerySnapshot> querySnapshot = collectionAdapter
            .getDocumentsWhereArrayContains(Path.ofCollection(Collections.users), GROUP_SUBSCRIPTIONS_FIELDS, oldName);
        try {
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                DocumentReference ref = document.getReference();
                batch.update(ref, GROUP_SUBSCRIPTIONS_FIELDS, FieldValue.arrayRemove(oldName));
                batch.update(ref, GROUP_SUBSCRIPTIONS_FIELDS, FieldValue.arrayUnion(newName));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("update-failed", "Failure when updating name in subscriptions");
        }
    }

    /**
     * Deletes the specified tags from a group.
     *
     * @param group The group name
     * @param groupId The group ID
     * @param deletedTags The list of deleted tags
     * @param batch The batch where to write
     */
    private void deleteGroupTags(String group, String groupId, List<String> deletedTags, WriteBatch batch) {
        documentAdapter.updateDocumentFields(batch, Path.ofDocument(Collections.groups, groupId), FIELD_TAGS,
            FieldValue.arrayRemove(deletedTags.toArray()));
        for (String tag : deletedTags) {
            deleteGroupFromTag(tag, group, batch);
        }
    }

    /**
     * Adds the specified tags to a group.
     *
     * @param group The group name
     * @param groupId The group ID
     * @param newTags The list of tags to add
     * @param batch The batch where to write
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void addTagsToGroup(String group, String groupId, List<String> newTags, WriteBatch batch)
        throws DatabaseAccessException {
        documentAdapter.updateDocumentFields(batch, Path.ofDocument(Collections.groups, groupId), FIELD_TAGS,
            FieldValue.arrayUnion(newTags.toArray()));
        for (String tag : newTags) {
            addGroupToTag(tag, group, batch);
        }
    }
}
