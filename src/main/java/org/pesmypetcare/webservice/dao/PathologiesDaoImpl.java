package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.pesmypetcare.webservice.entity.PathologiesEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class PathologiesDaoImpl implements PathologiesDao {
    private static final String DELFAIL_KEY = "deletion-failed";
    private static final String PATHOLOGIES_DOES_NOT_EXIST_EXC = "The pathologies does not exist";
    private static final String INVALID_PATHOLOGIES_EXC = "invalid-pet";
    private static final String INTERNAL_LIST_STRING_1 = "date";
    private static final String INTERNAL_LIST_STRING_2 = "body";


    private Firestore db;


    public PathologiesDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public void createPathologies(String owner, String petName, String date, PathologiesEntity pathologiesEntity) {
        DocumentReference pathologiesRef = getPathologiessRef(owner, petName).document(date);
        pathologiesRef.set(pathologiesEntity);
    }

    @Override
    public void deleteAllPathologiess(String owner, String petName) throws DatabaseAccessException {
        CollectionReference pathologiessRef = getPathologiessRef(owner, petName);
        try {
            ApiFuture<QuerySnapshot> future = pathologiessRef.get();
            List<QueryDocumentSnapshot> pathologiesDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot pathologiesDocument : pathologiesDocuments) {
                pathologiesDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
    }

    @Override
    public void deletePathologiesByDate(String owner, String petName, String petDate) {
        DocumentReference pathologiesRef = getPathologiessRef(owner, petName).document(petDate);
        pathologiesRef.delete();
    }

    @Override
    public PathologiesEntity getPathologiesByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        CollectionReference pathologiessRef = getPathologiessRef(owner, petName);
        DocumentReference pathologiesRef = pathologiessRef.document(petDate);
        ApiFuture<DocumentSnapshot> future = pathologiesRef.get();
        DocumentSnapshot mealDoc;
        try {
            mealDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!mealDoc.exists()) {
            throw new DatabaseAccessException(INVALID_PATHOLOGIES_EXC, PATHOLOGIES_DOES_NOT_EXIST_EXC);
        }
        return mealDoc.toObject(PathologiesEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllPathologies(String owner, String petName) throws DatabaseAccessException {
        CollectionReference pathologiessRef = getPathologiessRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getAllPathologiessOfAPetFromDatabase(pathologiessRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public List<Map<String, Object>> getAllPathologiessBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        CollectionReference pathologiessRef = getPathologiessRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getPathologiessBetweenDatesFromDatabase(initialDate, finalDate, pathologiessRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public void updatePathologies(String owner, String petName, String petDate, Object value) {
        CollectionReference pathologiessRef = getPathologiessRef(owner, petName);
        DocumentReference pathologiesRef = pathologiessRef.document(petDate);
        pathologiesRef.update("pathologiesValue", value);
    }

    /**
     * Return the pathologies collection of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return Return the pathologies collection of one pet
     */
    public CollectionReference getPathologiessRef(String owner, String petName) {
        return db.collection("users").document(owner).collection("pets").document(petName)
            .collection("pathologiess");
    }

    /**
     * Gets all the pathologiess of the collection and puts them in the externalList.
     * @param pathologiessRef Reference to the collection of pathologiess
     * @param externalList list that will contain all the pathologiess
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getAllPathologiessOfAPetFromDatabase(CollectionReference pathologiessRef, List<Map<String, Object>> externalList)
        throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = pathologiessRef.get();
        List<QueryDocumentSnapshot> pathologiesDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot pathologiesDocument : pathologiesDocuments) {
            Map<String, Object> internalList = new HashMap<>();
            internalList.put(INTERNAL_LIST_STRING_1, pathologiesDocument.getId());
            internalList.put(INTERNAL_LIST_STRING_2, pathologiesDocument.toObject(PathologiesEntity.class));
            externalList.add(internalList);
        }
    }

    /**
     * Gets all the pathologiess of the collection between the initial and final dates without taking them into account and
     * puts them in the externalList.
     * @param initialDate Initial date
     * @param finalDate Final date
     * @param pathologiessRef Reference to the collection of pathologiess
     * @param externalList list that will contain all the pathologiess
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getPathologiessBetweenDatesFromDatabase(String initialDate, String finalDate,
                                                    CollectionReference pathologiessRef, List<Map<String,
        Object>> externalList) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = pathologiessRef.get();
        List<QueryDocumentSnapshot> pathologiesDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot pathologiesDocument : pathologiesDocuments) {
            String date = pathologiesDocument.getId();
            if (initialDate.compareTo(date) < 0 && finalDate.compareTo(date) > 0) {
                Map<String, Object> internalList = new HashMap<>();
                internalList.put(INTERNAL_LIST_STRING_1, date);
                internalList.put(INTERNAL_LIST_STRING_2, pathologiesDocument.toObject(PathologiesEntity.class));
                externalList.add(internalList);
            }
        }
    }
}
