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

    public CollectionReference getCollectionReference(@NonNull String path) {
        return db.collection(path);
    }

    public String getCollectionId(@NonNull String path) {
        return db.collection(path).getId();
    }

    public DocumentReference getCollectionParent(@NonNull String path) {
        return db.collection(path).getParent();
    }

    public Iterable<DocumentReference> listAllCollectionDocuments(@NonNull String path) {
        return db.collection(path).listDocuments();
    }

    public Query getCollectionGroup(@NonNull String collectionId) {
        return db.collectionGroup(collectionId);
    }

    public void deleteCollection(@NonNull CollectionReference collection, @NonNull WriteBatch batch) {
        Iterable<DocumentReference> documents = collection.listDocuments();
        for (DocumentReference doc : documents) {
            Iterable<CollectionReference> innerCollections = doc.listCollections();
            for (CollectionReference innerCollection : innerCollections) {
                deleteCollection(innerCollection, batch);
            }
            batch.delete(doc);
        }
    }

    public Query getDocumentsWhereEqualTo(@NonNull String collectionPath, @NonNull String field,
                                          @NonNull Object value) {
        return db.collection(collectionPath).whereEqualTo(field, value);
    }

    public Query getDocumentsWhereEqualTo(@NonNull String collectionPath, @NonNull FieldPath fieldPath,
                                          @NonNull Object value) {
        return db.collection(collectionPath).whereEqualTo(fieldPath, value);
    }

    public Query getDocumentsWhereArrayContains(@NonNull String collectionPath, @NonNull String field,
                                                @NonNull Object value) {
        return db.collection(collectionPath).whereArrayContains(field, value);
    }

    public Query getDocumentsWhereArrayContains(@NonNull String collectionPath, @NonNull FieldPath fieldPath,
                                                @NonNull Object value) {
        return db.collection(collectionPath).whereArrayContains(fieldPath, value);
    }

    public Query getCollectionGroupDocumentsWhereEqualTo(@NonNull String collectionId, @NonNull String field,
                                                         @NonNull Object value) {
        return db.collectionGroup(collectionId).whereEqualTo(field, value);
    }

    public Query getCollectionGroupDocumentsWhereEqualTo(@NonNull String collectionId, @NonNull FieldPath fieldPath,
                                                         @NonNull Object value) {
        return db.collectionGroup(collectionId).whereEqualTo(fieldPath, value);
    }

    public Query getCollectionGroupDocumentsWhereArrayContains(@NonNull String collectionId, @NonNull String field,
                                                               @NonNull Object value) {
        return db.collectionGroup(collectionId).whereArrayContains(field, value);
    }

    public Query getCollectionGroupDocumentsWhereArrayContains(@NonNull String collectionId,
                                                               @NonNull FieldPath fieldPath, @NonNull Object value) {
        return db.collectionGroup(collectionId).whereArrayContains(fieldPath, value);
    }
}
