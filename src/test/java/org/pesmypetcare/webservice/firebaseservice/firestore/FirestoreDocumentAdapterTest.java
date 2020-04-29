package org.pesmypetcare.webservice.firebaseservice.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class FirestoreDocumentAdapterTest {
    private String documentPath;
    private String collectionPath;
    private Map<String, Object> fields;
    private String documentId;
    private UserEntity pojo;
    @Mock
    private Firestore db;
    @Mock
    private DocumentReference documentReference;
    @Mock
    private CollectionReference collectionReference;
    @Mock
    private DocumentSnapshot documentSnapshot;
    @Mock
    private ApiFuture<DocumentSnapshot> future;
    @Mock
    private WriteBatch batch;
    @InjectMocks
    private FirestoreDocument adapter = new FirestoreDocumentAdapter();

    @Nested
    class UsesDbDocument {
        @BeforeEach
        public void setUp() {
            documentId = "1231DADWAqsd2";
            collectionPath = "users";
            documentPath = collectionPath + "/" + documentId;
            fields = new HashMap<>();
            fields.put("username", "John");
            pojo = new UserEntity();
            given(db.document(anyString())).willReturn(documentReference);
        }
        
        @Test
        public void getDocumentReference() {
            DocumentReference result = adapter.getDocumentReference(documentPath);
            assertEquals(documentReference, result, "Should return the document reference");
        }

        @Test
        public void getDocumentId() {
            given(documentReference.getId()).willReturn(documentId);

            String result = adapter.getDocumentId(documentPath);
            assertEquals(documentId, result, "Should return the document id");
        }

        @Test
        public void getDocumentParent() {
            given(documentReference.getParent()).willReturn(collectionReference);

            CollectionReference result = adapter.getDocumentParent(documentPath);
            assertEquals(collectionReference, result, "Should return the parent reference");
        }

        @Test
        public void getDocumentSnapshot() throws DatabaseAccessException, DocumentException {
            given(documentReference.get()).willReturn(future);
            try {
                given(future.get()).willReturn(documentSnapshot);
                given(documentSnapshot.exists()).willReturn(true);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new DatabaseAccessException("retrieval-failed", "The document could not be retrieves");
            }

            DocumentSnapshot result = adapter.getDocumentSnapshot(documentPath);
            assertEquals(documentSnapshot, result, "Should return the document snapshot");
        }

        @Test
        public void shouldReturnDatabaseAccessExceptionWhenRetrievalFails() {
            assertThrows(DatabaseAccessException.class, () -> {
                given(documentReference.get()).willReturn(future);
                willThrow(InterruptedException.class).given(future).get();
                
                adapter.getDocumentSnapshot(documentPath);
            });
        }

        @Test
        public void shouldReturnDocumentExceptionWhenDocumentDoesNotExist() {
            assertThrows(DocumentException.class, () -> {
                given(documentReference.get()).willReturn(future);
                given(future.get()).willReturn(documentSnapshot);
                given(documentSnapshot.exists()).willReturn(false);
                
                adapter.getDocumentSnapshot(documentPath);
            });
        }

        @Test
        public void createDocumentWithId() {
            given(batch.create(same(documentReference), same(fields))).willReturn(batch);

            adapter.createDocumentWithId(collectionPath, documentId, fields, batch);
            verify(db).document(collectionPath + "/" + documentId);
            verify(batch).create(same(documentReference), same(fields));
        }

        @Test
        public void createDocumentWithIdFromPojo() {
            given(batch.create(same(documentReference), same(pojo))).willReturn(batch);

            adapter.createDocumentWithId(collectionPath, documentId, pojo, batch);
            verify(db).document(same(collectionPath + "/" + documentId));
            verify(batch).create(same(documentReference), same(pojo));
        }

        @Test
        public void setDocumentFields() {
            given(batch.set(same(documentReference), same(fields))).willReturn(batch);

            adapter.setDocumentFields(documentPath, fields, batch);
            verify(batch).set(same(documentReference), same(fields));
        }

        @Test
        public void setDocumentFieldsFromPojo() {
            given(batch.set(same(documentReference), same(pojo))).willReturn(batch);

            adapter.setDocumentFields(documentPath, pojo, batch);
            verify(batch).set(same(documentReference), same(pojo));
        }

        @Test
        public void updateDocumentFields() {
            given(batch.update(same(documentReference), same(fields))).willReturn(batch);

            adapter.updateDocumentFields(documentPath, fields, batch);
            verify(batch).update(same(documentReference), same(fields));
        }

        /*@Test
        public void deleteDocument() {
            Iterable<CollectionReference> collectionReferences = mock(Iterable.class);
            given(documentReference.listCollections()).willReturn(collectionReferences);

        }*/
    }

    @Test
    public void documentSnapshotExists() {
        given(documentSnapshot.exists()).willReturn(true);
        assertTrue(adapter.documentSnapshotExists(documentSnapshot), "Should return true when the document snapshot "
            + "exists");
    }

    @Test
    public void createDocument() {
        given(db.collection(collectionPath)).willReturn(collectionReference);
        given(collectionReference.document()).willReturn(documentReference);
        given(batch.create(same(documentReference), same(fields))).willReturn(batch);

        adapter.createDocument(collectionPath, fields, batch);
        verify(collectionReference).document();
        verify(batch).create(same(documentReference), same(fields));
    }

    @Test
    public void createDocumentFromPojo() {
        given(db.collection(collectionPath)).willReturn(collectionReference);
        given(collectionReference.document()).willReturn(documentReference);
        given(batch.create(same(documentReference), same(pojo))).willReturn(batch);

        adapter.createDocument(collectionPath, pojo, batch);
        verify(collectionReference).document();
        verify(batch).create(same(documentReference), same(pojo));
    }

    @Test
    public void getDocumentField() {
    }

    @Test
    public void testGetDocumentField() {
    }

    @Test
    public void getDoubleFromDocument() {
    }

    @Test
    public void getDateFromDocument() {
    }

    @Test
    public void getBooleanFromDocument() {
    }

    @Test
    public void getGeoPointFromDocument() {
    }

    @Test
    public void getStringFromDocument() {
    }

    @Test
    public void getTimestampFromDocument() {
    }

    @Test
    public void getDocumentData() {
    }

    @Test
    public void getDocumentDataAsObject() {
    }

    @Test
    public void documentContains() {
    }

    @Test
    public void testDocumentContains() {
    }
}
