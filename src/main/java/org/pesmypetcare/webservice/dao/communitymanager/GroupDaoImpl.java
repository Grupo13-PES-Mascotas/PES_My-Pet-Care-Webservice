package org.pesmypetcare.webservice.dao.communitymanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class GroupDaoImpl implements GroupDao {
    private final CollectionReference groups;
    private final CollectionReference groupsNames;
    private final Firestore db;
    private WriteBatch batch;

    public GroupDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
        groups = db.collection("groups");
        groupsNames = db.collection("groups_names");
    }

    @Override
    public void createGroup(GroupEntity entity) {
        batch = db.batch();
        DocumentReference groupRef = groups.document();
        batch.set(groupRef, entity);
        String name = entity.getName();
        saveGroupName(name, batch, groupRef.getId());
    }

    @Override
    public void deleteGroup(String name) throws DatabaseAccessException {
        String id = getGroupId(name);
        DocumentReference groupRef = groups.document(id);
        batch = db.batch();
        batch.delete(groupRef);
        DocumentReference namesRef = groupsNames.document(name);
        batch.delete(namesRef);
    }

    @Override
    public GroupEntity getGroup(String name) throws DatabaseAccessException {
        String id = getGroupId(name);
        DocumentSnapshot snapshot = getDocumentSnapshot(groups, id);
        return snapshot.toObject(GroupEntity.class);
    }

    @Override
    public List<GroupEntity> getAllGroups() throws DatabaseAccessException {
        ApiFuture<QuerySnapshot> future = groups.get();
        try {
            List<GroupEntity> groupList = new ArrayList<>();
            List<QueryDocumentSnapshot> groupDocs = future.get().getDocuments();
            for (QueryDocumentSnapshot group : groupDocs) {
                groupList.add(group.toObject(GroupEntity.class));
            }
            return groupList;
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("list-error", "The groups could not be retrieved");
        }
    }

    @Override
    public void updateField(String name, String field, Object newValue) throws DatabaseAccessException {
        String id = getGroupId(name);
        if ("tags".equals(field)) {
            groups.document(id).update(field, newValue);
        } else {
            groups.document(id).update(field, newValue);
        }
        if ("name".equals(field)) {
            groupsNames.document(name).delete();
            saveGroupName((String) newValue, id);
        }
    }

    @Override
    public boolean groupNameInUse(String name) throws DatabaseAccessException {
        DocumentSnapshot snapshot = getDocumentSnapshot(groupsNames, name);
        return snapshot.exists();
    }

    /**
     * Gets a document snapshot from a collection.
     * @param collection The collection to which the document belongs
     * @param name The document name
     * @return The document snapshot
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private DocumentSnapshot getDocumentSnapshot(CollectionReference collection,
                                                 String name) throws DatabaseAccessException {
        ApiFuture<DocumentSnapshot> future = collection.document(name).get();
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
     * @param batch The batch of writes to which it belongs
     * @param groupId The group id to which the name belongs
     */
    private void saveGroupName(String name, WriteBatch batch, String groupId) {
        DocumentReference namesRef = groupsNames.document(name);
        Map<String, String> docData = new HashMap<>();
        docData.put("group", groupId);
        batch.set(namesRef, docData);
    }

    /**
     * Saves the group name in database.
     * @param name The group name
     * @param groupId The group id to which the name belongs
     */
    private void saveGroupName(String name, String groupId) {
        DocumentReference namesRef = groupsNames.document(name);
        Map<String, String> docData = new HashMap<>();
        docData.put("group", groupId);
        namesRef.set(docData);
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
