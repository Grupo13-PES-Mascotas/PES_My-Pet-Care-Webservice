package org.pesmypetcare.webservice.firebaseservice;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.springframework.lang.NonNull;

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

    public DocumentReference getDocumentReference(@NonNull String path) {
        return db.document(path);
    }

    public DocumentSnapshot getDocumentSnapshot(@NonNull String path)
        throws DatabaseAccessException, DocumentException {
        ApiFuture<DocumentSnapshot> future = db.document(path).get();
        try {
            DocumentSnapshot snapshot = future.get();
            documentSnapshotExists(snapshot);
            return snapshot;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException("retrieval-failed", "The document could not ve retrieves");
        }
    }

    public void documentSnapshotExists(@NonNull DocumentSnapshot snapshot) throws DocumentException {
        if (!snapshot.exists()) {
            throw new DocumentException("invalid-path", "The document does not exist");
        }
    }

    public void createDocument(@NonNull String path, @NonNull Map<String, Object> fields,@NonNull WriteBatch batch) {
        DocumentReference ref = db.collection(path).document();
        batch.create(ref, fields);
    }

    public void createDocument(@NonNull String path, @NonNull Object pojo, @NonNull WriteBatch batch) {
        DocumentReference ref = db.collection(path).document();
        batch.create(ref, pojo);
    }

    public void createDocumentWithId(@NonNull String id, @NonNull String path, @NonNull Map<String, Object> fields,@NonNull WriteBatch batch) {
        DocumentReference ref = getDocumentReference(path);
        batch.create(ref, fields);
    }

    public void createDocumentWithId(@NonNull String id, @NonNull String path, @NonNull Object pojo, @NonNull WriteBatch batch) {
        DocumentReference ref = getDocumentReference(path);
        batch.create(ref, pojo);
    }

    public void setFields(@NonNull String path, @NonNull Map<String, Object> fields, @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.set(doc, fields);
    }

    public void setField(@NonNull String path, @NonNull Object pojo , @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.set(doc, pojo);
    }

    public void updateFields(@NonNull String path, @NonNull Map<String, Object> fields, @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        batch.update(doc, fields);
    }

    public void deleteDocument(@NonNull String path, @NonNull WriteBatch batch) {
        DocumentReference doc = getDocumentReference(path);
        deleteDocumentCollections(doc, batch);
        batch.delete(doc);
    }

    public void deleteDocumentCollections(@NonNull DocumentReference reference, @NonNull WriteBatch batch) {
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
