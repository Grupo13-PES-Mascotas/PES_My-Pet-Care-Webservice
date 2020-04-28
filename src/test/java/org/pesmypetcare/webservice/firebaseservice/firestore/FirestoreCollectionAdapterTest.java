package org.pesmypetcare.webservice.firebaseservice.firestore;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.WriteBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
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
    private String array = "numbers";
    private FieldPath fieldPath = FieldPath.of(field);
    private FieldPath arrayPath = FieldPath.of(array);

    @Mock
    private Firestore db;
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

    @InjectMocks
    private FirestoreCollection collectionAdapter = new FirestoreCollectionAdapter();

    @Nested
    class UseDbCollection {

        @BeforeEach
        public void setUp() {
            given(db.collection(same(collectionPath))).willReturn(collectionReference);
        }

        @Test
        public void getCollectionReference() {
            CollectionReference reference = collectionAdapter.getCollectionReference("users");
            assertEquals(collectionReference, reference, "Should return the collection reference");
        }

        @Test
        public void getCollectionId() {
            given(collectionReference.getId()).willReturn(collectionId);
            String id = collectionAdapter.getCollectionId("users");
            assertEquals(collectionId, id, "Should return the collection id");
        }

        @Test
        public void getCollectionParent() {
            given(collectionReference.getParent()).willReturn(documentReference);
            DocumentReference reference = collectionAdapter.getCollectionParent(collectionPath);
            assertEquals(documentReference, reference, "Should return the parent document reference of the collection");
        }

        @Test
        public void listAllCollectionDocuments() {
            given(collectionReference.listDocuments()).willReturn(documentReferences);
            Iterable<DocumentReference> documents = collectionAdapter.listAllCollectionDocuments(collectionPath);
            assertEquals(documentReferences, documents, "Should return an iterable with all the collection documents");
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

            collectionAdapter.deleteCollection(collectionPath, mockBatch);
            verify(mockBatch).delete(documentReference);
        }

        @Test
        public void getDocumentsWhereFieldEqualToValue() {
            given(collectionReference.whereEqualTo(same(field), same(value))).willReturn(query);
            Query result = collectionAdapter.getDocumentsWhereEqualTo(collectionPath, field, value);
            assertEquals(query, result, "Should return all documents from " + collectionPath + " where " + field + " "
                + "is equals to " + value);
        }

        @Test
        public void getDocumentsWhereFieldFromFieldPathEqualToValue() {
            given(collectionReference.whereEqualTo(same(fieldPath), same(value))).willReturn(query);
            Query result = collectionAdapter.getDocumentsWhereEqualTo(collectionPath, fieldPath, value);
            assertEquals(query, result, "Should return all documents from " + collectionPath + " where " + field + " "
                + "in " + fieldPath + " is equals to " + value);
        }

        @Test
        public void getDocumentsWhereArrayContainsValue() {
            given(collectionReference.whereArrayContains(same(array), same(value))).willReturn(query);
            Query result = collectionAdapter.getDocumentsWhereArrayContains(collectionPath, array, value);
            assertEquals(query, result,
                "Should return all documents from " + collectionPath + " where the array " + array + " contains " + value);
        }

        @Test
        public void getDocumentsWhereArrayFromFieldPathContainsValue() {
            given(collectionReference.whereArrayContains(same(arrayPath), same(value))).willReturn(query);
            Query result = collectionAdapter.getDocumentsWhereArrayContains(collectionPath, arrayPath, value);
            assertEquals(query, result,
                "Should return all documents from " + collectionPath + " where the array" + array + " in " + arrayPath + " contains " + value);
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
            Query result = collectionAdapter.getCollectionGroup(collectionGroup);
            assertEquals(query, result, "Should return a query with all the documents belonging to the collections " +
                "matching the required id");
        }

        @Test
        public void getCollectionGroupDocumentsWhereFieldEqualToValue() {
            given(query.whereEqualTo(same(field), same(value))).willReturn(query);
            Query result = collectionAdapter.getCollectionGroupDocumentsWhereEqualTo(collectionGroup, field, value);
            assertEquals(query, result,
                "Should return all documents from the collections whose id is " + collectionGroup + " where " + field +
                    " is equals to " + value);
        }

        @Test
        public void getCollectionGroupDocumentsWhereFieldFromFieldPathEqualToValue() {
            given(query.whereEqualTo(same(fieldPath), same(value))).willReturn(query);
            Query result = collectionAdapter.getCollectionGroupDocumentsWhereEqualTo(collectionGroup, fieldPath, value);
            assertEquals(query, result,
                "Should return all documents from the collections whose id is " + collectionGroup + " where " + field +
                    " in " + fieldPath + " is equals to " + value);
        }

        @Test
        public void getCollectionGroupDocumentsWhereArrayContainsValue() {
            given(query.whereArrayContains(same(array), same(value))).willReturn(query);
            Query result = collectionAdapter.getCollectionGroupDocumentsWhereArrayContains(collectionGroup, array, value);
            assertEquals(query, result,
                "Should return all documents from the collections whose id is " + collectionGroup + " and the " + "array" + array + " contains " + value);
        }

        @Test
        public void getCollectionGroupDocumentsWhereArrayFromFieldPathContainsValue() {
            given(query.whereArrayContains(same(arrayPath), same(value))).willReturn(query);
            Query result = collectionAdapter.getCollectionGroupDocumentsWhereArrayContains(collectionGroup, arrayPath,
                value);
            assertEquals(query, result,
                "Should return all documents from the collections whose id is " + collectionGroup + " and have the " +
                    "array" + array + " in " + arrayPath + " which contains " + value);
        }
    }
}
