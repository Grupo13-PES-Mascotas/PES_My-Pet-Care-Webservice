package org.pesmypetcare.webservice.firebaseservice;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.WriteBatch;

/**
 * @author Santiago Del Rey
 */
public class FirestoreCollectionAdapter {
    private Firestore db;

    public FirestoreCollectionAdapter() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    public CollectionReference getCollectionReference(String path) {
        return db.collection(path);
    }

    public String getCollectionId(String path) {
        return db.collection(path).getId();
    }

    public DocumentReference getCollectionParent(String path) {
        return db.collection(path).getParent();
    }

    public Iterable<DocumentReference> listAllCollectionDocuments(String path) {
        return db.collection(path).listDocuments();
    }

    public Query getCollectionGroup(String collectionId) {
        return db.collectionGroup(collectionId);
    }

    public void deleteAllCollectionDocuments(CollectionReference collection, WriteBatch batch) {
        Iterable<DocumentReference> documents = collection.listDocuments();
        for (DocumentReference doc : documents) {
            Iterable<CollectionReference> innerCollections = doc.listCollections();
            for (CollectionReference innerCollection : innerCollections) {
                deleteAllCollectionDocuments(innerCollection, batch);
            }
            batch.delete(doc);
        }
    }

    public Query getDocumentsWhereEqualTo(String collectionPath, String field, Object value) {
        return db.collection(collectionPath).whereEqualTo(field, value);
    }

    public Query getDocumentsWhereEqualTo(String collectionPath, FieldPath fieldPath, Object value) {
        return db.collection(collectionPath).whereEqualTo(fieldPath, value);
    }

    public Query getDocumentsWhereArrayContains(String collectionPath, String field, Object value) {
        return db.collection(collectionPath).whereArrayContains(field, value);
    }

    public Query getDocumentsWhereArrayContains(String collectionPath, FieldPath fieldPath, Object value) {
        return db.collection(collectionPath).whereArrayContains(fieldPath, value);
    }

    public Query getCollectionGroupDocumentsWhereEqualTo(String collectionId, String field, Object value) {
        return db.collectionGroup(collectionId).whereEqualTo(field, value);
    }

    public Query getCollectionGroupDocumentsWhereEqualTo(String collectionId, FieldPath fieldPath, Object value) {
        return db.collectionGroup(collectionId).whereEqualTo(fieldPath, value);
    }

    public Query getCollectionGroupDocumentsWhereArrayContains(String collectionId, String field, Object value) {
        return db.collectionGroup(collectionId).whereArrayContains(field, value);
    }

    public Query getCollectionGroupDocumentsWhereArrayContains(String collectionId, FieldPath fieldPath, Object value) {
        return db.collectionGroup(collectionId).whereArrayContains(fieldPath, value);
    }
}
