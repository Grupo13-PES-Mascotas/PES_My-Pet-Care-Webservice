package org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.lenient;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class FirestoreDocumentAdapterTest {
    private String documentPath;
    private String collectionPath;
    private Map<String, Object> fields;
    private String documentId;
    private String field;
    private String aString;
    private UserEntity pojo;
    private FieldPath fieldPath;
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
    private ApiFuture<List<WriteResult>> writeResult;
    @Mock
    private WriteBatch batch;
    @Mock
    private Iterable<DocumentReference> documentReferences;
    @Mock
    private Iterable<CollectionReference> collectionReferences;

    @InjectMocks
    private FirestoreDocument adapter = new FirestoreDocumentAdapter();

    @BeforeEach
    public void setUp() {
        collectionPath = "users";
        fields = new HashMap<>();
        field = "someField";
        aString = "John";
        fields.put(field, aString);
        pojo = new UserEntity();
    }

    @Test
    public void batch() {
        given(db.batch()).willReturn(batch);

        assertEquals(batch, adapter.batch(), "Should return a write batch.");
    }

    @Test
    public void commitWriteBatch()
        throws ExecutionException, InterruptedException, DatabaseAccessException, DocumentException {
        given(batch.commit()).willReturn(writeResult);
        given(writeResult.get()).willReturn(null);

        adapter.commitBatch(batch);
        verify(batch).commit();
        verify(writeResult).get();
    }

    @Test
    public void commitWriteBatchShouldReturnDatabaseAccessExceptionWhenInterrupted()
        throws ExecutionException, InterruptedException {
        given(batch.commit()).willReturn(writeResult);
        willThrow(InterruptedException.class).given(writeResult).get();

        assertThrows(DatabaseAccessException.class, () -> adapter.commitBatch(batch),
            "Should throw database access " + "exception when commit is interrupted");
    }

    @Test
    public void commitWriteBatchShouldReturnDocumentExceptionWhenExecutionFails()
        throws ExecutionException, InterruptedException {
        given(batch.commit()).willReturn(writeResult);
        willThrow(ExecutionException.class).given(writeResult).get();

        assertThrows(DocumentException.class, () -> adapter.commitBatch(batch),
            "Should throw document " + "exception when commit execution fails");
    }

    @Test
    public void documentSnapshotExists() {
        given(documentSnapshot.exists()).willReturn(true);

        assertTrue(adapter.documentSnapshotExists(documentSnapshot),
            "Should return true when the document snapshot " + "exists");
    }

    @Test
    public void createDocument() {
        given(db.collection(anyString())).willReturn(collectionReference);
        given(collectionReference.document()).willReturn(documentReference);
        given(batch.create(any(DocumentReference.class), anyMap())).willReturn(batch);

        DocumentReference ref = adapter.createDocument(collectionPath, fields, batch);
        verify(collectionReference).document();
        verify(batch).create(same(documentReference), anyMap());
        assertEquals(documentReference, ref, "Should return the document reference created");
    }

    @Test
    public void createDocumentWithoutBatch() {
        given(db.collection(anyString())).willReturn(collectionReference);
        given(collectionReference.document()).willReturn(documentReference);
        given(documentReference.create(anyMap())).willReturn(null);

        DocumentReference ref = adapter.createDocument(collectionPath, fields);
        verify(collectionReference).document();
        verify(documentReference).create(same(fields));
        assertEquals(documentReference, ref, "Should return the document reference created");
    }

    @Test
    public void createDocumentFromPojo() {
        given(db.collection(anyString())).willReturn(collectionReference);
        given(collectionReference.document()).willReturn(documentReference);
        given(batch.create(any(DocumentReference.class), any(pojo.getClass()))).willReturn(batch);

        DocumentReference ref = adapter.createDocument(collectionPath, pojo, batch);
        verify(collectionReference).document();
        verify(batch).create(same(documentReference), same(pojo));
        assertEquals(documentReference, ref, "Should return the document reference created");
    }

    @Test
    public void createDocumentFromPojoWithoutBatch() {
        given(db.collection(anyString())).willReturn(collectionReference);
        given(collectionReference.document()).willReturn(documentReference);
        given(documentReference.create(any(pojo.getClass()))).willReturn(null);

        DocumentReference ref = adapter.createDocument(collectionPath, pojo);
        verify(collectionReference).document();
        verify(documentReference).create(same(pojo));
        assertEquals(documentReference, ref, "Should return the document reference created");
    }

    @Nested
    class UsesDbDocument {
        @BeforeEach
        public void setUp() {
            documentId = "1231DADWAqsd2";
            documentPath = collectionPath + "/" + documentId;
            fieldPath = FieldPath.of(field);
            given(db.document(anyString())).willReturn(documentReference);
        }

        @AfterEach
        public void afterEach() {
            verify(db).document(documentPath);
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
        public void createDocumentWithId() {
            given(batch.create(any(DocumentReference.class), anyMap())).willReturn(batch);

            DocumentReference ref = adapter.createDocumentWithId(collectionPath, documentId, fields, batch);
            verify(db).document(collectionPath + "/" + documentId);
            verify(batch).create(same(documentReference), same(fields));
            assertEquals(documentReference, ref, "Should return the document reference created");
        }

        @Test
        public void createDocumentWithIdWithoutBatch() {
            given(documentReference.create(anyMap())).willReturn(null);

            DocumentReference ref = adapter.createDocumentWithId(collectionPath, documentId, fields);
            verify(db).document(collectionPath + "/" + documentId);
            verify(documentReference).create(same(fields));
            assertEquals(documentReference, ref, "Should return the document reference created");
        }

        @Test
        public void createDocumentWithIdFromPojo() {
            given(batch.create(any(DocumentReference.class), any(pojo.getClass()))).willReturn(batch);

            adapter.createDocumentWithId(collectionPath, documentId, pojo, batch);
            verify(db).document(collectionPath + "/" + documentId);
            verify(batch).create(same(documentReference), same(pojo));
        }

        @Test
        public void createDocumentWithIdFromPojoWithoutBatch() {
            given(documentReference.create(any(pojo.getClass()))).willReturn(null);

            DocumentReference ref = adapter.createDocumentWithId(collectionPath, documentId, pojo);
            verify(db).document(collectionPath + "/" + documentId);
            verify(documentReference).create(same(pojo));
            assertEquals(documentReference, ref, "Should return the document reference created");
        }

        @Test
        public void documentExists() throws ExecutionException, InterruptedException, DatabaseAccessException {
            given(documentReference.get()).willReturn(future);
            given(future.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(true);

            assertTrue(adapter.documentExists(documentPath), "Should return true if the document exists.");
        }

        @Test
        public void documentExistsShouldFailIfTheRetrievalIsInterrupted()
            throws ExecutionException, InterruptedException {
            given(documentReference.get()).willReturn(future);
            willThrow(InterruptedException.class).given(future).get();

            assertThrows(DatabaseAccessException.class, () -> adapter.documentExists(documentPath),
                "Should fail if the retrieval is interrupted.");
        }

        @Test
        public void documentExistsShouldFailIfTheRetrievalExecutionFails()
            throws ExecutionException, InterruptedException {
            given(documentReference.get()).willReturn(future);
            willThrow(ExecutionException.class).given(future).get();

            assertThrows(DatabaseAccessException.class, () -> adapter.documentExists(documentPath),
                "Should fail if the retrieval execution fails.");
        }

        @Test
        public void setDocumentFields() {
            given(batch.set(any(DocumentReference.class), anyMap())).willReturn(batch);

            adapter.setDocumentFields(documentPath, fields, batch);
            verify(batch).set(same(documentReference), same(fields));
        }

        @Test
        public void setDocumentFieldsWithoutBatch() {
            given(documentReference.set(anyMap())).willReturn(null);

            adapter.setDocumentFields(documentPath, fields);
            verify(documentReference).set(same(fields));
        }

        @Test
        public void setDocumentFieldsFromPojo() {
            given(batch.set(any(DocumentReference.class), any(pojo.getClass()))).willReturn(batch);

            adapter.setDocumentFields(documentPath, pojo, batch);
            verify(batch).set(same(documentReference), same(pojo));
        }

        @Test
        public void setDocumentFieldsFromPojoWithoutBatch() {
            given(documentReference.set(any(pojo.getClass()))).willReturn(null);

            adapter.setDocumentFields(documentPath, pojo);
            verify(documentReference).set(same(pojo));
        }

        @Test
        public void updateDocumentFieldsFromMap() {
            given(batch.update(any(DocumentReference.class), anyMap())).willReturn(batch);

            adapter.updateDocumentFields(documentPath, fields, batch);
            verify(batch).update(same(documentReference), same(fields));
        }

        @Test
        public void updateDocumentFieldsFromMapWithoutBatch() {
            given(documentReference.update(anyMap())).willReturn(null);

            adapter.updateDocumentFields(documentPath, fields);
            verify(documentReference).update(same(fields));
        }

        @Test
        public void updateDocumentFieldsFromFieldPath() {
            given(batch.update(any(DocumentReference.class), any(FieldPath.class), any())).willReturn(batch);

            adapter.updateDocumentFields(batch, documentPath, fieldPath, aString);
            verify(batch).update(same(documentReference), same(fieldPath), same(aString));
        }

        @Test
        public void updateDocumentFieldsFromFieldPathWithoutBatch() {
            given(documentReference.update(any(FieldPath.class), any())).willReturn(null);

            adapter.updateDocumentFields(documentPath, fieldPath, aString);
            verify(documentReference).update(same(fieldPath), same(aString));
        }

        @Test
        public void updateDocumentFields() {
            given(batch.update(any(DocumentReference.class), anyString(), any())).willReturn(batch);

            adapter.updateDocumentFields(batch, documentPath, field, aString);
            verify(batch).update(same(documentReference), same(field), same(aString));
        }

        @Test
        public void updateDocumentFieldsWithoutBatch() {
            given(documentReference.update(anyString(), any())).willReturn(null);

            adapter.updateDocumentFields(documentPath, field, aString);
            verify(documentReference).update(same(field), same(aString));
        }

        @Test
        public void shouldReturnDatabaseAccessExceptionWhenRetrievalInterruptedOrItsExecutionFails() {
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

        @Nested
        class RecursiveOperations {
            @BeforeEach
            public void setUp() {
                given(documentReference.listCollections()).willReturn(collectionReferences);
                Iterator collectionIterator = mock(Iterator.class);
                given(collectionReferences.iterator()).willReturn(collectionIterator);
                given(collectionIterator.hasNext()).willReturn(true, false, false);
                given(collectionIterator.next()).willReturn(collectionReference);
                given(collectionReference.listDocuments()).willReturn(documentReferences);
                Iterator documentIterator = mock(Iterator.class);
                given(documentReferences.iterator()).willReturn(documentIterator);
                given(documentIterator.hasNext()).willReturn(true, false);
                given(documentIterator.next()).willReturn(documentReference);
            }

            @Test
            public void deleteDocument() {
                given(batch.delete(any(DocumentReference.class))).willReturn(batch);

                adapter.deleteDocument(documentPath, batch);
                verify(batch, times(2)).delete(documentReference);
            }

            @Test
            public void deleteDocumentWithoutBatch() {
                given(documentReference.delete()).willReturn(null);

                adapter.deleteDocument(documentPath);
                verify(documentReference, times(2)).delete();
            }
        }

        @Nested
        class UsesFuture {
            private double aDouble;
            private Date aDate;
            private boolean aBoolean;
            private GeoPoint aGeoPoint;
            private Timestamp aTimestamp;

            @BeforeEach
            public void setUp() throws DatabaseAccessException {
                given(documentReference.get()).willReturn(future);
                try {
                    given(future.get()).willReturn(documentSnapshot);
                    given(documentSnapshot.exists()).willReturn(true);
                } catch (InterruptedException | ExecutionException e) {
                    throw new DatabaseAccessException("retrieval-failed", "The document could not be retrieves");
                }

                aDouble = 2.0;
                aDate = new Date();
                aBoolean = true;
                aGeoPoint = new GeoPoint(2.0, 1.0);
                aTimestamp = Timestamp.now();
                fieldPath = FieldPath.of(field);
            }

            @Test
            public void getDocumentSnapshot() throws DatabaseAccessException, DocumentException {
                DocumentSnapshot result = adapter.getDocumentSnapshot(documentPath);
                assertEquals(documentSnapshot, result, "Should return the document snapshot");
            }

            @Test
            public void getDocumentField() throws DatabaseAccessException, DocumentException {
                willReturn(aString).given(documentSnapshot).get(anyString());

                Object result = adapter.getDocumentField(documentPath, field);
                assertEquals(aString, result,
                    "Should return " + aString + " as the value of the " + field + " in the " + "document located at "
                        + documentPath);
            }

            @Test
            public void getDocumentFieldFromFieldPath() throws DatabaseAccessException, DocumentException {
                lenient().when(documentSnapshot.get(fieldPath)).thenReturn(aString);

                Object result = adapter.getDocumentField(documentPath, fieldPath);
                assertEquals(aString, result,
                    "Should return " + aString + " as the value in " + fieldPath + " in the " + "document located at "
                        + documentPath);
            }

            @Test
            public void getDoubleFromDocument() throws DatabaseAccessException, DocumentException {
                given(documentSnapshot.getDouble(anyString())).willReturn(aDouble);

                Double result = adapter.getDoubleFromDocument(documentPath, field);
                assertEquals(aDouble, result,
                    "Should return " + aDouble + " as the value of the " + field + " in the " + "document located at "
                        + documentPath);
            }

            @Test
            public void getDateFromDocument() throws DatabaseAccessException, DocumentException {
                given(documentSnapshot.getDate(anyString())).willReturn(aDate);

                Date result = adapter.getDateFromDocument(documentPath, field);
                assertEquals(aDate, result,
                    "Should return " + aDate + " as the value of the " + field + " in the " + "document located at "
                        + documentPath);
            }

            @Test
            public void getBooleanFromDocument() throws DatabaseAccessException, DocumentException {
                given(documentSnapshot.getBoolean(anyString())).willReturn(aBoolean);

                Boolean result = adapter.getBooleanFromDocument(documentPath, field);
                assertEquals(aBoolean, result,
                    "Should return " + aBoolean + " as the value of the " + field + " in the " + "document located at "
                        + documentPath);
            }

            @Test
            public void getGeoPointFromDocument() throws DatabaseAccessException, DocumentException {
                given(documentSnapshot.getGeoPoint(anyString())).willReturn(aGeoPoint);

                GeoPoint result = adapter.getGeoPointFromDocument(documentPath, field);
                assertEquals(aGeoPoint, result,
                    "Should return " + aGeoPoint + " as the value of the " + field + " in the " + "document located at "
                        + documentPath);
            }

            @Test
            public void getStringFromDocument() throws DatabaseAccessException, DocumentException {
                given(documentSnapshot.getString(anyString())).willReturn(aString);

                String result = adapter.getStringFromDocument(documentPath, field);
                assertEquals(aString, result,
                    "Should return " + aString + " as the value of the " + field + " in the " + "document located at "
                        + documentPath);
            }

            @Test
            public void getTimestampFromDocument() throws DatabaseAccessException, DocumentException {
                given(documentSnapshot.getTimestamp(anyString())).willReturn(aTimestamp);

                Timestamp result = adapter.getTimestampFromDocument(documentPath, field);
                assertEquals(aTimestamp, result,
                    "Should return " + aTimestamp + " as the value of the " + field + " in the "
                        + "document located at " + documentPath);
            }

            @Test
            public void getDocumentData() throws DatabaseAccessException, DocumentException {
                given(documentSnapshot.getData()).willReturn(fields);

                Map<String, Object> result = adapter.getDocumentData(documentPath);
                assertEquals(fields, result,
                    "Should return " + fields + " as the data stored in the document located at " + documentPath);
            }

            @Test
            public void getDocumentDataAsObject() throws DatabaseAccessException, DocumentException {
                given(documentSnapshot.toObject(any())).willReturn(pojo);

                UserEntity result = adapter.getDocumentDataAsObject(documentPath, UserEntity.class);
                assertEquals(pojo, result,
                    "Should return a pojo of type " + pojo.getClass().getSimpleName() + " as the data "
                        + "stored in the document located at " + documentPath);
            }

            @Test
            public void documentContains() throws DatabaseAccessException, DocumentException {
                given(documentSnapshot.contains(anyString())).willReturn(true);

                assertTrue(adapter.documentContains(documentPath, field),
                    "Should return true if the document contains the field " + field);
            }

            @Test
            public void documentContainsFieldPath() throws DatabaseAccessException, DocumentException {
                lenient().when(documentSnapshot.contains(fieldPath)).thenReturn(true);

                assertTrue(adapter.documentContains(documentPath, fieldPath),
                    "Should return true if the document contains the field in " + fieldPath);
            }
        }
    }
}
