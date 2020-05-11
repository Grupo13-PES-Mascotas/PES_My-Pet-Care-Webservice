package org.pesmypetcare.webservice.dao.communitymanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
import org.pesmypetcare.webservice.dao.usermanager.UserDao;
import org.pesmypetcare.webservice.entity.communitymanager.Group;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.entity.communitymanager.TagEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class GroupDaoTest {
    private String groupName;
    private String groupId;
    private String username;
    private String userId;
    private String tag;
    private Group group;
    private String date;
    private String groupPath;
    private String tagsPath;
    private String tagPath;
    private String usersPath;
    private String groupNamePath;
    private String newName;
    private String field;

    @Mock
    private UserDao userDao;
    @Mock
    private FirestoreDocument documentAdapter;
    @Mock
    private FirestoreCollection collectionAdapter;
    @Mock
    private WriteBatch batch;
    @Mock
    private DocumentReference docRef;
    @Mock
    private ApiFuture<QuerySnapshot> query;
    @Mock
    private QuerySnapshot querySnapshot;
    @Mock
    private QueryDocumentSnapshot documentSnapshot;

    @InjectMocks
    private GroupDao dao = new GroupDaoImpl();

    @BeforeEach
    public void setUp() {
        groupName = "Dogs";
        groupId = "12daw3e23d";
        group = new Group();
        date = "2020-05-01T17:48:15";
        tag = "dogs";
        newName = "Small Dogs";
        field = "name";
        groupPath = Path.ofDocument(Collections.groups, groupId);
        tagsPath = Path.ofCollection(Collections.tags);
        tagPath = Path.ofDocument(Collections.tags, tag);
        usersPath = Path.ofCollection(Collections.users);
        groupNamePath = Path.ofDocument(Collections.groupsNames, groupName);
    }

    @Test
    public void getGroup() throws DatabaseAccessException, DocumentException {
        mockGetGroupId();
        given(documentAdapter.getDocumentDataAsObject(eq(groupPath), same(Group.class))).willReturn(group);
        mockListAllCollectionDocumentSnapshots();
        given(documentSnapshot.getString(anyString())).willReturn(username, date);

        Group result = dao.getGroup(groupName);
        Map<String, String> members = new HashMap<>();
        members.put(username, date);
        group.setMembers(members);
        assertEquals(group, result, "Should return the requested group.");
    }

    @Test
    public void getAllGroups() throws DatabaseAccessException {
        mockListAllCollectionDocumentSnapshots();
        given(documentSnapshot.toObject(any())).willReturn(group);
        given(documentSnapshot.getString(anyString())).willReturn(username, date);
        given(documentSnapshot.getId()).willReturn(groupId);


        List<Group> result = dao.getAllGroups();
        Map<String, String> members = new HashMap<>();
        members.put(username, date);
        group.setMembers(members);
        List<Group> groupList = new ArrayList<>();
        groupList.add(group);
        assertEquals(groupList, result, "Should return all the existing groups.");
    }

    @Test
    public void groupNameInUse() throws DatabaseAccessException {
        lenient().when(documentAdapter.documentExists(eq(groupNamePath))).thenReturn(true);
        assertTrue(dao.groupNameInUse(groupName), "Should return true if the group name is already in use.");
    }

    @Test
    public void getAllTags() throws DatabaseAccessException {
        List<DocumentSnapshot> snapshotList = new ArrayList<>();
        snapshotList.add(documentSnapshot);
        given(collectionAdapter.listAllCollectionDocumentSnapshots(any())).willReturn(snapshotList);
        given(documentSnapshot.getId()).willReturn(tag);
        TagEntity tagEntity = new TagEntity();
        given(documentSnapshot.toObject(any())).willReturn(tagEntity);

        Map<String, TagEntity> result = dao.getAllTags();
        Map<String, TagEntity> expected = new HashMap<>();
        expected.put(tag, tagEntity);
        assertEquals(expected, result, "Should return all the existing tags");
    }

    @Test
    public void getGroupId() throws DatabaseAccessException, DocumentException {
        mockGetGroupId();
        assertEquals(groupId, dao.getGroupId(groupName), "Should return the group ID.");
    }

    private void mockListAllCollectionDocumentSnapshots() throws DatabaseAccessException {
        List<DocumentSnapshot> snapshotList = new ArrayList<>();
        snapshotList.add(documentSnapshot);
        given(collectionAdapter.listAllCollectionDocumentSnapshots(AdditionalMatchers
            .or(eq(Path.ofCollection(Collections.members, groupId)), eq(Path.ofCollection(Collections.groups)))))
            .willReturn(snapshotList);
    }

    private void mockGetGroupId() throws DatabaseAccessException, DocumentException {
        given(documentAdapter.getStringFromDocument(eq(groupNamePath), eq("group"))).willReturn(groupId);
    }

    private void mockQuery() throws ExecutionException, InterruptedException {
        given(query.get()).willReturn(querySnapshot);
        List<QueryDocumentSnapshot> mockList = mock(List.class);
        given(querySnapshot.getDocuments()).willReturn(mockList);
        Iterator<QueryDocumentSnapshot> mockIterator = mock(Iterator.class);
        given(mockList.iterator()).willReturn(mockIterator);
        given(mockIterator.hasNext()).willReturn(true, false);
        given(mockIterator.next()).willReturn(documentSnapshot);
    }

    @Test
    public void updateNameShouldFailIfNewNameAlreadyInUse() throws DatabaseAccessException, DocumentException {
        mockGetGroupId();
        given(documentAdapter.batch()).willReturn(batch);
        willDoNothing().given(documentAdapter).updateDocumentFields(any(WriteBatch.class), anyString(), anyString(),
            any());
        given(documentAdapter.documentExists(anyString())).willReturn(true);
        assertThrows(DocumentException.class, () -> dao.updateField(groupName, field, newName),
            "Update group name should fail if the new name is already in use.");
    }

    @Nested
    class UsesWriteBatch {
        private String groupNamesPath;
        private String groupsPath;

        @BeforeEach
        public void setUp() throws DatabaseAccessException, DocumentException {
            username = "John";
            userId = "sda23e8823nda";
            groupsPath = Path.ofCollection(Collections.groups);
            groupNamesPath = Path.ofCollection(Collections.groupsNames);
            given(documentAdapter.batch()).willReturn(batch);
            willDoNothing().given(documentAdapter).commitBatch(any(WriteBatch.class));
        }

        @Test
        public void createGroup() throws DatabaseAccessException, DocumentException {
            given(documentAdapter.createDocument(anyString(), any(GroupEntity.class), any(WriteBatch.class)))
                .willReturn(docRef);
            willDoNothing().given(userDao).addGroupSubscription(anyString(), anyString(), any(WriteBatch.class));
            lenient().when(documentAdapter.createDocumentWithId(anyString(), anyString(), any(), any(WriteBatch.class)))
                .thenReturn(docRef);
            given(documentAdapter.documentExists(anyString())).willReturn(true);
            lenient().doNothing().when(documentAdapter).updateDocumentFields(anyString(), anyString(), any(),
                any(WriteBatch.class));
            given(docRef.getId()).willReturn(groupId);
            given(userDao.getUid(anyString())).willReturn(userId);

            List<String> tags = new ArrayList<>();
            tags.add(tag);
            GroupEntity entity = new GroupEntity(groupName, username, "", tags);
            dao.createGroup(entity);
            verify(documentAdapter).createDocument(eq(groupsPath), same(entity), same(batch));
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss",
                new Locale("es", "ES"));
            Map<String, Object> data = new HashMap<>();
            data.put("group", groupId);
            String membersPath = Path.ofCollection(Collections.members, groupId);
            Map<String, Object> data2 = new HashMap<>();
            data2.put("user", username);
            data2.put("date", timeFormatter.format(LocalDateTime.now()));
            verify(documentAdapter).createDocumentWithId(AdditionalMatchers.or(eq(groupNamesPath), eq(membersPath)),
                AdditionalMatchers.or(eq(groupName), eq(userId)), AdditionalMatchers.or(eq(data), eq(data2)),
                same(batch));
            verify(userDao).addGroupSubscription(eq(username), eq(groupName), same(batch));
            verify(documentAdapter).documentExists(eq(tagPath));
            verify(documentAdapter).updateDocumentFields(same(batch), eq(tagPath), eq("groups"), any(FieldValue.class));
        }

        @Test
        public void deleteGroup()
            throws ExecutionException, InterruptedException, DatabaseAccessException, DocumentException {
            mockGetGroupId();
            given(collectionAdapter.getDocumentsWhereArrayContains(anyString(), anyString(), any())).willReturn(query);
            given(documentSnapshot.getReference()).willReturn(docRef);
            lenient().when(batch.update(any(DocumentReference.class), anyString(), any())).thenReturn(batch);
            willDoNothing().given(documentAdapter).deleteDocument(anyString(), any(WriteBatch.class));
            mockQuery();

            dao.deleteGroup(groupName);
            verify(documentAdapter).getStringFromDocument(eq(groupNamePath), eq("group"));
            verify(collectionAdapter, times(2)).getDocumentsWhereArrayContains(
                AdditionalMatchers.or(eq(tagsPath), eq(usersPath)),
                AdditionalMatchers.or(eq("groups"), eq("groupSubscriptions")), eq(groupName));
            verify(batch).update(same(docRef), AdditionalMatchers.or(eq("groups"), eq("groupSubscriptions")),
                eq(FieldValue.arrayRemove(groupName)));
            String groupPath = Path.ofDocument(Collections.groups, groupId);
            verify(documentAdapter, times(2)).deleteDocument(AdditionalMatchers.or(eq(groupPath), eq(groupNamePath)),
                same(batch));
        }

        @Test
        public void updateField()
            throws DatabaseAccessException, DocumentException, ExecutionException, InterruptedException {
            mockGetGroupId();
            willDoNothing().given(documentAdapter).updateDocumentFields(any(WriteBatch.class), anyString(), anyString(),
                any());
            given(documentAdapter.documentExists(anyString())).willReturn(false, true);
            given(collectionAdapter.getDocumentsWhereArrayContains(anyString(), anyString(), any())).willReturn(query);
            mockQuery();
            given(documentSnapshot.getId()).willReturn(tag);
            willDoNothing().given(documentAdapter).deleteDocument(anyString(), any(WriteBatch.class));
            lenient().when(
                documentAdapter.createDocumentWithId(anyString(), anyString(), anyMap(), any(WriteBatch.class)))
                .thenReturn(docRef);

            dao.updateField(groupName, field, newName);
            verify(documentAdapter).updateDocumentFields(same(batch), eq(groupPath), eq(field), eq(newName));
            verify(collectionAdapter, times(2)).getDocumentsWhereArrayContains(
                AdditionalMatchers.or(eq(tagsPath), eq(usersPath)),
                AdditionalMatchers.or(eq("groups"), eq("groupSubscriptions")), eq(groupName));
            verify(documentAdapter).updateDocumentFields(same(batch), AdditionalMatchers.or(eq(groupPath), eq(tagPath)),
                AdditionalMatchers.or(eq("groups"), eq("name")), AdditionalMatchers.or(eq(newName),
                    AdditionalMatchers.or(eq(FieldValue.arrayRemove(groupId)), eq(FieldValue.arrayUnion(groupId)))));
            verify(documentAdapter).documentExists(tagPath);
            verify(documentAdapter).deleteDocument(eq(groupNamePath), same(batch));
            Map<String, String> docData = new HashMap<>();
            docData.put("group", groupId);
            verify(documentAdapter).createDocumentWithId(eq(groupNamesPath), same(newName), eq(docData), same(batch));
            verify(documentAdapter).commitBatch(same(batch));
        }

        @Test
        public void subscribe() throws DatabaseAccessException, DocumentException {
            given(userDao.getUid(anyString())).willReturn(userId);
            mockGetGroupId();
            lenient().when(
                documentAdapter.createDocumentWithId(anyString(), anyString(), anyMap(), any(WriteBatch.class)))
                .thenReturn(docRef);

            dao.subscribe(groupName, username);
            verify(userDao).addGroupSubscription(same(username), same(groupName), same(batch));
            String membersPath = Path.ofCollection(Collections.members, groupId);
            verify(documentAdapter).createDocumentWithId(eq(membersPath), same(userId), anyMap(), same(batch));
        }

        @Test
        public void unsubscribe() throws DatabaseAccessException, DocumentException {
            given(userDao.getUid(anyString())).willReturn(userId);
            mockGetGroupId();
            willDoNothing().given(documentAdapter).deleteDocument(anyString(), any(WriteBatch.class));
            willDoNothing().given(userDao).deleteGroupSubscription(anyString(), anyString(), any(WriteBatch.class));

            dao.unsubscribe(groupName, username);
            verify(userDao).getUid(same(username));
            String memberPath = Path.ofDocument(Collections.members, groupId, userId);
            verify(documentAdapter).deleteDocument(eq(memberPath), same(batch));
            verify(userDao).deleteGroupSubscription(same(userId), same(groupName), same(batch));
        }

        @Test
        public void updateTags() throws DatabaseAccessException, DocumentException {
            mockGetGroupId();
            willDoNothing().given(documentAdapter).updateDocumentFields(any(WriteBatch.class), anyString(), anyString(),
                any());
            given(documentAdapter.documentExists(tagPath)).willReturn(false);
            given(documentAdapter.createDocumentWithId(anyString(), anyString(), anyMap(), any(WriteBatch.class)))
                .willReturn(docRef);

            List<String> deletedTags = new ArrayList<>();
            deletedTags.add(tag);
            List<String> newTags = new ArrayList<>();
            newTags.add(tag);
            dao.updateTags(groupName, newTags, deletedTags);
            verify(documentAdapter, times(2)).updateDocumentFields(same(batch),
                AdditionalMatchers.or(eq(groupPath), eq(tagPath)), AdditionalMatchers.or(eq("groups"), eq("tags")),
                AdditionalMatchers.or(eq(FieldValue.arrayRemove(groupId)), AdditionalMatchers
                    .or(eq(FieldValue.arrayUnion(newTags.toArray())),
                        eq(FieldValue.arrayRemove(deletedTags.toArray())))));
            verify(documentAdapter).createDocumentWithId(eq(tagsPath), eq(tag), anyMap(), same(batch));
        }
    }
}

