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
    private static final String COLLECTION_ID = "as2asdh34hg";
    private static final String COLLECTION_PATH = "users";
    private static final String COLLECTION_GROUP = "forums";
    private static final int VALUE = 2;
    private static final String FIELD = "number";
    private static final String VALUE_2 = "some text";
    private static final String FIELD_2 = "text";
    private static final String ARRAY = "numbers";
    private static final String ARRAY_2 = "texts";
    private static final FieldPath FIELD_PATH = FieldPath.of(FIELD);
    private static final FieldPath FIELD_PATH_2 = FieldPath.of(FIELD_2);
    private static final FieldPath ARRAY_PATH = FieldPath.of(ARRAY);
    private static final FieldPath ARRAY_PATH_2 = FieldPath.of(ARRAY_2);

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
            () -> adapter.getDocumentsWhereEqualTo(COLLECTION_GROUP, ARRAY_PATH, VALUE, ARRAY_PATH));
    }

    @Nested
    class UseDbCollection {


        @BeforeEach
        public void setUp() {
            given(db.collection(same(COLLECTION_PATH))).willReturn(collectionReference);
        }

        @Test
        public void getCollectionReference() {
            CollectionReference reference = adapter.getCollectionReference(COLLECTION_PATH);
            assertEquals(collectionReference, reference, "Should return the collection reference");
        }

        @Test
        public void getCollectionId() {
            given(collectionReference.getId()).willReturn(COLLECTION_ID);
            String id = adapter.getCollectionId(COLLECTION_PATH);
            assertEquals(COLLECTION_ID, id, "Should return the collection id");
        }

        @Test
        public void getCollectionParent() {
            given(collectionReference.getParent()).willReturn(documentReference);
            DocumentReference reference = adapter.getCollectionParent(COLLECTION_PATH);
            assertEquals(documentReference, reference, "Should return the parent document reference of the collection");
        }

        @Test
        public void listAllCollectionDocuments() {
            given(collectionReference.listDocuments()).willReturn(documentReferences);
            Iterable<DocumentReference> documents = adapter.listAllCollectionDocuments(COLLECTION_PATH);
            assertEquals(documentReferences, documents, "Should return an iterable with all the collection documents");
        }

        @Test
        public void listAllCollectionDocumentSnapshots() throws DatabaseAccessException, DocumentException {
            given(collectionReference.listDocuments()).willReturn(documentReferences);
            Iterator documentIterator = mock(Iterator.class);
            given(documentReferences.iterator()).willReturn(documentIterator);
            given(documentIterator.hasNext()).willReturn(true, false);
            given(documentIterator.next()).willReturn(documentReference);
            given(documentReference.getPath()).willReturn(COLLECTION_PATH);
            DocumentSnapshot snapshot = mock(DocumentSnapshot.class);
            given(documentAdapter.getDocumentSnapshot(anyString())).willReturn(snapshot);

            List<DocumentSnapshot> snapshots = adapter.listAllCollectionDocumentSnapshots(COLLECTION_PATH);
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
            given(collectionReference.getPath()).willReturn(COLLECTION_PATH);
            WriteBatch mockBatch = mock(WriteBatch.class);
            given(mockBatch.delete(any(DocumentReference.class))).willReturn(mockBatch);

            adapter.deleteCollection(COLLECTION_PATH, mockBatch);
            verify(mockBatch).delete(documentReference);
        }

        @Test
        public void getDocumentsWhereFieldEqualToValues() {
            given(collectionReference.whereEqualTo(same(FIELD), same(VALUE))).willReturn(query);
            given(query.get()).willReturn(apiFuture);
            ApiFuture<QuerySnapshot> result = adapter
                .getDocumentsWhereEqualTo(COLLECTION_PATH, FIELD, VALUE, FIELD_2, VALUE_2);
            assertEquals(apiFuture, result,
                "Should return all documents from " + COLLECTION_PATH + " where " + FIELD + " is equals to " + VALUE
                    + " and " + FIELD_2 + " is equals to " + VALUE_2);
        }

        @Test
        public void getDocumentsWhereFieldEqualToValuesShouldFailIfAnyExtraFieldIsEmpty() {
            given(collectionReference.whereEqualTo(same(FIELD), same(VALUE))).willReturn(query);
            assertThrows(IllegalArgumentException.class,
                () -> adapter.getDocumentsWhereEqualTo(COLLECTION_PATH, FIELD, VALUE, "", VALUE_2),
                "Should fail if any of the fields in the varargs is empty");
        }

        @Test
        public void getDocumentsWhereFieldEqualToValuesShouldFailIfAnyExtraFieldIsNull() {
            given(collectionReference.whereEqualTo(same(FIELD), same(VALUE))).willReturn(query);
            assertThrows(IllegalArgumentException.class,
                () -> adapter.getDocumentsWhereEqualTo(COLLECTION_PATH, FIELD, VALUE, null, VALUE_2),
                "Should fail if any of the fields in the varargs is null");
        }

        @Test
        public void getDocumentsWhereFieldFromFieldPathsEqualToValue() {
            given(collectionReference.whereEqualTo(same(FIELD_PATH), same(VALUE))).willReturn(query);
            given(query.get()).willReturn(apiFuture);
            ApiFuture<QuerySnapshot> result = adapter
                .getDocumentsWhereEqualTo(COLLECTION_PATH, FIELD_PATH, VALUE, FIELD_PATH_2, VALUE_2);
            assertEquals(apiFuture, result,
                "Should return all documents from " + COLLECTION_PATH + " where " + FIELD + " in " + FIELD_PATH
                    + " is equals to " + VALUE + " and " + FIELD_2 + " in " + FIELD_PATH_2 + " is equals to "
                    + VALUE_2);
        }

        @Test
        public void getDocumentsWhereFieldEqualToValuesFromFieldPathsShouldFailIfAnyExtraFieldPathIsNull() {
            given(collectionReference.whereEqualTo(same(FIELD_PATH), same(VALUE))).willReturn(query);
            assertThrows(IllegalArgumentException.class,
                () -> adapter.getDocumentsWhereEqualTo(COLLECTION_PATH, FIELD_PATH, VALUE, null, VALUE_2),
                "Should fail if any of the field paths in the varargs is null");
        }

        @Test
        public void getDocumentsWhereArraysContainValue() {
            given(collectionReference.whereArrayContains(same(ARRAY), same(VALUE))).willReturn(query);
            given(query.get()).willReturn(apiFuture);

            ApiFuture<QuerySnapshot> result = adapter
                .getDocumentsWhereArrayContains(COLLECTION_PATH, ARRAY, VALUE, ARRAY_2, VALUE_2);
            assertEquals(apiFuture, result,
                "Should return all documents from " + COLLECTION_PATH + " where the array " + ARRAY + " contains "
                    + VALUE + " and the array " + ARRAY_2 + " contains " + VALUE_2);
        }

        @Test
        public void getDocumentsWhereArraysContainValueShouldFailIfAnyExtraFieldIsEmpty() {
            given(collectionReference.whereArrayContains(same(ARRAY), same(VALUE))).willReturn(query);
            assertThrows(IllegalArgumentException.class,
                () -> adapter.getDocumentsWhereArrayContains(COLLECTION_PATH, ARRAY, VALUE, "", VALUE_2),
                "Should fail if any of the fields in the varargs is empty");
        }

        @Test
        public void getDocumentsWhereArraysContainValueShouldFailIfAnyExtraFieldIsNull() {
            given(collectionReference.whereArrayContains(same(ARRAY), same(VALUE))).willReturn(query);
            assertThrows(IllegalArgumentException.class,
                () -> adapter.getDocumentsWhereArrayContains(COLLECTION_PATH, ARRAY, VALUE, null, VALUE_2),
                "Should fail if any of the fields in the varargs is null");
        }

        @Test
        public void getDocumentsWhereArraysFromFieldPathsContainsValue() {
            given(collectionReference.whereArrayContains(same(ARRAY_PATH), same(VALUE))).willReturn(query);
            given(query.get()).willReturn(apiFuture);
            ApiFuture<QuerySnapshot> result = adapter
                .getDocumentsWhereArrayContains(COLLECTION_PATH, ARRAY_PATH, VALUE, ARRAY_PATH_2, VALUE_2);
            assertEquals(apiFuture, result,
                "Should return all documents from " + COLLECTION_PATH + " where the array" + ARRAY + " in " + ARRAY_PATH
                    + " contains " + VALUE + " and the array " + ARRAY_2 + " in " + ARRAY_PATH_2 + " contains "
                    + VALUE_2);
        }

        @Test
        public void getDocumentsWhereArraysFromFieldPathsShouldFailIfAnyExtraFieldPathIsNull() {
            given(collectionReference.whereArrayContains(same(ARRAY_PATH), same(VALUE))).willReturn(query);
            assertThrows(IllegalArgumentException.class,
                () -> adapter.getDocumentsWhereArrayContains(COLLECTION_PATH, ARRAY_PATH, VALUE, null, VALUE_2),
                "Should fail if any of the field paths in the varargs is null");
        }

    }

    @Nested
    class UsesCollectionGroup {

        @BeforeEach
        public void setUp() {
            given(db.collectionGroup(same(COLLECTION_GROUP))).willReturn(query);
        }

        @Test
        public void getCollectionGroup() {
            Query result = adapter.getCollectionGroup(COLLECTION_GROUP);
            assertEquals(query, result, "Should return a query with all the documents belonging to the collections "
                + "matching the required id");
        }

        @Test
        public void getCollectionGroupDocumentsWhereFieldEqualToValue() {
            given(query.whereEqualTo(same(FIELD), same(VALUE))).willReturn(query);
            given(query.get()).willReturn(apiFuture);

            ApiFuture<QuerySnapshot> result = adapter
                .getCollectionGroupDocumentsWhereEqualTo(COLLECTION_GROUP, FIELD, VALUE);
            assertEquals(apiFuture, result,
                "Should return all documents from the collections whose id is " + COLLECTION_GROUP + " where " + FIELD
                    + " is equals to " + VALUE);
        }

        @Test
        public void getCollectionGroupDocumentsWhereFieldFromFieldPathEqualToValue() {
            given(query.whereEqualTo(same(FIELD_PATH), same(VALUE))).willReturn(query);
            given(query.get()).willReturn(apiFuture);

            ApiFuture<QuerySnapshot> result = adapter
                .getCollectionGroupDocumentsWhereEqualTo(COLLECTION_GROUP, FIELD_PATH, VALUE);
            assertEquals(apiFuture, result,
                "Should return all documents from the collections whose id is " + COLLECTION_GROUP + " where " + FIELD
                    + " in " + FIELD_PATH + " is equals to " + VALUE);
        }

        @Test
        public void getCollectionGroupDocumentsWhereArrayContainsValue() {
            given(query.whereArrayContains(same(ARRAY), same(VALUE))).willReturn(query);
            given(query.get()).willReturn(apiFuture);

            ApiFuture<QuerySnapshot> result = adapter
                .getCollectionGroupDocumentsWhereArrayContains(COLLECTION_GROUP, ARRAY, VALUE);
            assertEquals(apiFuture, result,
                "Should return all documents from the collections whose id is " + COLLECTION_GROUP + " and the "
                    + "array" + ARRAY + " contains " + VALUE);
        }

        @Test
        public void getCollectionGroupDocumentsWhereArrayFromFieldPathContainsValue() {
            given(query.whereArrayContains(same(ARRAY_PATH), same(VALUE))).willReturn(query);
            given(query.get()).willReturn(apiFuture);

            ApiFuture<QuerySnapshot> result = adapter
                .getCollectionGroupDocumentsWhereArrayContains(COLLECTION_GROUP, ARRAY_PATH, VALUE);
            assertEquals(apiFuture, result,
                "Should return all documents from the collections whose id is " + COLLECTION_GROUP + " and have the "
                    + "array" + ARRAY + " in " + ARRAY_PATH + " which contains " + VALUE);
        }

    }
}
