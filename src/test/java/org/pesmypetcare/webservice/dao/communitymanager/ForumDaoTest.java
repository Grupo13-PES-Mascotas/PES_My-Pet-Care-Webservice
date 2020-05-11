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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private String groupName;
    private String forumName;
    private String groupId;
    private String forumId;
    private String tagsPath;
    private String messagePath;
    private String tagPath;
    private ForumEntity forumEntity;
    private List<String> tags;
    private Message message;
    private MessageEntity messageEntity;

    @Mock
    private GroupDao groupDao;
    @Mock
    private StorageDao storageDao;
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


    @BeforeEach
    public void setUp() {
        groupName = "Dogs";
        forumName = "Huskies";
        groupId = "asd2d9833jdaA3";
        forumId = "ad33i8jf93";
        forumEntity = new ForumEntity();
        forumEntity.setName(forumName);
        tags = new ArrayList<>();
        tags.add("dogs");
        forumEntity.setTags(tags);
        tagsPath = Path.ofCollection(Collections.tags);
        tagPath = Path.ofDocument(Collections.tags, "dogs");
        messagePath = Path.ofCollection(Collections.messages, groupId, forumId);
        messageEntity = new MessageEntity();
        messageEntity.setCreator("John");
        messageEntity.setText("Some text");
        message = new Message();
        message.setMessage(messageEntity);
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
        List<DocumentSnapshot> snapshots = mock(List.class);
        given(collectionAdapter.listAllCollectionDocumentSnapshots(anyString())).willReturn(snapshots);
        Iterator<DocumentSnapshot> mockIterator = mock(Iterator.class);
        given(snapshots.iterator()).willReturn(mockIterator);
        given(mockIterator.hasNext()).willReturn(true, false);
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        given(mockIterator.next()).willReturn(documentSnapshot);
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

    private void verifyAddForumToTag(String forumName) throws DatabaseAccessException, DocumentException {
        Map<String, Object> data = new HashMap<>();
        data.put("forums", FieldValue.arrayUnion(forumName));
        verify(documentAdapter).getDocumentSnapshot(eq(tagPath));
        verify(documentAdapter).setDocumentFields(eq(tagPath), eq(data), same(batch));
    }

    private void verifySaveForumName(String forumName) {
        String forumNamePath = Path.ofCollection(Collections.forumsNames, groupName);
        Map<String, Object> docData = new HashMap<>();
        docData.put("forum", forumId);
        verify(documentAdapter).createDocumentWithId(eq(forumNamePath), eq(forumName), eq(docData), same(batch));
    }

    private void mockAddForumToTag() throws DatabaseAccessException, DocumentException {
        given(documentAdapter.getDocumentSnapshot(anyString())).willReturn(null);
        willDoNothing().given(documentAdapter).setDocumentFields(anyString(), anyMap(), any(WriteBatch.class));
    }

    private void mockGetGroupAndForumIds() throws DatabaseAccessException, DocumentException {
        given(groupDao.getGroupId(anyString())).willReturn(groupId);
        given(documentAdapter.getStringFromDocument(anyString(), anyString())).willReturn(forumId);
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

                String groupPath = Path.ofDocument(Collections.groupsNames, groupName);
                verify(documentAdapter).getStringFromDocument(eq(groupPath), eq("group"));
                verify(documentAdapter).createDocument(eq(forumsPath), same(forumEntity), same(batch));
                verifySaveForumName(forumName);
            }

            @Test
            public void updateTags() throws DatabaseAccessException, DocumentException {
                mockGetGroupAndForumIds();
                willDoNothing().given(documentAdapter).updateDocumentFields(anyString(), anyString(),
                    any(FieldValue.class), any(WriteBatch.class));
                mockAddForumToTag();

                dao.updateTags(groupName, forumName, tags, tags);
                verify(documentAdapter).updateDocumentFields(eq(forumPath), eq("tags"),
                    eq(FieldValue.arrayRemove(tags.toArray())), same(batch));
                verify(documentAdapter).updateDocumentFields(eq(tagPath), eq("forums"),
                    eq(FieldValue.arrayRemove(forumName)), same(batch));
                verify(documentAdapter).updateDocumentFields(eq(forumPath), eq("tags"),
                    eq(FieldValue.arrayUnion(tags.toArray())), same(batch));
                verifyAddForumToTag(forumName);
            }

            @Test
            public void postMessage() throws DatabaseAccessException, DocumentException {
                mockGetGroupAndForumIds();
                given(documentAdapter.createDocument(anyString(), any(MessageEntity.class), any(WriteBatch.class))).willReturn(
                    documentReference);
                String messageId = "1";
                given(documentReference.getId()).willReturn(messageId);
                String imagePath = "some-path";
                given(storageDao.uploadPostImage(anyString(), anyString(), any(ImageEntity.class))).willReturn(
                    imagePath);

                ImageEntity image = new ImageEntity();
                message.setImage(image);
                dao.postMessage(groupName, forumName, message);
                verify(documentAdapter).createDocument(eq(messagePath), eq(messageEntity), same(batch));
                verify(storageDao).uploadPostImage(same(groupName), same(forumName), same(image));
                verify(documentAdapter).updateDocumentFields(same(batch),
                    eq(Path.ofDocument(Collections.messages, groupId, forumId, messageId)), eq("imagePath"),
                    eq(imagePath));
            }

            @Nested
            class UsesQuery {

                @BeforeEach
                public void setUp() throws ExecutionException, InterruptedException {
                    given(query.get()).willReturn(querySnapshot);
                    List<QueryDocumentSnapshot> mockList = mock(List.class);
                    given(querySnapshot.getDocuments()).willReturn(mockList);
                    Iterator<QueryDocumentSnapshot> mockIterator = mock(Iterator.class);
                    given(mockList.iterator()).willReturn(mockIterator);
                    given(mockIterator.hasNext()).willReturn(true, false);
                    given(mockIterator.next()).willReturn(documentSnapshot);
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
                    String forumNamePath = Path.ofDocument(Collections.forumsNames, groupName, forumName);
                    verify(documentAdapter).getStringFromDocument(eq(forumNamePath), eq("forum"));
                    verify(documentAdapter).deleteDocument(eq(forumPath), same(batch));
                    verify(documentAdapter).deleteDocument(eq(forumNamePath), same(batch));
                    verify(collectionAdapter).getDocumentsWhereArrayContains(eq(tagsPath), eq("forums"),
                        same(forumName));
                    Map<String, Object> data = new HashMap<>();
                    data.put("forums", FieldValue.arrayRemove(forumName));
                    verify(batch).update(same(documentReference), eq(data));
                }

                @Test
                public void updateName() throws DatabaseAccessException, DocumentException {
                    mockGetGroupAndForumIds();
                    given(documentAdapter.documentExists(anyString())).willReturn(false);
                    willDoNothing().given(documentAdapter).updateDocumentFields(anyString(), anyString(), anyString(),
                        any(WriteBatch.class));
                    willDoNothing().given(documentAdapter).deleteDocument(anyString(), any(WriteBatch.class));
                    willDoNothing().given(documentAdapter).updateDocumentFields(anyString(), anyString(),
                        any(FieldValue.class), any(WriteBatch.class));
                    mockAddForumToTag();
                    given(
                        documentAdapter.createDocumentWithId(anyString(), anyString(), anyMap(), any(WriteBatch.class)))
                        .willReturn(documentReference);
                    given(collectionAdapter.getDocumentsWhereArrayContains(anyString(), anyString(), anyString()))
                        .willReturn(query);
                    given(documentSnapshot.getId()).willReturn("dogs");

                    String newName = "German Shepherds";
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

                    dao.deleteMessage(groupName, forumName, "John", "2020-05-01T17:48:15");
                    verify(collectionAdapter).getDocumentsWhereEqualTo(
                        eq(Path.ofCollection(Collections.messages, groupId, forumId)), eq("creator"), eq("John"),
                        eq("publicationDate"), eq("2020-05-01T17:48:15"));
                    verify(storageDao).deleteImageByName("some-path");
                    verify(batch).delete(documentReference);
                }
            }
        }

    }
}
