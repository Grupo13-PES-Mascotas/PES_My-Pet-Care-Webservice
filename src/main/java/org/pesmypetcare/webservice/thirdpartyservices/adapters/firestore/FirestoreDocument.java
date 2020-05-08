package org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.Map;

/**
 * @author Santiago Del Rey
 */
public interface FirestoreDocument {
    /**
     * Gets a Firestore WriteBatch instance that can be used to combine multiple writes.
     *
     * @return A WriteBatch that operates on this Firestore client
     */
    WriteBatch batch();

    /**
     * Commits a write batch
     * @param batch The batch to commit
     * @throws DatabaseAccessException When the commit fails
     */
    void commitBatch(@NonNull WriteBatch batch) throws DatabaseAccessException;

    /**
     * Gets a DocumentReference that refers to the document at the specified path.
     *
     * @param path A slash-separated path to a document
     * @return The DocumentReference instance
     */
    @NonNull
    DocumentReference getDocumentReference(@NonNull String path);

    /**
     * The id of a document refers to the last component of the path pointing to a document.
     *
     * @param path A slash-separated path to a document
     * @return The ID of the document
     */
    @NonNull
    String getDocumentId(@NonNull String path);

    /**
     * A reference to the Collection to which this Document belongs to.
     *
     * @param path A slash-separated path to a document
     * @return The CollectionReference instance
     */
    @NonNull
    CollectionReference getDocumentParent(@NonNull String path);

    /**
     * A snapshot to the Document referenced by this path.
     *
     * @param path A slash-separated path to a document
     * @return The DocumentSnapshot instance
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    @NonNull
    DocumentSnapshot getDocumentSnapshot(@NonNull String path) throws DatabaseAccessException, DocumentException;

    /**
     * Returns whether or not exists the document. Returns false if the document does not exist.
     *
     * @param snapshot The DocumentSnapshot instance
     * @return Whether the document existed in this snapshot
     */
    boolean documentSnapshotExists(@NonNull DocumentSnapshot snapshot);

    /**
     * Returns whether or not exists the document. Returns false if the document does not exist.
     *
     * @param path The path to the Document
     * @return Whether the document existed
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     */
    boolean documentExists(@NonNull String path) throws DatabaseAccessException;

    /**
     * Creates a new Document at the paths's location with an auto-generated id.
     * It fails the write if the document exists.
     *
     * @param path A slash-separated path to a document
     * @param fields A map of the fields and values for the document
     * @param batch The batch where to write
     * @return The document reference of the created document
     */
    DocumentReference createDocument(@NonNull String path, @NonNull Map<String, Object> fields,
                                     @NonNull WriteBatch batch);

    /**
     * Creates a new Document at the paths's location with an auto-generated id.
     * It fails the write if the document exists.
     *
     * @param path A slash-separated path to a document
     * @param fields A map of the fields and values for the document
     * @return The document reference of the created document
     */
    DocumentReference createDocument(@NonNull String path, @NonNull Map<String, Object> fields);

    /**
     * Creates a new Document at the paths's location with an auto-generated id.
     * It fails the write if the document exists.
     *
     * @param path A slash-separated path to a document
     * @param pojo A map of the fields and values for the document
     * @param batch The batch where to write
     * @return The document reference of the created document
     */
    DocumentReference createDocument(@NonNull String path, @NonNull Object pojo, @NonNull WriteBatch batch);

    /**
     * Creates a new Document at the paths's location with an auto-generated id.
     * It fails the write if the document exists.
     *
     * @param path A slash-separated path to a document
     * @param pojo A map of the fields and values for the document
     * @return The document reference of the created document
     */
    DocumentReference createDocument(@NonNull String path, @NonNull Object pojo);

    /**
     * Creates a new Document at the paths's location. It fails the write if the document exists.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param id The ID the document will have
     * @param fields A map of the fields and values for the document
     * @param batch The batch where to write
     * @return The document reference of the created document
     */
    DocumentReference createDocumentWithId(@NonNull String collectionPath, @NonNull String id, @NonNull Map<String,
        Object> fields, @NonNull WriteBatch batch);

    /**
     * Creates a new Document at the paths's location. It fails the write if the document exists.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param id The ID the document will have
     * @param fields A map of the fields and values for the document
     * @return The document reference of the created document
     */
    DocumentReference createDocumentWithId(@NonNull String collectionPath, @NonNull String id, @NonNull Map<String,
        Object> fields);

    /**
     * Creates a new Document at the paths's location. It fails the write if the document exists.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param id The ID the document will have
     * @param pojo A map of the fields and values for the document
     * @param batch The batch where to write
     * @return The document reference of the created document
     */
    DocumentReference createDocumentWithId(@NonNull String collectionPath, @NonNull String id, @NonNull Object pojo,
                                           @NonNull WriteBatch batch);

    /**
     * Creates a new Document at the paths's location. It fails the write if the document exists.
     *
     * @param collectionPath A slash-separated path to a collection
     * @param id The ID the document will have
     * @param pojo A map of the fields and values for the document
     * @return The document reference of the created document
     */
    DocumentReference createDocumentWithId(@NonNull String collectionPath, @NonNull String id, @NonNull Object pojo);

    /**
     * Overwrites the document referred to by this path.
     * If the document doesn't exist yet, it will be created. If a document already exists, it will be overwritten.
     *
     * @param path A slash-separated path to a document
     * @param fields A map of the fields and values for the document
     * @param batch The batch where to write
     */
    void setDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields, @NonNull WriteBatch batch);

    /**
     * Overwrites the document referred to by this path.
     * If the document doesn't exist yet, it will be created. If a document already exists, it will be overwritten.
     *
     * @param path A slash-separated path to a document
     * @param fields A map of the fields and values for the document
     */
    void setDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields);

    /**
     * Overwrites the document referred to by this path.
     * If the document doesn't exist yet, it will be created. If a document already exists, it will be overwritten.
     *
     * @param path A slash-separated path to a document
     * @param pojo A map of the fields and values for the document
     * @param batch The batch where to write
     */
    void setDocumentFields(@NonNull String path, @NonNull Object pojo, @NonNull WriteBatch batch);

    /**
     * Overwrites the document referred to by this path.
     * If the document doesn't exist yet, it will be created. If a document already exists, it will be overwritten.
     *
     * @param path A slash-separated path to a document
     * @param pojo A map of the fields and values for the document
     */
    void setDocumentFields(@NonNull String path, @NonNull Object pojo);

    /**
     * Updates fields in the document referred to by this path.
     * If the document doesn't exist yet, the update will fail.
     *
     * @param path A slash-separated path to a document
     * @param fields A map of the fields and values for the document
     * @param batch The batch where to write
     */
    void updateDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields, @NonNull WriteBatch batch);

    /**
     * Updates fields in the document referred to by this path.
     * If the document doesn't exist yet, the update will fail.
     *
     * @param path A slash-separated path to a document
     * @param fields A map of the fields and values for the document
     */
    void updateDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields);

    /**
     * Updates the fields in the document referred to by this path. If the document
     * doesn't exist yet, the update will fail.
     *
     * @param batch The batch where to write
     * @param path A slash-separated path to a document
     * @param fieldPath The first field to set.
     * @param value The first value to set.
     * @param moreFieldsAndValues String and Object pairs with more fields to be set.
     */
    void updateDocumentFields(@NonNull WriteBatch batch, @NonNull String path, @NonNull FieldPath fieldPath,
                              @Nullable Object value, Object... moreFieldsAndValues);

    /**
     * Updates the fields in the document referred to by this path. If the document
     * doesn't exist yet, the update will fail.
     *
     * @param path A slash-separated path to a document
     * @param fieldPath The first field to set.
     * @param value The first value to set.
     * @param moreFieldsAndValues String and Object pairs with more fields to be set.
     */
    void updateDocumentFields(@NonNull String path, @NonNull FieldPath fieldPath, @Nullable Object value,
                              Object... moreFieldsAndValues);

    /**
     * Updates the fields in the document referred to by this path. If the document
     * doesn't exist yet, the update will fail.
     *
     * @param batch The batch where to write
     * @param path A slash-separated path to a document
     * @param field The first field to set.
     * @param value The first value to set.
     * @param moreFieldsAndValues String and Object pairs with more fields to be set.
     */
    void updateDocumentFields(@NonNull WriteBatch batch, @NonNull String path, @NonNull String field,
                              @Nullable Object value, Object... moreFieldsAndValues);

    /**
     * Updates the fields in the document referred to by this path. If the document
     * doesn't exist yet, the update will fail.
     *
     * @param path A slash-separated path to a document
     * @param field The first field to set.
     * @param value The first value to set.
     * @param moreFieldsAndValues String and Object pairs with more fields to be set.
     */
    void updateDocumentFields(@NonNull String path, @NonNull String field, @Nullable Object value,
                              Object... moreFieldsAndValues);

    /**
     * Deletes the document referred to by this path. If the document has inner collections they will be also deleted.
     *
     * @param path A slash-separated path to a document
     * @param batch The batch where to write
     */
    void deleteDocument(@NonNull String path, @NonNull WriteBatch batch);

    /**
     * Deletes the document referred to by this path. If the document has inner collections they will be also deleted.
     *
     * @param path A slash-separated path to a document
     */
    void deleteDocument(@NonNull String path);

    /**
     * Returns the value at the field or null if the field doesn't exist.
     *
     * @param path A slash-separated path to a document
     * @param field The path to the field
     * @return The value at the given field or null
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    Object getDocumentField(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException;

    /**
     * Returns the value at the field or null if the field doesn't exist.
     *
     * @param path A slash-separated path to a document
     * @param fieldPath The path to the field
     * @return The value at the given field or null
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    @Nullable
    Object getDocumentField(@NonNull String path, @NonNull FieldPath fieldPath)
        throws DatabaseAccessException, DocumentException;

    /**
     * Returns the value of the field as a double.
     *
     * @param path A slash-separated path to a document
     * @param field The path to the field
     * @return The value of the field
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     * @throws RuntimeException If the value is not a Number
     */
    @Nullable
    Double getDoubleFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException;

    /**
     * Returns the value of the field as a Date.
     *
     * @param path A slash-separated path to a document
     * @param field The path to the field
     * @return The value of the field
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     * @throws RuntimeException If the value is not a Date
     */
    @Nullable
    Date getDateFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException;

    /**
     * Returns the value of the field as a boolean.
     *
     * @param path A slash-separated path to a document
     * @param field The path to the field
     * @return The value of the field
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     * @throws RuntimeException If the value is not a Boolean
     */
    @Nullable
    Boolean getBooleanFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException;

    /**
     * Returns the value of the field as a GeoPoint.
     *
     * @param path A slash-separated path to a document
     * @param field The path to the field
     * @return The value of the field
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     * @throws RuntimeException If the value is not a GeoPoint
     */
    @Nullable
    GeoPoint getGeoPointFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException;

    /**
     * Returns the value of the field as a String.
     *
     * @param path A slash-separated path to a document
     * @param field The path to the field
     * @return The value of the field
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     * @throws RuntimeException If the value is not a String
     */
    @Nullable
    String getStringFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException;

    /**
     * Returns the value of the field as a Timestamp.
     *
     * @param path A slash-separated path to a document
     * @param field The path to the field
     * @return The value of the field
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     * @throws RuntimeException If the value is not a Date
     */
    @Nullable
    Timestamp getTimestampFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException;

    /**
     * Returns the fields of the document as a Map.
     * Field values will be converted to their native Java representation.
     *
     * @param path A slash-separated path to a document
     * @return The fields of the document as a Map
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    @Nullable
    Map<String, Object> getDocumentData(@NonNull String path) throws DatabaseAccessException, DocumentException;

    /**
     * Returns the contents of the document converted to a POJO.
     *
     * @param path A slash-separated path to a document
     * @param valueType The Java class to create
     * @return The contents of the document in an object of type T
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    @Nullable
    <T> T getDocumentDataAsObject(@NonNull String path, @NonNull Class<T> valueType)
        throws DatabaseAccessException, DocumentException;

    /**
     * Returns whether or not the field exists in the document.
     *
     * @param path A slash-separated path to a document
     * @param field The path to the field
     * @return True if the field exists
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    boolean documentContains(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException;

    /**
     * Returns whether or not the field exists in the document.
     *
     * @param path A slash-separated path to a document
     * @param fieldPath The path to the field
     * @return True if the field exists
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    boolean documentContains(@NonNull String path, @NonNull FieldPath fieldPath)
        throws DatabaseAccessException, DocumentException;
}
