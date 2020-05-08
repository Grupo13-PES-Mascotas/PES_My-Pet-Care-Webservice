package org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.FirebaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Santiago Del Rey
 */
@Repository
public class FirestoreCollectionAdapter implements FirestoreCollection {
    private static final String INVALID_FIELD_MESSAGE = "Invalid field. Field must not be null or empty";
    private Firestore db;
    @Autowired
    private FirestoreDocument documentAdapter;

    public FirestoreCollectionAdapter() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public WriteBatch batch() {
        return db.batch();
    }

    @Override
    public void commitBatch(@NonNull WriteBatch batch) throws DatabaseAccessException {
        try {
            batch.commit().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("write-failed", e.getMessage());
        }
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

    @NonNull
    @Override
    public List<DocumentSnapshot> listAllCollectionDocumentSnapshots(@NonNull String path)
        throws DatabaseAccessException {
        Iterable<DocumentReference> iterable = listAllCollectionDocuments(path);
        List<DocumentSnapshot> snapshots = new ArrayList<>();
        for (DocumentReference doc : iterable) {
            try {
                snapshots.add(documentAdapter.getDocumentSnapshot(doc.getPath()));
            } catch (DocumentException ignore) { }
        }
        return snapshots;
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
    public ApiFuture<QuerySnapshot> getDocumentsWhereEqualTo(@NonNull String collectionPath, @NonNull String field,
                                                             @Nullable Object value, Object... moreFieldsAndValues) {
        checkArguments(moreFieldsAndValues);
        Query query = db.collection(collectionPath).whereEqualTo(field, value);
        return concatenateWhereEqualTo(query, moreFieldsAndValues);
    }

    @NonNull
    @Override
    public ApiFuture<QuerySnapshot> getDocumentsWhereEqualTo(@NonNull String collectionPath,
                                                             @NonNull FieldPath fieldPath, @Nullable Object value,
                                                             Object... moreFieldsAndValues) {
        checkArguments(moreFieldsAndValues);
        Query query = db.collection(collectionPath).whereEqualTo(fieldPath, value);
        return concatenateWhereEqualToFieldPath(query, moreFieldsAndValues);
    }

    @NonNull
    @Override
    public ApiFuture<QuerySnapshot> getDocumentsWhereArrayContains(@NonNull String collectionPath,
                                                                   @NonNull String field, @NonNull Object value,
                                                                   Object... moreFieldsAndValues) {
        checkArguments(moreFieldsAndValues);
        Query query = db.collection(collectionPath).whereArrayContains(field, value);
        return concatenateWhereArrayContains(query, moreFieldsAndValues);
    }

    @NonNull
    @Override
    public ApiFuture<QuerySnapshot> getDocumentsWhereArrayContains(@NonNull String collectionPath,
                                                                   @NonNull FieldPath fieldPath, @NonNull Object value,
                                                                   Object... moreFieldsAndValues) {
        checkArguments(moreFieldsAndValues);
        Query query = db.collection(collectionPath).whereArrayContains(fieldPath, value);
        return concatenateWhereArrayContainsFieldPath(query, moreFieldsAndValues);
    }

    @NonNull
    @Override
    public ApiFuture<QuerySnapshot> getCollectionGroupDocumentsWhereEqualTo(@NonNull String collectionId,
                                                                            @NonNull String field,
                                                                            @Nullable Object value,
                                                                            Object... moreFieldsAndValues) {
        checkArguments(moreFieldsAndValues);
        Query query = db.collectionGroup(collectionId).whereEqualTo(field, value);
        return concatenateWhereEqualTo(query, moreFieldsAndValues);
    }

    @NonNull
    @Override
    public ApiFuture<QuerySnapshot> getCollectionGroupDocumentsWhereEqualTo(@NonNull String collectionId,
                                                                            @NonNull FieldPath fieldPath,
                                                                            @Nullable Object value,
                                                                            Object... moreFieldsAndValues) {
        checkArguments(moreFieldsAndValues);
        Query query = db.collectionGroup(collectionId).whereEqualTo(fieldPath, value);
        return concatenateWhereEqualToFieldPath(query, moreFieldsAndValues);
    }

    @NonNull
    @Override
    public ApiFuture<QuerySnapshot> getCollectionGroupDocumentsWhereArrayContains(@NonNull String collectionId,
                                                                                  @NonNull String field,
                                                                                  @NonNull Object value,
                                                                                  Object... moreFieldsAndValues) {
        checkArguments(moreFieldsAndValues);
        Query query = db.collectionGroup(collectionId).whereArrayContains(field, value);
        return concatenateWhereArrayContains(query, moreFieldsAndValues);
    }

    @NonNull
    @Override
    public ApiFuture<QuerySnapshot> getCollectionGroupDocumentsWhereArrayContains(@NonNull String collectionId,
                                                                                  @NonNull FieldPath fieldPath,
                                                                                  @NonNull Object value,
                                                                                  Object... moreFieldsAndValues) {
        checkArguments(moreFieldsAndValues);
        Query query = db.collectionGroup(collectionId).whereArrayContains(fieldPath, value);
        return concatenateWhereArrayContainsFieldPath(query, moreFieldsAndValues);
    }

    /**
     * Checks that the number of arguments received is correct.
     *
     * @param fieldsAndValues The fields and values to check
     * @throws IllegalArgumentException When the number of arguments is odd
     */
    private void checkArguments(Object[] fieldsAndValues) {
        if (fieldsAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Wrong number of arguments. There must be an even number of arguments");
        }
    }

    /**
     * Returns a query snapshot of the documents that contain the given fields and theirs values are equals to the
     * specified ones.
     *
     * @param query the query where to add the filters
     * @param moreFieldsAndValues The pair of field and values
     * @return An ApiFuture that will be resolved with the results of the Query
     * @throws IllegalArgumentException When the field is empty or null
     */
    private ApiFuture<QuerySnapshot> concatenateWhereEqualTo(Query query, Object[] moreFieldsAndValues) {
        String field;
        Object value;
        for (int i = 0; i < moreFieldsAndValues.length; i += 2) {
            field = (String) moreFieldsAndValues[i];
            value = moreFieldsAndValues[i + 1];
            if (field == null || field.isEmpty()) {
                throw new IllegalArgumentException(INVALID_FIELD_MESSAGE);
            }
            query.whereEqualTo(field, value);
        }
        return query.get();
    }

    /**
     * Returns a query snapshot of the documents that contain the given fields and theirs values are equals to the
     * specified ones.
     *
     * @param query the query where to add the filters
     * @param moreFieldsAndValues The pair of field and values
     * @return An ApiFuture that will be resolved with the results of the Query
     * @throws IllegalArgumentException When the field is empty or null
     */
    private ApiFuture<QuerySnapshot> concatenateWhereEqualToFieldPath(Query query, Object[] moreFieldsAndValues) {
        FieldPath fieldPath;
        Object value;
        for (int i = 0; i < moreFieldsAndValues.length; i += 2) {
            fieldPath = (FieldPath) moreFieldsAndValues[i];
            value = moreFieldsAndValues[i + 1];
            if (fieldPath == null || fieldPath.toString().isEmpty()) {
                throw new IllegalArgumentException(INVALID_FIELD_MESSAGE);
            }
            query.whereEqualTo(fieldPath, value);
        }
        return query.get();
    }

    /**
     * Returns a query snapshot of the documents that contain the given array fields and they contain the values
     * specified.
     *
     * @param query the query where to add the filters
     * @param moreFieldsAndValues The pair of field and values
     * @return An ApiFuture that will be resolved with the results of the Query
     * @throws IllegalArgumentException When the field is empty or null
     */
    private ApiFuture<QuerySnapshot> concatenateWhereArrayContains(Query query, Object[] moreFieldsAndValues) {
        String field;
        Object value;
        for (int i = 0; i < moreFieldsAndValues.length; i += 2) {
            field = (String) moreFieldsAndValues[i];
            value = moreFieldsAndValues[i + 1];
            if (field == null || field.isEmpty()) {
                throw new IllegalArgumentException(INVALID_FIELD_MESSAGE);
            }
            query.whereArrayContains(field, value);
        }
        return query.get();
    }

    /**
     * Returns a query snapshot of the documents that contain the given array fields and they contain the values
     * specified.
     *
     * @param query the query where to add the filters
     * @param moreFieldsAndValues The pair of field and values
     * @return An ApiFuture that will be resolved with the results of the Query
     * @throws IllegalArgumentException When the field is empty or null
     */
    private ApiFuture<QuerySnapshot> concatenateWhereArrayContainsFieldPath(Query query, Object[] moreFieldsAndValues) {
        FieldPath fieldPath;
        Object value;
        for (int i = 0; i < moreFieldsAndValues.length; i += 2) {
            fieldPath = (FieldPath) moreFieldsAndValues[i];
            value = moreFieldsAndValues[i + 1];
            if (fieldPath == null || fieldPath.toString().isEmpty()) {
                throw new IllegalArgumentException(INVALID_FIELD_MESSAGE);
            }
            query.whereArrayContains(fieldPath, value);
        }
        return query.get();
    }
}
