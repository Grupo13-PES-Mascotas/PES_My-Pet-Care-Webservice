package org.pesmypetcare.webservice.dao.communitymanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
    private ForumEntity forumEntity;

    @Mock
    private GroupDao groupDao;
    @Mock
    private FirestoreDocument documentAdapter;
    @Mock
    private FirestoreCollection collectionAdapter;
    @Mock
    private WriteBatch batch;
    @Mock
    private ApiFuture<List<WriteResult>> writeResults;
    @Mock
    private DocumentReference documentReference;
    @Mock
    private ApiFuture<QuerySnapshot> query;
    @Mock
    private QuerySnapshot querySnapshot;

    @InjectMocks
    private ForumDao dao = new ForumDaoImpl();

    @BeforeEach
    public void setUp() {
        groupName = "Dogs";
        forumName = "Huskies";
        forumEntity = new ForumEntity();
        forumEntity.setName(forumName);
        List<String> tags = new LinkedList<>();
        tags.add("dogs");
        forumEntity.setTags(tags);
    }

    @Test
    public void forumNameInUseShouldReturnTrueWhenTheNameAlreadyExistsInTheGroup()
        throws DatabaseAccessException, DocumentException {
        given(documentAdapter.getDocumentSnapshot(anyString())).willReturn(null);

        assertTrue(dao.forumNameInUse(groupName, forumName),
                   "Should return true when the forum name is already in use in the specified group.");
    }

    @Test
    public void forumNameInUseShouldReturnFalseWhenTheNameDoesNotExistInTheGroup()
        throws DatabaseAccessException, DocumentException {
        willThrow(DocumentException.class).given(documentAdapter).getDocumentSnapshot(anyString());

        assertFalse(dao.forumNameInUse(groupName, forumName),
                    "Should return false when the forum name is not in use in the specified group.");
    }

    @Nested
    class UsesWriteBatch {

        private String groupId;
        private String forumId;

        @BeforeEach
        public void setUp() {
            given(documentAdapter.batch()).willReturn(batch);
            groupId = "asd2d9833jdaA3";
            forumId = "ad33i8jf93";
        }

        @Test
        public void createForumShouldThrowDocumentExceptionWhenTheGroupDoesNotExist()
            throws DatabaseAccessException, DocumentException {
            willThrow(DocumentException.class).given(documentAdapter).getStringFromDocument(anyString(), anyString());
            assertThrows(DocumentException.class, () -> dao.createForum(groupName, forumEntity),
                         "Create forum should" + " fail when the group does not exist.");
        }

        @Nested
        class CommitsBatch {
            @BeforeEach
            public void setUp() {
                given(batch.commit()).willReturn(writeResults);
                given(writeResults.isCancelled()).willReturn(false);
            }

            @Test
            public void createForum() throws DatabaseAccessException, DocumentException {
                given(documentAdapter.getStringFromDocument(anyString(), anyString())).willReturn(groupId);
                given(documentAdapter.createDocument(anyString(), any(ForumEntity.class), any(WriteBatch.class)))
                    .willReturn(documentReference);
                given(documentAdapter.createDocumentWithId(anyString(), anyString(), anyMap(), any(WriteBatch.class)))
                    .willReturn(documentReference);
                given(documentAdapter.getDocumentSnapshot(anyString())).willReturn(null);
                willDoNothing().given(documentAdapter).setDocumentFields(anyString(), anyMap(), any(WriteBatch.class));
                given(documentReference.getId()).willReturn(forumId);


                dao.createForum(groupName, forumEntity);

                String tagPath = Path.ofDocument(Collections.tags, "dogs");
                Map<String, Object> data = new HashMap<>();
                data.put("forums", FieldValue.arrayUnion(forumName));
                verify(documentAdapter).getDocumentSnapshot(eq(tagPath));
                verify(documentAdapter).setDocumentFields(eq(tagPath), eq(data), same(batch));

                String groupPath = Path.ofDocument(Collections.groupsNames, groupName);
                verify(documentAdapter).getStringFromDocument(eq(groupPath), eq("group"));
                String forumPath = Path.ofCollection(Collections.forums, groupId);
                verify(documentAdapter).createDocument(eq(forumPath), same(forumEntity), same(batch));
                String forumNamePath = Path.ofCollection(Collections.forumsNames, groupName);
                Map<String, Object> docData = new HashMap<>();
                docData.put("forum", forumId);
                verify(documentAdapter)
                    .createDocumentWithId(eq(forumNamePath), eq(forumName), eq(docData), same(batch));
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
                    QueryDocumentSnapshot documentSnapshot = mock(QueryDocumentSnapshot.class);
                    given(mockIterator.next()).willReturn(documentSnapshot);
                    given(documentSnapshot.getReference()).willReturn(documentReference);
                }

                @Test
                public void deleteForum() throws DatabaseAccessException, DocumentException {
                    given(groupDao.getGroupId(anyString())).willReturn(groupId);
                    given(documentAdapter.getStringFromDocument(anyString(), anyString())).willReturn(forumId);
                    willDoNothing().given(documentAdapter).deleteDocument(anyString(), any(WriteBatch.class));
                    given(
                        collectionAdapter.getDocumentsWhereArrayContains(anyString(), anyString(), anyString(), any()))
                        .willReturn(query);
                    given(batch.update(any(DocumentReference.class), anyMap())).willReturn(batch);

                    dao.deleteForum(groupName, forumName);
                    verify(groupDao).getGroupId(same(groupName));
                    String forumNamePath = Path.ofDocument(Collections.forumsNames, groupName, forumName);
                    verify(documentAdapter).getStringFromDocument(eq(forumNamePath), eq("forum"));
                    String forumPath = Path.ofDocument(Collections.forums, groupId, forumId);
                    verify(documentAdapter).deleteDocument(eq(forumPath), same(batch));
                    verify(documentAdapter).deleteDocument(eq(forumNamePath), same(batch));
                    String tagsPath = Path.ofCollection(Collections.tags);
                    verify(collectionAdapter)
                        .getDocumentsWhereArrayContains(eq(tagsPath), eq("forums"), same(forumName));
                    Map<String, Object> data = new HashMap<>();
                    data.put("forums", FieldValue.arrayRemove(forumName));
                    verify(batch).update(same(documentReference), eq(data));
                }

            }
        }

    }

    @Test
    public void getForum() {
    }

    @Test
    public void getAllForumsFromGroup() {
    }

    @Test
    public void updateName() {
    }

    @Test
    public void updateTags() {
    }

    @Test
    public void postMessage() {
    }

    @Test
    public void deleteMessage() {
    }

    @Test
    public void getForumIdShouldThrowDocumentExceptionWhenWhenTheForumDoesNotExist() {

    }
}
