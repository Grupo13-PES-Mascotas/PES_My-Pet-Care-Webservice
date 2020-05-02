package org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.thirdpartyservices.FirebaseFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

/**
 * @author Santiago Del Rey
 */
@Repository
public class FirestoreCollectionAdapter implements FirestoreCollection {
    private Firestore db;

    public FirestoreCollectionAdapter() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public WriteBatch batch() {
        return db.batch();
    }

    @NonNull
    @Override
    public CollectionReference getCollectionReference(@NonNull String path) {
        return db.collection(path);
    }

    @NonNull
    @Override
    public String getCollectionId(@NonNull String path) {
        return db.collection(path).getId();
    }

    @Nullable
    @Override
    public DocumentReference getCollectionParent(@NonNull String path) {
        return db.collection(path).getParent();
    }

    @NonNull
    @Override
    public Iterable<DocumentReference> listAllCollectionDocuments(@NonNull String path) {
        return db.collection(path).listDocuments();
    }

    @Override
    public Query getCollectionGroup(@NonNull String collectionId) {
        return db.collectionGroup(collectionId);
    }

    @Override
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

    @NonNull
    @Override
    public Query getDocumentsWhereEqualTo(@NonNull String collectionPath, @NonNull String field,
                                          @Nullable Object value) {
        return db.collection(collectionPath).whereEqualTo(field, value);
    }

    @NonNull
    @Override
    public Query getDocumentsWhereEqualTo(@NonNull String collectionPath, @NonNull FieldPath fieldPath,
                                          @Nullable Object value) {
        return db.collection(collectionPath).whereEqualTo(fieldPath, value);
    }

    @NonNull
    @Override
    public Query getDocumentsWhereArrayContains(@NonNull String collectionPath, @NonNull String field,
                                                @NonNull Object value) {
        return db.collection(collectionPath).whereArrayContains(field, value);
    }

    @NonNull
    @Override
    public Query getDocumentsWhereArrayContains(@NonNull String collectionPath, @NonNull FieldPath fieldPath,
                                                @NonNull Object value) {
        return db.collection(collectionPath).whereArrayContains(fieldPath, value);
    }

    @NonNull
    @Override
    public Query getCollectionGroupDocumentsWhereEqualTo(@NonNull String collectionId, @NonNull String field,
                                                         @Nullable Object value) {
        return db.collectionGroup(collectionId).whereEqualTo(field, value);
    }

    @NonNull
    @Override
    public Query getCollectionGroupDocumentsWhereEqualTo(@NonNull String collectionId, @NonNull FieldPath fieldPath,
                                                         @Nullable Object value) {
        return db.collectionGroup(collectionId).whereEqualTo(fieldPath, value);
    }

    @NonNull
    @Override
    public Query getCollectionGroupDocumentsWhereArrayContains(@NonNull String collectionId, @NonNull String field,
                                                               @NonNull Object value) {
        return db.collectionGroup(collectionId).whereArrayContains(field, value);
    }

    @NonNull
    @Override
    public Query getCollectionGroupDocumentsWhereArrayContains(@NonNull String collectionId,
                                                               @NonNull FieldPath fieldPath, @NonNull Object value) {
        return db.collectionGroup(collectionId).whereArrayContains(fieldPath, value);
    }
}
