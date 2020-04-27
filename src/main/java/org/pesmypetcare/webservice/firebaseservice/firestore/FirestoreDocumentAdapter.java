package org.pesmypetcare.webservice.firebaseservice.firestore;

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
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Santiago Del Rey
 */
@Repository
public class FirestoreDocumentAdapter implements FirestoreDocument {
    private Firestore db;

    public FirestoreDocumentAdapter() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public DocumentReference getDocumentReference(@NonNull String path) {
        return db.document(path);
    }

    @Override
    public String getDocumentId(@NonNull String path) {
        return getDocumentReference(path).getId();
    }

    @Override
    public CollectionReference getDocumentParent(@NonNull String path) {
        return getDocumentReference(path).getParent();
    }

    @Override
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

    @Override
    public boolean documentSnapshotExists(@NonNull DocumentSnapshot snapshot) {
        return snapshot.exists();
    }

    @Override
    public void createDocument(@NonNull String path, @NonNull Map<String, Object> fields, @NonNull WriteBatch batch) {
        DocumentReference ref = db.collection(path).document();
        batch.create(ref, fields);
    }

    @Override
    public void createDocument(@NonNull String path, @NonNull Object pojo, @NonNull WriteBatch batch) {
        DocumentReference ref = db.collection(path).document();
        batch.create(ref, pojo);
    }

    @Override
    public void createDocumentWithId(@NonNull String id, @NonNull String collectionPath, @NonNull Map<String, Object> fields, @NonNull WriteBatch batch) {
        DocumentReference ref = getDocumentReference(collectionPath + "/" + id);
        batch.create(ref, fields);
    }

    @Override
    public void createDocumentWithId(@NonNull String id, @NonNull String collectionPath, @NonNull Object pojo, @NonNull WriteBatch batch) {
        DocumentReference ref = getDocumentReference(collectionPath + "/" + id);
        batch.create(ref, pojo);
    }

    @Override
    public void setDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields, @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.set(doc, fields);
    }

    @Override
    public void setDocumentField(@NonNull String path, @NonNull Object pojo, @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.set(doc, pojo);
    }

    @Override
    public void updateDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields, @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.update(doc, fields);
    }

    @Override
    public void deleteDocument(@NonNull String path, @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        deleteDocumentCollections(doc, batch);
        batch.delete(doc);
    }

    @Override
    public Object getDocumentField(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).get(field);
    }

    @Override
    public Object getDocumentField(@NonNull String path, @NonNull FieldPath fieldPath) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).get(fieldPath);
    }

    @Override
    @Nullable
    public Double getDoubleFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getDouble(field);
    }

    @Override
    public Date getDateFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getDate(field);
    }

    @Override
    @Nullable
    public Boolean getBooleanFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getBoolean(field);
    }

    @Override
    public GeoPoint getGeoPointFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getGeoPoint(field);
    }

    @Override
    public String getStringFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getString(field);
    }

    @Override
    public Timestamp getTimestampFromDocument(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getTimestamp(field);
    }

    @Override
    public Map<String, Object> getDocumentData(@NonNull String path) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getData();
    }

    @Override
    public <T> T getDocumentDataAsObject(@NonNull String path, @NonNull Class<T> valueType) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).toObject(valueType);
    }

    @Override
    public boolean documentContains(@NonNull String path, @NonNull String field) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).contains(field);
    }

    @Override
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
