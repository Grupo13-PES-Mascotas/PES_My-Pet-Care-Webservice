package org.pesmypetcare.webservice.firebaseservice.adapters.firestore;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.WriteBatch;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @author Santiago Del Rey
 */
public interface FirestoreCollection {
    /**
     * Gets a Firestore WriteBatch instance that can be used to combine multiple writes.
     * @return A WriteBatch that operates on this Firestore client
     */
    WriteBatch batch();
    /**
     * Gets a CollectionReference that refers to the collection at the specified path.
     *
     * @param path A slash-separated path to a collection
     * @return The CollectionReference instance
     */
    @NonNull
    CollectionReference getCollectionReference(@NonNull String path);

    /**
     * The id of a collection refers to the last component of the path pointing to a collection.
     *
     * @param path A slash-separated path to a collection
     * @return The ID of the collection
     */
    @NonNull
    String getCollectionId(@NonNull String path);

    /**
     * A reference to the Document to which this Collection belongs to.
     *
     * @param path A slash-separated path to a collection
     * @return The DocumentReference instance
     */
    @Nullable
    DocumentReference getCollectionParent(@NonNull String path);

    /**
     * Retrieves the list of documents in this collection.
     * The document references returned may include references to "missing documents", i.e. document locations that have
     * no document present but which contain subcollections with documents. Attempting to read such a document reference
     * (e.g. via `get()` or `onSnapshot()`) will return a `DocumentSnapshot` whose `exists()` method returns false.
     *
     * @param path A slash-separated path to a collection
     * @return The list of documents in this collection
     */
    @NonNull
    Iterable<DocumentReference> listAllCollectionDocuments(@NonNull String path);

    /**
     * Creates and returns a new Query that includes all documents in the database that are contained in a
     * collection or subcollection with the given {@code collectionId}.
     * @param collectionId Identifies the collections to query over. Every collection or subcollection with this ID as
     *                    the last segment of its path will be included. Cannot contain a slash.
     * @return The created Query
     */
    Query getCollectionGroup(@NonNull String collectionId);

    /**
     * Deletes the Collection referred to by this path.
     *
     * @param path A slash-separated path to a collection
     * @param batch The batch where to write
     */
    void deleteCollection(@NonNull String path, @NonNull WriteBatch batch);

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field
     * and the value should be equal to the specified value.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param field The name of the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    @NonNull
    Query getDocumentsWhereEqualTo(@NonNull String collectionPath, @NonNull String field,
                                   @Nullable Object value);

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field
     * and the value should be equal to the specified value.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param fieldPath The name of the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    @NonNull
    Query getDocumentsWhereEqualTo(@NonNull String collectionPath, @NonNull FieldPath fieldPath,
                                   @Nullable Object value);

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field,
     * the value must be an array, and that the array must contain the provided value.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param field The name of the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    @NonNull
    Query getDocumentsWhereArrayContains(@NonNull String collectionPath, @NonNull String field,
                                         @NonNull Object value);

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field,
     * the value must be an array, and that the array must contain the provided value.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param fieldPath The name ofDocument the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    @NonNull
    Query getDocumentsWhereArrayContains(@NonNull String collectionPath, @NonNull FieldPath fieldPath,
                                         @NonNull Object value);

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field
     * and the value should be equal to the specified value.
     *
     * @param collectionId Identifies the collections to query over. Every collection or subcollection with this ID as
     *                     the last segment ofDocument its path will be included. Cannot contain a slash.
     * @param field The name ofDocument the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    @NonNull
    Query getCollectionGroupDocumentsWhereEqualTo(@NonNull String collectionId, @NonNull String field,
                                                  @Nullable Object value);

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field
     * and the value should be equal to the specified value.
     *
     * @param collectionId Identifies the collections to query over. Every collection or subcollection with this ID as
     *                     the last segment ofDocument its path will be included. Cannot contain a slash.
     * @param fieldPath The name ofDocument the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    @NonNull
    Query getCollectionGroupDocumentsWhereEqualTo(@NonNull String collectionId, @NonNull FieldPath fieldPath,
                                                  @Nullable Object value);

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field,
     * the value must be an array, and that the array must contain the provided value.
     *
     * @param collectionId Identifies the collections to query over. Every collection or subcollection with this ID as
     *                     the last segment ofDocument its path will be included. Cannot contain a slash.
     * @param field The name ofDocument the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    @NonNull
    Query getCollectionGroupDocumentsWhereArrayContains(@NonNull String collectionId, @NonNull String field,
                                                        @NonNull Object value);

    /**
     * Creates and returns a new Query with the additional filter that documents must contain the specified field,
     * the value must be an array, and that the array must contain the provided value.
     *
     * @param collectionId Identifies the collections to query over. Every collection or subcollection with this ID as
     *                     the last segment ofDocument its path will be included. Cannot contain a slash.
     * @param fieldPath The name ofDocument the field to compare
     * @param value The value for comparison
     * @return The created Query
     */
    @NonNull
    Query getCollectionGroupDocumentsWhereArrayContains(@NonNull String collectionId,
                                                        @NonNull FieldPath fieldPath, @NonNull Object value);
}
