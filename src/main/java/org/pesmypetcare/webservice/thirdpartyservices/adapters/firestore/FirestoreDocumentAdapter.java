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
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.FirebaseFactory;
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
    private static final String DOCUMENT_NOT_RETRIEVED = "The document could not be retrieved";
    private static final String RETRIEVAL_FAILED_CODE = "retrieval-failed";
    private Firestore db;

    public FirestoreDocumentAdapter() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public WriteBatch batch() {
        return db.batch();
    }

    @Override
    public void commitBatch(@NonNull WriteBatch batch) throws DatabaseAccessException, DocumentException {
        try {
            batch.commit().get();
        } catch (InterruptedException e) {
            throw new DatabaseAccessException("write-failed", e.getMessage());
        } catch (ExecutionException e) {
            throw new DocumentException("write-failed", e.getMessage());
        }
    }

    @NonNull
    @Override
    public DocumentReference getDocumentReference(@NonNull String path) {
        System.out.println("Document " + path);
        return db.document(path);
    }

    @NonNull
    @Override
    public String getDocumentId(@NonNull String path) {
        return getDocumentReference(path).getId();
    }

    @NonNull
    @Override
    public CollectionReference getDocumentParent(@NonNull String path) {
        return getDocumentReference(path).getParent();
    }

    @NonNull
    @Override
    public DocumentSnapshot getDocumentSnapshot(@NonNull String path)
        throws DatabaseAccessException, DocumentException {
        ApiFuture<DocumentSnapshot> future = db.document(path).get();
        try {
            DocumentSnapshot snapshot = future.get();
            if (!documentSnapshotExists(snapshot)) {
                throw new DocumentException("document-not-exists", "The document does not exist");
            }
            return snapshot;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException(RETRIEVAL_FAILED_CODE, DOCUMENT_NOT_RETRIEVED);
        }
    }

    @Override
    public boolean documentSnapshotExists(@NonNull DocumentSnapshot snapshot) {
        return snapshot.exists();
    }

    @Override
    public boolean documentExists(@NonNull String path) throws DatabaseAccessException {
        ApiFuture<DocumentSnapshot> future = db.document(path).get();
        try {
            DocumentSnapshot snapshot = future.get();
            return documentSnapshotExists(snapshot);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException(RETRIEVAL_FAILED_CODE, DOCUMENT_NOT_RETRIEVED);
        }
    }

    @Override
    public DocumentReference createDocument(@NonNull String path, @NonNull Map<String, Object> fields,
                                            @NonNull WriteBatch batch) {
        DocumentReference ref = db.collection(path).document();
        batch.create(ref, fields);
        return ref;
    }

    @Override
    public DocumentReference createDocument(@NonNull String path, @NonNull Map<String, Object> fields) {
        DocumentReference ref = db.collection(path).document();
        ref.create(fields);
        return ref;
    }

    @Override
    public DocumentReference createDocument(@NonNull String path, @NonNull Object pojo, @NonNull WriteBatch batch) {
        DocumentReference ref = db.collection(path).document();
        batch.create(ref, pojo);
        return ref;
    }

    @Override
    public DocumentReference createDocument(@NonNull String path, @NonNull Object pojo) {
        DocumentReference ref = db.collection(path).document();
        ref.create(pojo);
        return ref;
    }

    @Override
    public DocumentReference createDocumentWithId(@NonNull String collectionPath, @NonNull String id,
                                                  @NonNull Map<String, Object> fields, @NonNull WriteBatch batch) {
        DocumentReference ref = getDocumentReference(collectionPath + "/" + id);
        batch.create(ref, fields);
        return ref;
    }

    @Override
    public DocumentReference createDocumentWithId(@NonNull String collectionPath, @NonNull String id,
                                                  @NonNull Map<String, Object> fields) {
        DocumentReference ref = getDocumentReference(collectionPath + "/" + id);
        ref.create(fields);
        return ref;
    }

    @Override
    public DocumentReference createDocumentWithId(@NonNull String collectionPath, @NonNull String id,
                                                  @NonNull Object pojo, @NonNull WriteBatch batch) {
        DocumentReference ref = getDocumentReference(collectionPath + "/" + id);
        batch.create(ref, pojo);
        return ref;
    }

    @Override
    public DocumentReference createDocumentWithId(@NonNull String collectionPath, @NonNull String id,
                                                  @NonNull Object pojo) {
        DocumentReference ref = getDocumentReference(collectionPath + "/" + id);
        ref.create(pojo);
        return ref;
    }

    @Override
    public void setDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields,
                                  @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.set(doc, fields);
    }

    @Override
    public void setDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields) {
        getDocumentReference(path).set(fields);
    }

    @Override
    public void setDocumentFields(@NonNull String path, @NonNull Object pojo, @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.set(doc, pojo);
    }

    @Override
    public void setDocumentFields(@NonNull String path, @NonNull Object pojo) {
        getDocumentReference(path).set(pojo);
    }

    @Override
    public void updateDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields,
                                     @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.update(doc, fields);
    }

    @Override
    public void updateDocumentFields(@NonNull String path, @NonNull Map<String, Object> fields) {
        getDocumentReference(path).update(fields);
    }

    @Override
    public void updateDocumentFields(@NonNull WriteBatch batch, @NonNull String path, @NonNull FieldPath fieldPath,
                                     @Nullable Object value, Object... moreFieldsAndValues) {
        DocumentReference doc = getDocumentReference(path);
        batch.update(doc, fieldPath, value, moreFieldsAndValues);
    }

    @Override
    public void updateDocumentFields(@NonNull String path, @NonNull FieldPath fieldPath, @Nullable Object value,
                                     Object... moreFieldsAndValues) {
        getDocumentReference(path).update(fieldPath, value, moreFieldsAndValues);
    }

    @Override
    public void updateDocumentFields(@NonNull WriteBatch batch, @NonNull String path, @NonNull String field,
                                     @Nullable Object value, Object... moreFieldsAndValues) {
        DocumentReference doc = getDocumentReference(path);
        batch.update(doc, field, value, moreFieldsAndValues);
    }

    @Override
    public void updateDocumentFields(@NonNull String path, @NonNull String field, @Nullable Object value,
                                     Object... moreFieldsAndValues) {
        getDocumentReference(path).update(field, value, moreFieldsAndValues);
    }

    @Override
    public void deleteDocument(@NonNull String path, @NonNull WriteBatch batch) {
        System.out.println("Path delete doc: " + path);
        DocumentReference doc = getDocumentReference(path);
        deleteDocumentCollections(doc, batch);
        batch.delete(doc);
    }

    @Override
    public void deleteDocument(@NonNull String path) {
        DocumentReference doc = getDocumentReference(path);
        deleteDocumentCollections(doc);
        doc.delete();
    }

    @Nullable
    @Override
    public Object getDocumentField(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).get(field);
    }

    @Nullable
    @Override
    public Object getDocumentField(@NonNull String path, @NonNull FieldPath fieldPath)
        throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).get(fieldPath);
    }

    @Nullable
    @Override
    public Double getDoubleFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getDouble(field);
    }

    @Nullable
    @Override
    public Date getDateFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getDate(field);
    }

    @Override
    @Nullable
    public Boolean getBooleanFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getBoolean(field);
    }

    @Nullable
    @Override
    public GeoPoint getGeoPointFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getGeoPoint(field);
    }

    @Nullable
    @Override
    public String getStringFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getString(field);
    }

    @Nullable
    @Override
    public Timestamp getTimestampFromDocument(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getTimestamp(field);
    }

    @Nullable
    @Override
    public Map<String, Object> getDocumentData(@NonNull String path) throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).getData();
    }

    @Nullable
    @Override
    public <T> T getDocumentDataAsObject(@NonNull String path, @NonNull Class<T> valueType)
        throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).toObject(valueType);
    }

    @Override
    public boolean documentContains(@NonNull String path, @NonNull String field)
        throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).contains(field);
    }

    @Override
    public boolean documentContains(@NonNull String path, @NonNull FieldPath fieldPath)
        throws DatabaseAccessException, DocumentException {
        return getDocumentSnapshot(path).contains(fieldPath);
    }

    /**
     * Deletes all the document inner collections referred to by this DocumentReference.
     *
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

    /**
     * Deletes all the document inner collections referred to by this DocumentReference.
     *
     * @param reference The DocumentReference to delete
     */
    private void deleteDocumentCollections(@NonNull DocumentReference reference) {
        Iterable<CollectionReference> collections = reference.listCollections();
        for (CollectionReference collection : collections) {
            Iterable<DocumentReference> documents = collection.listDocuments();
            for (DocumentReference document : documents) {
                deleteDocumentCollections(document);
                document.delete();
            }
        }
    }
}
