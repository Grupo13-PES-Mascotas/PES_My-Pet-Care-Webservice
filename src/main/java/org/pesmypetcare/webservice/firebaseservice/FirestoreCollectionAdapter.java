package org.pesmypetcare.webservice.firebaseservice;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.WriteBatch;
import org.springframework.lang.NonNull;

/**
 * @author Santiago Del Rey
 */
public class FirestoreCollectionAdapter {
    private Firestore db;

    public FirestoreCollectionAdapter() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    /**
     * Gets a CollectionReference that refers to the collection at the specified path.
     *
     * @param path A slash-separated path to a collection
     * @return The CollectionReference instance
     */
    public CollectionReference getCollectionReference(@NonNull String path) {
        return db.collection(path);
    }

    /**
     * The id of a collection refers to the last component of path pointing to a collection.
     *
     * @param path A slash-separated path to a collection
     * @return The ID of the collection
     */
    public String getCollectionId(@NonNull String path) {
        return db.collection(path).getId();
    }

    /**
     * A reference to the Document to which this Collection belongs to.
     *
     * @param path A slash-separated path to a collection
     * @return The DocumentReference instance
     */
    public DocumentReference getCollectionParent(@NonNull String path) {
        return db.collection(path).getParent();
    }

    /**
     * Retrieves the list of documents in this collection.
     * The document references returned may include references to "missing documents", i.e. document locations that have
     * no document present but which contain subcollections with documents. Attempting to read such a document reference
     * (e.g. via `get()` or `onSnapshot()`) will return a `DocumentSnapshot` whose `exists()` method returns false.
     *
     * @param path A slash-separated path to a collection
     * @return The list of documents in this collection
     */
    public Iterable<DocumentReference> listAllCollectionDocuments(@NonNull String path) {
        return db.collection(path).listDocuments();
    }

    /**
     * Creates and returns a new Query that includes all documents in the database that are contained in a
     * collection or subcollection with the given {@code collectionId}.
     * @param collectionId Identifies the collections to query over. Every collection or subcollection with this ID as
     *                    the last segment of its path will be included. Cannot contain a slash.
     * @return The created Query
     */
    public Query getCollectionGroup(@NonNull String collectionId) {
        return db.collectionGroup(collectionId);
    }

    /**
     * Deletes the Collection referred to by this path.
     *
     * @param path A slash-separated path to a collection
     * @param batch The batch where to write
     */
    public void deleteCollection(@NonNull String path, @NonNull WriteBatch batch) {
        Iterable<DocumentReference> documents = getCollectionReference(path).listDocuments();
        for (DocumentReference doc : documents) {
            Iterable<CollectionReference> innerCollections = doc.listCollections();
            for (CollectionReference innerCollection : innerCollections) {
                deleteCollection(innerCollection.getPath(), batch);
            }
            batch.delete(doc);
        }
    }

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field
     * and the value should be equal to the specified value.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param field The name of the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    public Query getDocumentsWhereEqualTo(@NonNull String collectionPath, @NonNull String field,
                                          @NonNull Object value) {
        return db.collection(collectionPath).whereEqualTo(field, value);
    }

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field
     * and the value should be equal to the specified value.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param fieldPath The name of the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    public Query getDocumentsWhereEqualTo(@NonNull String collectionPath, @NonNull FieldPath fieldPath,
                                          @NonNull Object value) {
        return db.collection(collectionPath).whereEqualTo(fieldPath, value);
    }

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field,
     * the value must be an array, and that the array must contain the provided value.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param field The name of the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    public Query getDocumentsWhereArrayContains(@NonNull String collectionPath, @NonNull String field,
                                                @NonNull Object value) {
        return db.collection(collectionPath).whereArrayContains(field, value);
    }

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field,
     * the value must be an array, and that the array must contain the provided value.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param fieldPath The name of the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    public Query getDocumentsWhereArrayContains(@NonNull String collectionPath, @NonNull FieldPath fieldPath,
                                                @NonNull Object value) {
        return db.collection(collectionPath).whereArrayContains(fieldPath, value);
    }

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field
     * and the value should be equal to the specified value.
     *
     * @param collectionId Identifies the collections to query over. Every collection or subcollection with this ID as
     *                     the last segment of its path will be included. Cannot contain a slash.
     * @param field The name of the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    public Query getCollectionGroupDocumentsWhereEqualTo(@NonNull String collectionId, @NonNull String field,
                                                         @NonNull Object value) {
        return db.collectionGroup(collectionId).whereEqualTo(field, value);
    }

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field
     * and the value should be equal to the specified value.
     *
     * @param collectionId Identifies the collections to query over. Every collection or subcollection with this ID as
     *                     the last segment of its path will be included. Cannot contain a slash.
     * @param fieldPath The name of the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    public Query getCollectionGroupDocumentsWhereEqualTo(@NonNull String collectionId, @NonNull FieldPath fieldPath,
                                                         @NonNull Object value) {
        return db.collectionGroup(collectionId).whereEqualTo(fieldPath, value);
    }

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field,
     * the value must be an array, and that the array must contain the provided value.
     *
     * @param collectionId Identifies the collections to query over. Every collection or subcollection with this ID as
     *                     the last segment of its path will be included. Cannot contain a slash.
     * @param field The name of the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    public Query getCollectionGroupDocumentsWhereArrayContains(@NonNull String collectionId, @NonNull String field,
                                                               @NonNull Object value) {
        return db.collectionGroup(collectionId).whereArrayContains(field, value);
    }

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field,
     * the value must be an array, and that the array must contain the provided value.
     *
     * @param collectionId Identifies the collections to query over. Every collection or subcollection with this ID as
     *                     the last segment of its path will be included. Cannot contain a slash.
     * @param fieldPath The name of the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    public Query getCollectionGroupDocumentsWhereArrayContains(@NonNull String collectionId,
                                                               @NonNull FieldPath fieldPath, @NonNull Object value) {
        return db.collectionGroup(collectionId).whereArrayContains(fieldPath, value);
    }
}
