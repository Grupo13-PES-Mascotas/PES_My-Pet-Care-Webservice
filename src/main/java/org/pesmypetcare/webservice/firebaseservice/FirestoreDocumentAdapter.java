package org.pesmypetcare.webservice.firebaseservice;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Santiago Del Rey
 */
public class FirestoreDocumentAdapter {
    private Firestore db;

    public FirestoreDocumentAdapter() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    /**
     * Gets a DocumentReference that refers to the document at the specified path.
     *
     * @param path A slash-separated path to a document
     * @return The DocumentReference instance
     */
    public DocumentReference getDocumentReference(@NonNull String path) {
        return db.document(path);
    }

    /**
     * The id of a document refers to the last component of path pointing to a document.
     *
     * @param path A slash-separated path to a document
     * @return The ID of the document
     */
    public String getDocumentId(@NonNull String path) {
        return getDocumentReference(path).getId();
    }

    /**
     * A reference to the Collection to which this DocumentReference belongs to.
     *
     * @param path A slash-separated path to a document
     * @return The CollectionReference instance
     */
    public CollectionReference getDocumentParent(@NonNull String path) {
        return getDocumentReference(path).getParent();
    }

    /**
     * A snapshot to the Document to which this DocumentReference belongs to.
     *
     * @param path A slash-separated path to a document
     * @return The DocumentSnapshot instance
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    public DocumentSnapshot getDocumentSnapshot(@NonNull String path)
        throws DatabaseAccessException, DocumentException {
        ApiFuture<DocumentSnapshot> future = db.document(path).get();
        try {
            DocumentSnapshot snapshot = future.get();
            if (!documentSnapshotExists(snapshot)) {
                throw new DocumentException("invalid-path", "The document does not exist");
            }
            return snapshot;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("retrieval-failed", "The document could not be retrieves");
        }
    }

    /**
     * Returns whether or not the field exists in the document. Returns false if the document does not exist.
     *
     * @param snapshot The DocumentSnapshot instance
     * @return Whether the document existed in this snapshot
     */
    public boolean documentSnapshotExists(@NonNull DocumentSnapshot snapshot) {
        return snapshot.exists();
    }

    /**
     * Creates a new Document at the paths's location with an auto-generated id.
     * It fails the write if the document exists.
     *
     * @param path A slash-separated path to a document
     * @param fields A map of the fields and values for the document
     * @param batch The batch where to write
     */
    public void createDocument(@NonNull String path, @NonNull Map<String, Object> fields,@NonNull WriteBatch batch) {
        DocumentReference ref = db.collection(path).document();
        batch.create(ref, fields);
    }

    /**
     * Creates a new Document at the paths's location with an auto-generated id.
     * It fails the write if the document exists.
     *
     * @param path A slash-separated path to a document
     * @param pojo A map of the fields and values for the document
     * @param batch The batch where to write
     */
    public void createDocument(@NonNull String path, @NonNull Object pojo, @NonNull WriteBatch batch) {
        DocumentReference ref = db.collection(path).document();
        batch.create(ref, pojo);
    }

    /**
     * Creates a new Document at the paths's location. It fails the write if the document exists.
     *
     * @param id The ID the document will have
     * @param collectionPath A slash-separated path to a collection
     * @param fields A map of the fields and values for the document
     * @param batch The batch where to write
     */
    public void createDocumentWithId(@NonNull String id, @NonNull String collectionPath, @NonNull Map<String, Object> fields,@NonNull WriteBatch batch) {
        DocumentReference ref = getDocumentReference(collectionPath + "/" + id);
        batch.create(ref, fields);
    }

    /**
     * Creates a new Document at the paths's location. It fails the write if the document exists.
     *
     * @param id The ID the document will have
     * @param collectionPath A slash-separated path to a collection
     * @param pojo A map of the fields and values for the document
     * @param batch The batch where to write
     */
    public void createDocumentWithId(@NonNull String id, @NonNull String collectionPath, @NonNull Object pojo, @NonNull WriteBatch batch) {
        DocumentReference ref = getDocumentReference(collectionPath + "/" + id);
        batch.create(ref, pojo);
    }

    /**
     * Overwrites the document referred to by this path.
     * If the document doesn't exist yet, it will be created. If a document already exists, it will be overwritten.
     *
     * @param path A slash-separated path to a document
     * @param fields A map of the fields and values for the document
     * @param batch The batch where to write
     */
    public void setDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields, @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.set(doc, fields);
    }

    /**
     * Overwrites the document referred to by this path.
     * If the document doesn't exist yet, it will be created. If a document already exists, it will be overwritten.
     *
     * @param path A slash-separated path to a document
     * @param pojo A map of the fields and values for the document
     * @param batch The batch where to write
     */
    public void setDocumentField(@NonNull String path, @NonNull Object pojo , @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.set(doc, pojo);
    }

    /**
     * Updates fields in the document referred to by this DocumentReference.
     * If the document doesn't exist yet, the update will fail.
     *
     * @param path A slash-separated path to a document
     * @param fields A map of the fields and values for the document
     * @param batch The batch where to write
     */
    public void updateDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields, @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.update(doc, fields);
    }

    /**
     * Deletes the document referred to by this path. If the document has inner collections they will be also deleted.
     * @param path A slash-separated path to a document
     * @param batch The batch where to write
     */
    public void deleteDocument(@NonNull String path, @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        deleteDocumentCollections(doc, batch);
        batch.delete(doc);
    }

    /**
     * Returns the value at the field or null if the field doesn't exist.
     *
     * @param path A slash-separated path to a document
     * @param field The path to the field
     * @return The value at the given field or null
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    public Object getDocumentField(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).get(field);
    }

    /**
     * Returns the value at the field or null if the field doesn't exist.
     *
     * @param path A slash-separated path to a document
     * @param fieldPath The path to the field
     * @return The value at the given field or null
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    public Object getDocumentField(@NonNull String path, @NonNull FieldPath fieldPath) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).get(fieldPath);
    }

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
    public Double getDoubleFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getDouble(field);
    }

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
    public Date getDateFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getDate(field);
    }

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
    public Boolean getBooleanFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getBoolean(field);
    }

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
    public GeoPoint getGeoPointFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getGeoPoint(field);
    }

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
    public String getStringFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getString(field);
    }

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
    public Timestamp getTimestampFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getTimestamp(field);
    }

    /**
     * Returns the fields of the document as a Map.
     * Field values will be converted to their native Java representation.
     *
     * @param path A slash-separated path to a document
     * @return The fields of the document as a Map
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    public Map<String, Object> getDocumentData(@NonNull String path) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getData();
    }

    /**
     * Returns the contents of the document converted to a POJO.
     *
     * @param path A slash-separated path to a document
     * @param valueType The Java class to create
     * @return The contents of the document in an object of type T
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    public <T> T getDocumentDataAsObject(@NonNull String path, @NonNull Class<T> valueType) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).toObject(valueType);
    }

    /**
     * Returns whether or not the field exists in the document.
     *
     * @param path A slash-separated path to a document
     * @param field The path to the field
     * @return True if the field exists
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    public boolean documentContains(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).contains(field);
    }

    /**
     * Returns whether or not the field exists in the document.
     *
     * @param path A slash-separated path to a document
     * @param fieldPath The path to the field
     * @return True if the field exists
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    public boolean documentContains(@NonNull String path, @NonNull FieldPath fieldPath) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).contains(fieldPath);
    }

    /**
     * Deletes all the document inner collections referred to by this DocumentReference.
     * @param reference The DocumentReference to delete
     * @param batch The batch where to write
     */
    private void deleteDocumentCollections(@NonNull DocumentReference reference, @NonNull WriteBatch batch) {
        Iterable<CollectionReference> collections = reference.listCollections();
        for (CollectionReference collection : collections) {
            Iterable<DocumentReference> documents = collection.listDocuments();
            for (DocumentReference document : documents) {
                deleteDocumentCollections(document, batch);
                batch.delete(document);
            }
        }
    }
}
