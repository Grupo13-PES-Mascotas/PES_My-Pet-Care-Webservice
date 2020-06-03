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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith({MockitoExtension.class})
class ForumDaoTest {
    private static String groupName;
    private static String forumName;
    private static String groupId;
    private static String forumId;
    private static String tagsPath;
    private static String messagePath;
    private static String tagPath;
    private static String username;
    private static String username2;
    private static String date;
    private static ForumEntity forumEntity;
    private static List<String> tags;
    private static List<String> usernameList;
    private static Message message;
    private List<QueryDocumentSnapshot> queryDocumentSnapshots;

    @Mock
    private GroupDao groupDao;
    @Mock
    private UserDao userDao;
    @Mock
    private StorageDao storageDao;
    @Mock
    private FirebaseMessaging firebaseMessaging;
    @Mock
    private FirestoreDocument documentAdapter;
    @Mock
    private FirestoreCollection collectionAdapter;
    @Mock
    private WriteBatch batch;
    @Mock
    private DocumentReference documentReference;
    @Mock
    private ApiFuture<QuerySnapshot> query;
    @Mock
    private QuerySnapshot querySnapshot;
    @Mock
    private QueryDocumentSnapshot documentSnapshot;

    @InjectMocks
    private ForumDao dao = new ForumDaoImpl();
    private String publicationDate;
    private String newName;

    @BeforeAll
    public static void beforeAll() {
        groupName = "Dogs";
        forumName = "Huskies";
        groupId = "asd2d9833jdaA3";
        forumId = "ad33i8jf93";
        username = "John";
        username2 = "Henry";
        usernameList = new ArrayList<>();
        usernameList.add("John");
        usernameList.add("Pedro");
        usernameList.add("Chang");
        date = "2020-05-01T17:48:15";
        forumEntity = new ForumEntity();
        forumEntity.setName(forumName);
        tags = new ArrayList<>();
        tags.add("dogs");
        forumEntity.setTags(tags);
        tagsPath = Path.ofCollection(Collections.tags);
        tagPath = Path.ofDocument(Collections.tags, "dogs");
        messagePath = Path.ofCollection(Collections.messages, groupId, forumId);
        message = new Message();
        message.setCreator(username);
        message.setText("Some text");
        message.setEncodedImage("ZGFkYTIxM2FkMw==");
    }


    @BeforeEach
    public void setUp() {
        queryDocumentSnapshots = new ArrayList<>();
        queryDocumentSnapshots.add(documentSnapshot);
        publicationDate = "publicationDate";
        newName = "German Shepherds";
    }

    @Test
    public void forumNameInUseShouldReturnTrueWhenTheNameAlreadyExistsInTheGroup() throws DatabaseAccessException {
        given(documentAdapter.documentExists(anyString())).willReturn(true);

        assertTrue(dao.forumNameInUse(groupName, forumName),
            "Should return true when the forum name is already in use in the specified group.");
    }

    @Test
    public void forumNameInUseShouldReturnFalseWhenTheNameDoesNotExistInTheGroup() throws DatabaseAccessException {
        given(documentAdapter.documentExists(anyString())).willReturn(false);

        assertFalse(dao.forumNameInUse(groupName, forumName),
            "Should return false when the forum name is not in use in the specified group.");
    }

    @Test
    public void getForum() throws DatabaseAccessException, DocumentException {
        mockGetGroupAndForumIds();
        given(documentAdapter.getDocumentDataAsObject(anyString(), any())).willReturn(forumEntity);

        ForumEntity forum = dao.getForum(groupName, forumName);

        assertEquals(forumEntity, forum, "Should return the requested forum");
    }

    @Test
    public void getAllForumsFromGroup() throws DatabaseAccessException, DocumentException {
        given(groupDao.getGroupId(anyString())).willReturn(groupId);
        mockDocumentSnapshotList();
        given(documentSnapshot.toObject(any())).willReturn(forumEntity);

        List<ForumEntity> forums = dao.getAllForumsFromGroup(groupName);
        List<ForumEntity> forumList = new ArrayList<>();
        forumList.add(forumEntity);
        assertEquals(forumList, forums, "Should return all forums in the group.");
    }

    @Test
    public void getForumIdShouldThrowDocumentExceptionWhenWhenTheForumDoesNotExist()
        throws DatabaseAccessException, DocumentException {
        given(groupDao.getGroupId(anyString())).willReturn(groupId);
        assertThrows(DocumentException.class, () -> {
            willThrow(DocumentException.class).given(documentAdapter).getStringFromDocument(anyString(), anyString());
            dao.getForum(groupName, forumName);
        });
    }

    @Test
    public void getAllPostImagesPaths() throws DatabaseAccessException, DocumentException {
        mockGetGroupAndForumIds();
        mockDocumentSnapshotList();
        String imagePath = "some/image/path";
        given(documentSnapshot.getString(anyString())).willReturn(imagePath);

        List<String> paths = new ArrayList<>();
        paths.add(imagePath);
        List<String> result = dao.getAllPostImagesPaths(groupName, forumName);
        assertEquals(paths, result, "Should return all the path images of the forum posts.");
    }

    @Test
    public void updateNameShouldFailWhenTheNewNameIsInUse() {
        assertThrows(DocumentException.class, () -> {
            given(documentAdapter.documentExists(anyString())).willReturn(true);
            dao.updateName(groupName, forumName, newName);
        }, "Should fail when the name is already in use.");
    }

    private void mockGetGroupAndForumIds() throws DatabaseAccessException, DocumentException {
        given(groupDao.getGroupId(anyString())).willReturn(groupId);
        given(documentAdapter.getStringFromDocument(anyString(), anyString())).willReturn(forumId);
    }

    private void mockAddForumToTag() throws DatabaseAccessException {
        given(documentAdapter.documentExists(anyString())).willReturn(false);
        willDoNothing().given(documentAdapter).setDocumentFields(anyString(), anyMap(), any(WriteBatch.class));
    }

    private void verifyAddForumToTag(String forumName) throws DatabaseAccessException {
        Map<String, Object> data = new HashMap<>();
        data.put("forums", FieldValue.arrayUnion(forumName));
        verify(documentAdapter).documentExists(eq(tagPath));
        verify(documentAdapter).setDocumentFields(eq(tagPath), eq(data), same(batch));
    }

    private void verifySaveForumName(String forumName) {
        String forumNamePath = Path.ofCollection(Collections.forum_names, groupName);
        Map<String, Object> docData = new HashMap<>();
        docData.put("forum", forumId);
        verify(documentAdapter).createDocumentWithId(eq(forumNamePath), eq(forumName), eq(docData), same(batch));
    }

    private void mockDocumentSnapshotList() throws DatabaseAccessException {
        List<DocumentSnapshot> snapshots = mock(List.class);
        given(collectionAdapter.listAllCollectionDocumentSnapshots(anyString())).willReturn(snapshots);
        Iterator<DocumentSnapshot> mockIterator = mock(Iterator.class);
        given(snapshots.iterator()).willReturn(mockIterator);
        given(mockIterator.hasNext()).willReturn(true, false);
        given(mockIterator.next()).willReturn(documentSnapshot);
    }

    @Nested
    class UsesWriteBatch {
        private String forumsPath;
        private String forumPath;

        @BeforeEach
        public void setUp() {
            given(documentAdapter.batch()).willReturn(batch);
            forumsPath = Path.ofCollection(Collections.forums, groupId);
            forumPath = Path.ofDocument(Collections.forums, groupId, forumId);
        }

        @Test
        public void createForumShouldThrowDocumentExceptionWhenTheGroupDoesNotExist()
            throws DatabaseAccessException, DocumentException {
            willThrow(DocumentException.class).given(documentAdapter).getStringFromDocument(anyString(), anyString());
            assertThrows(DocumentException.class, () -> dao.createForum(groupName, forumEntity),
                "Create forum should fail when the group does not exist.");
        }

        @Nested
        class CommitsBatch {
            @BeforeEach
            public void setUp() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(documentAdapter).commitBatch(batch);
            }

            @Test
            public void createForum() throws DatabaseAccessException, DocumentException {
                given(documentAdapter.getStringFromDocument(anyString(), anyString())).willReturn(groupId);
                given(documentAdapter.createDocument(anyString(), any(ForumEntity.class), any(WriteBatch.class)))
                    .willReturn(documentReference);
                given(documentAdapter.createDocumentWithId(anyString(), anyString(), anyMap(), any(WriteBatch.class)))
                    .willReturn(documentReference);
                mockAddForumToTag();
                given(documentReference.getId()).willReturn(forumId);

                dao.createForum(groupName, forumEntity);

                verifyAddForumToTag(forumName);

                String groupPath = Path.ofDocument(Collections.groups_names, groupName);
                verify(documentAdapter).getStringFromDocument(eq(groupPath), eq("group"));
                verify(documentAdapter).createDocument(eq(forumsPath), same(forumEntity), same(batch));
                verifySaveForumName(forumName);
            }

            @Test
            public void updateTags() throws DatabaseAccessException, DocumentException {
                mockGetGroupAndForumIds();
                willDoNothing().given(documentAdapter)
                    .updateDocumentFields(anyString(), anyString(), any(FieldValue.class), any(WriteBatch.class));
                mockAddForumToTag();

                dao.updateTags(groupName, forumName, tags, tags);
                verify(documentAdapter)
                    .updateDocumentFields(eq(forumPath), eq("tags"), eq(FieldValue.arrayRemove(tags.toArray())),
                        same(batch));
                verify(documentAdapter)
                    .updateDocumentFields(eq(tagPath), eq("forums"), eq(FieldValue.arrayRemove(forumName)),
                        same(batch));
                verify(documentAdapter)
                    .updateDocumentFields(eq(forumPath), eq("tags"), eq(FieldValue.arrayUnion(tags.toArray())),
                        same(batch));
                verifyAddForumToTag(forumName);
            }

            @Test
            public void postMessage() throws DatabaseAccessException, DocumentException, FirebaseMessagingException {
                mockGetGroupAndForumIds();
                given(documentAdapter.createDocument(anyString(), any(MessageEntity.class), any(WriteBatch.class)))
                    .willReturn(documentReference);
                List<String> devices = new ArrayList<>();
                String fcmToken = "adanj3n2";
                devices.add(fcmToken);
                devices.add("ffkjkke");
                given(documentAdapter.getDocumentField(anyString(), anyString())).willReturn(devices);
                given(firebaseMessaging.sendMulticast(any(MulticastMessage.class))).willReturn(null);
                given(userDao.getUid(anyString())).willReturn(username);
                given(userDao.getField(anyString(), anyString())).willReturn(fcmToken);

                dao.postMessage(groupName, forumName, message);
                verify(documentAdapter).createDocument(eq(messagePath), isA(MessageEntity.class), same(batch));
                verify(documentAdapter)
                    .getDocumentField(eq(Path.ofDocument(Collections.groups, groupId)), eq("notification-tokens"));
                verify(firebaseMessaging).sendMulticast(isA(MulticastMessage.class));
                verify(userDao).getUid(eq(username));
                verify(userDao).getField(eq(username), eq("FCM"));
            }

            @Nested
            class UsesQuery {

                @BeforeEach
                public void setUp() throws ExecutionException, InterruptedException {
                    given(query.get()).willReturn(querySnapshot);
                    given(querySnapshot.getDocuments()).willReturn(queryDocumentSnapshots);
                }

                @Test
                public void deleteForum() throws DatabaseAccessException, DocumentException {
                    mockGetGroupAndForumIds();
                    willDoNothing().given(documentAdapter).deleteDocument(anyString(), any(WriteBatch.class));
                    given(
                        collectionAdapter.getDocumentsWhereArrayContains(anyString(), anyString(), anyString(), any()))
                        .willReturn(query);
                    given(documentSnapshot.getReference()).willReturn(documentReference);
                    given(batch.update(any(DocumentReference.class), anyMap())).willReturn(batch);

                    dao.deleteForum(groupName, forumName);
                    verify(groupDao).getGroupId(same(groupName));
                    String forumNamePath = Path.ofDocument(Collections.forum_names, groupName, forumName);
                    verify(documentAdapter).getStringFromDocument(eq(forumNamePath), eq("forum"));
                    verify(documentAdapter).deleteDocument(eq(forumPath), same(batch));
                    verify(documentAdapter).deleteDocument(eq(forumNamePath), same(batch));
                    verify(collectionAdapter)
                        .getDocumentsWhereArrayContains(eq(tagsPath), eq("forums"), same(forumName));
                    Map<String, Object> data = new HashMap<>();
                    data.put("forums", FieldValue.arrayRemove(forumName));
                    verify(batch).update(same(documentReference), eq(data));
                }

                @Test
                public void updateName() throws DatabaseAccessException, DocumentException {
                    mockGetGroupAndForumIds();
                    given(documentAdapter.documentExists(anyString())).willReturn(false);
                    willDoNothing().given(documentAdapter)
                        .updateDocumentFields(anyString(), anyString(), anyString(), any(WriteBatch.class));
                    willDoNothing().given(documentAdapter).deleteDocument(anyString(), any(WriteBatch.class));
                    willDoNothing().given(documentAdapter)
                        .updateDocumentFields(anyString(), anyString(), any(FieldValue.class), any(WriteBatch.class));
                    mockAddForumToTag();
                    given(
                        documentAdapter.createDocumentWithId(anyString(), anyString(), anyMap(), any(WriteBatch.class)))
                        .willReturn(documentReference);
                    given(collectionAdapter.getDocumentsWhereArrayContains(anyString(), anyString(), anyString()))
                        .willReturn(query);
                    given(documentSnapshot.getId()).willReturn("dogs");

                    dao.updateName(groupName, forumName, newName);
                    verifyAddForumToTag(newName);
                    verifySaveForumName(newName);
                }

                @Test
                public void deleteMessage() throws DatabaseAccessException, DocumentException {
                    mockGetGroupAndForumIds();
                    given(
                        collectionAdapter.getDocumentsWhereEqualTo(anyString(), anyString(), any(), anyString(), any()))
                        .willReturn(query);
                    given(documentSnapshot.getString(anyString())).willReturn("some-path");
                    given(documentSnapshot.getReference()).willReturn(documentReference);
                    given(batch.delete(any(DocumentReference.class))).willReturn(batch);
                    willDoNothing().given(storageDao).deleteImageByName(anyString());

                    dao.deleteMessage(groupName, forumName, username, date);
                    verify(collectionAdapter)
                        .getDocumentsWhereEqualTo(eq(Path.ofCollection(Collections.messages, groupId, forumId)),
                            eq("creator"), eq(username), eq(publicationDate), eq(date));
                    verify(storageDao).deleteImageByName(eq("some-path"));
                    verify(batch).delete(same(documentReference));
                }

                @Test
                public void reportMessage() throws DatabaseAccessException, DocumentException,
                    InvalidOperationException {
                    mockGetGroupAndForumIds();
                    given(
                        collectionAdapter.getDocumentsWhereEqualTo(anyString(), anyString(), any(), anyString(), any()))
                        .willReturn(query);
                    given(documentSnapshot.getReference()).willReturn(documentReference);
                    given(documentSnapshot.get(anyString())).willReturn(usernameList);
                    given(batch.update(any(DocumentReference.class), anyString(), any())).willReturn(batch);

                    dao.reportMessage(groupName, forumName, username, username2, date);
                    verify(collectionAdapter)
                        .getDocumentsWhereEqualTo(eq(Path.ofCollection(Collections.messages, groupId, forumId)),
                            eq("creator"), eq(username), eq(publicationDate), eq(date));
                    verify(batch).update(same(documentReference), eq("reportedBy"),
                        eq(FieldValue.arrayUnion(username2)));
                    verify(batch).update(same(documentReference), eq("banned"),
                        eq(true));
                }

                @Test
                public void unbanMessage() throws DatabaseAccessException, DocumentException {
                    mockGetGroupAndForumIds();
                    given(
                        collectionAdapter.getDocumentsWhereEqualTo(anyString(), anyString(), any(), anyString(), any()))
                        .willReturn(query);
                    given(documentSnapshot.getReference()).willReturn(documentReference);
                    given(batch.update(any(DocumentReference.class), anyString(), any())).willReturn(batch);

                    dao.unbanMessage(groupName, forumName, username, date);
                    verify(collectionAdapter)
                        .getDocumentsWhereEqualTo(eq(Path.ofCollection(Collections.messages, groupId, forumId)),
                            eq("creator"), eq(username), eq(publicationDate), eq(date));
                    verify(batch).update(same(documentReference), eq("reportedBy"),
                        eq(new ArrayList()));
                    verify(batch).update(same(documentReference), eq("banned"),
                        eq(false));
                }

                @Test
                public void addUserToLikedByOfMessage() throws DatabaseAccessException, DocumentException {
                    mockGetGroupAndForumIds();
                    given(
                        collectionAdapter.getDocumentsWhereEqualTo(anyString(), anyString(), any(), anyString(), any()))
                        .willReturn(query);
                    given(documentSnapshot.getReference()).willReturn(documentReference);
                    given(batch.update(any(DocumentReference.class), anyString(), any(FieldValue.class)))
                        .willReturn(batch);

                    dao.addUserToLikedByOfMessage(username, groupName, forumName, username, date);
                    verify(collectionAdapter)
                        .getDocumentsWhereEqualTo(eq(Path.ofCollection(Collections.messages, groupId, forumId)),
                            eq("creator"), eq(username), eq(publicationDate), eq(date));
                    verify(batch).update(same(documentReference), eq("likedBy"),
                        eq(FieldValue.arrayUnion(username)));
                }

                @Test
                public void removeUserFromLikedByOfMessage() throws DatabaseAccessException, DocumentException {
                    mockGetGroupAndForumIds();
                    given(
                        collectionAdapter.getDocumentsWhereEqualTo(anyString(), anyString(), any(), anyString(), any()))
                        .willReturn(query);
                    given(documentSnapshot.getReference()).willReturn(documentReference);
                    given(batch.update(any(DocumentReference.class), anyString(), any(FieldValue.class)))
                        .willReturn(batch);

                    dao.removeUserFromLikedByOfMessage(username, groupName, forumName, username, date);
                    verify(collectionAdapter)
                        .getDocumentsWhereEqualTo(eq(Path.ofCollection(Collections.messages, groupId, forumId)),
                            eq("creator"), eq(username), eq(publicationDate), eq(date));
                    verify(batch).update(same(documentReference), eq("likedBy"),
                        eq(FieldValue.arrayRemove(username)));
                }
            }
        }

    }
}
