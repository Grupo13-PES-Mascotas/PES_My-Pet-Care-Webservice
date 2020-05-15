package org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
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
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class FirestoreCollectionAdapterTest {
    private String collectionId = "as2asdh34hg";
    private String collectionPath = "users";
    private String collectionGroup = "forums";
    private int value = 2;
    private String field = "number";
    private String value2 = "some text";
    private String field2 = "text";
    private String array = "numbers";
    private String array2 = "texts";
    private FieldPath fieldPath = FieldPath.of(field);
    private FieldPath fieldPath2 = FieldPath.of(field2);
    private FieldPath arrayPath = FieldPath.of(array);

    @Mock
    private Firestore db;
    @Mock
    private FirestoreDocument documentAdapter;
    @Mock
    private CollectionReference collectionReference;
    @Mock
    private DocumentReference documentReference;
    @Mock
    private Iterable<DocumentReference> documentReferences;
    @Mock
    private Iterable<CollectionReference> collectionReferences;
    @Mock
    private Query query;
    @Mock
    private ApiFuture<QuerySnapshot> apiFuture;
    @Mock
    private WriteBatch batch;
    @Mock
    private ApiFuture<List<WriteResult>> writeResult;

    @InjectMocks
    private FirestoreCollection adapter = new FirestoreCollectionAdapter();

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
    public void checkArgumentsShouldThrowExceptionWhenOddNumberOfArguments() {
        assertThrows(IllegalArgumentException.class,
            () -> adapter.getDocumentsWhereEqualTo(collectionGroup, arrayPath, value, arrayPath));
    }

    @Nested
    class UseDbCollection {


        @BeforeEach
        public void setUp() {
            given(db.collection(same(collectionPath))).willReturn(collectionReference);
        }

        @Test
        public void getCollectionReference() {
            CollectionReference reference = adapter.getCollectionReference(collectionPath);
            assertEquals(collectionReference, reference, "Should return the collection reference");
        }

        @Test
        public void getCollectionId() {
            given(collectionReference.getId()).willReturn(collectionId);
            String id = adapter.getCollectionId(collectionPath);
            assertEquals(collectionId, id, "Should return the collection id");
        }

        @Test
        public void getCollectionParent() {
            given(collectionReference.getParent()).willReturn(documentReference);
            DocumentReference reference = adapter.getCollectionParent(collectionPath);
            assertEquals(documentReference, reference, "Should return the parent document reference of the collection");
        }

        @Test
        public void listAllCollectionDocuments() {
            given(collectionReference.listDocuments()).willReturn(documentReferences);
            Iterable<DocumentReference> documents = adapter.listAllCollectionDocuments(collectionPath);
            assertEquals(documentReferences, documents, "Should return an iterable with all the collection documents");
        }

        @Test
        public void listAllCollectionDocumentSnapshots() throws DatabaseAccessException, DocumentException {
            given(collectionReference.listDocuments()).willReturn(documentReferences);
            Iterator documentIterator = mock(Iterator.class);
            given(documentReferences.iterator()).willReturn(documentIterator);
            given(documentIterator.hasNext()).willReturn(true, false);
            given(documentIterator.next()).willReturn(documentReference);
            given(documentReference.getPath()).willReturn(collectionPath);
            DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
            given(documentAdapter.getDocumentSnapshot(anyString())).willReturn(snapshot);

            List<DocumentSnapshot> snapshots = adapter.listAllCollectionDocumentSnapshots(collectionPath);
            List<DocumentSnapshot> expected = new ArrayList<>();
            expected.add(snapshot);

            assertEquals(expected, snapshots, "Should return a list with all the document snapshots in the collection");
        }

        @Test
        public void deleteCollection() {
            given(collectionReference.listDocuments()).willReturn(documentReferences);
            Iterator documentIterator = mock(Iterator.class);
            given(documentReferences.iterator()).willReturn(documentIterator);
            given(documentIterator.hasNext()).willReturn(true, false, false);
            given(documentIterator.next()).willReturn(documentReference);
            given(documentReference.listCollections()).willReturn(collectionReferences);
            Iterator collectionIterator = mock(Iterator.class);
            given(collectionReferences.iterator()).willReturn(collectionIterator);
            given(collectionIterator.hasNext()).willReturn(true, false);
            given(collectionIterator.next()).willReturn(collectionReference);
            given(collectionReference.getPath()).willReturn(collectionPath);
            WriteBatch mockBatch = mock(WriteBatch.class);
            given(mockBatch.delete(any(DocumentReference.class))).willReturn(mockBatch);

            adapter.deleteCollection(collectionPath, mockBatch);
            verify(mockBatch).delete(documentReference);
        }

        @Test
        public void getDocumentsWhereFieldEqualToValues() {
            given(collectionReference.whereEqualTo(same(field), same(value))).willReturn(query);
            given(query.get()).willReturn(apiFuture);
            ApiFuture<QuerySnapshot> result = adapter
                .getDocumentsWhereEqualTo(collectionPath, field, value, field2, value2);
            assertEquals(apiFuture, result,
                "Should return all documents from " + collectionPath + " where " + field + " is equals to " + value
                    + " and " + field2 + " is equals to " + value2);
        }

        @Test
        public void getDocumentsWhereFieldEqualToValuesShouldFailIfAnyExtraFieldIsEmpty() {
            given(collectionReference.whereEqualTo(same(field), same(value))).willReturn(query);
            assertThrows(IllegalArgumentException.class,
                () -> adapter.getDocumentsWhereEqualTo(collectionPath, field, value, "", value2),
                "Should fail if any of the fields in the varargs is empty");
        }

        @Test
        public void getDocumentsWhereFieldEqualToValuesShouldFailIfAnyExtraFieldIsNull() {
            given(collectionReference.whereEqualTo(same(field), same(value))).willReturn(query);
            assertThrows(IllegalArgumentException.class,
                () -> adapter.getDocumentsWhereEqualTo(collectionPath, field, value, null, value2),
                "Should fail if any of the fields in the varargs is null");
        }

        @Test
        public void getDocumentsWhereFieldFromFieldPathsEqualToValue() {
            given(collectionReference.whereEqualTo(same(fieldPath), same(value))).willReturn(query);
            given(query.get()).willReturn(apiFuture);
            ApiFuture<QuerySnapshot> result = adapter
                .getDocumentsWhereEqualTo(collectionPath, fieldPath, value, fieldPath2, value2);
            assertEquals(apiFuture, result,
                "Should return all documents from " + collectionPath + " where " + field + " in " + fieldPath
                    + " is equals to " + value + " and " + field2 + " in " + fieldPath2 + " is equals to " + value2);
        }

        @Test
        public void getDocumentsWhereFieldEqualToValuesFromFieldPathsShouldFailIfAnyExtraFieldPathIsNull() {
            given(collectionReference.whereEqualTo(same(fieldPath), same(value))).willReturn(query);
            assertThrows(IllegalArgumentException.class,
                () -> adapter.getDocumentsWhereEqualTo(collectionPath, fieldPath, value, null, value2),
                "Should fail if any of the field paths in the varargs is null");
        }

        @Test
        public void getDocumentsWhereArraysContainValue() {
            given(collectionReference.whereArrayContains(same(array), same(value))).willReturn(query);
            given(query.get()).willReturn(apiFuture);

            ApiFuture<QuerySnapshot> result = adapter
                .getDocumentsWhereArrayContains(collectionPath, array, value, array2, value2);
            assertEquals(apiFuture, result,
                "Should return all documents from " + collectionPath + " where the array " + array + " contains "
                    + value + " and " + array2 + " contains "
                    + value2);
        }

        @Test
        public void getDocumentsWhereArrayFromFieldPathContainsValue() {
            given(collectionReference.whereArrayContains(same(arrayPath), same(value))).willReturn(query);
            given(query.get()).willReturn(apiFuture);
            ApiFuture<QuerySnapshot> result = adapter.getDocumentsWhereArrayContains(collectionPath, arrayPath, value);
            assertEquals(apiFuture, result,
                "Should return all documents from " + collectionPath + " where the array" + array + " in " + arrayPath
                    + " contains " + value);
        }

    }

    @Nested
    class UsesCollectionGroup {

        @BeforeEach
        public void setUp() {
            given(db.collectionGroup(same(collectionGroup))).willReturn(query);
        }

        @Test
        public void getCollectionGroup() {
            Query result = adapter.getCollectionGroup(collectionGroup);
            assertEquals(query, result, "Should return a query with all the documents belonging to the collections "
                + "matching the required id");
        }

        @Test
        public void getCollectionGroupDocumentsWhereFieldEqualToValue() {
            given(query.whereEqualTo(same(field), same(value))).willReturn(query);
            given(query.get()).willReturn(apiFuture);

            ApiFuture<QuerySnapshot> result = adapter
                .getCollectionGroupDocumentsWhereEqualTo(collectionGroup, field, value);
            assertEquals(apiFuture, result,
                "Should return all documents from the collections whose id is " + collectionGroup + " where " + field
                    + " is equals to " + value);
        }

        @Test
        public void getCollectionGroupDocumentsWhereFieldFromFieldPathEqualToValue() {
            given(query.whereEqualTo(same(fieldPath), same(value))).willReturn(query);
            given(query.get()).willReturn(apiFuture);

            ApiFuture<QuerySnapshot> result = adapter
                .getCollectionGroupDocumentsWhereEqualTo(collectionGroup, fieldPath, value);
            assertEquals(apiFuture, result,
                "Should return all documents from the collections whose id is " + collectionGroup + " where " + field
                    + " in " + fieldPath + " is equals to " + value);
        }

        @Test
        public void getCollectionGroupDocumentsWhereArrayContainsValue() {
            given(query.whereArrayContains(same(array), same(value))).willReturn(query);
            given(query.get()).willReturn(apiFuture);

            ApiFuture<QuerySnapshot> result = adapter
                .getCollectionGroupDocumentsWhereArrayContains(collectionGroup, array, value);
            assertEquals(apiFuture, result,
                "Should return all documents from the collections whose id is " + collectionGroup + " and the "
                    + "array" + array + " contains " + value);
        }

        @Test
        public void getCollectionGroupDocumentsWhereArrayFromFieldPathContainsValue() {
            given(query.whereArrayContains(same(arrayPath), same(value))).willReturn(query);
            given(query.get()).willReturn(apiFuture);

            ApiFuture<QuerySnapshot> result = adapter
                .getCollectionGroupDocumentsWhereArrayContains(collectionGroup, arrayPath, value);
            assertEquals(apiFuture, result,
                "Should return all documents from the collections whose id is " + collectionGroup + " and have the "
                    + "array" + array + " in " + arrayPath + " which contains " + value);
        }

    }
}
