package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.pesmypetcare.webservice.entity.WeightEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class WeightDaoImpl implements WeightDao {
    private final String DELFAIL_KEY;
    private final String WEIGHT_DOES_NOT_EXIST_EXC;
    private final String INVALID_WEIGHT_EXC;

    private Firestore db;


    public WeightDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();

        DELFAIL_KEY = "deletion-failed";
        WEIGHT_DOES_NOT_EXIST_EXC = "The weight does not exist";
        INVALID_WEIGHT_EXC = "invalid-pet";
    }

    @Override
    public void createWeight(String owner, String petName, String date, WeightEntity weightEntity) {
        DocumentReference weightRef = getWeightsRef(owner, petName).document(date);
        weightRef.set(weightEntity);
        System.out.println("Weight created");
    }

    @Override
    public void deleteAllWeights(String owner, String petName) throws DatabaseAccessException {
        CollectionReference weightsRef = getWeightsRef(owner, petName);
        try {
            ApiFuture<QuerySnapshot> future = weightsRef.get();
            List<QueryDocumentSnapshot> weightDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot weightDocument : weightDocuments) {
                weightDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
    }

    @Override
    public void deleteWeightByDate(String owner, String petName, String petDate) {
        DocumentReference weightRef = getWeightsRef(owner, petName).document(petDate);
        weightRef.delete();
    }

    @Override
    public WeightEntity getWeightByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        CollectionReference weightsRef = getWeightsRef(owner, petName);
        DocumentReference weightRef = weightsRef.document(petDate);
        ApiFuture<DocumentSnapshot> future = weightRef.get();
        DocumentSnapshot mealDoc;
        try {
            mealDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!mealDoc.exists()) {
            throw new DatabaseAccessException(INVALID_WEIGHT_EXC, WEIGHT_DOES_NOT_EXIST_EXC);
        }
        return mealDoc.toObject(WeightEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllWeight(String owner, String petName) throws DatabaseAccessException {
        CollectionReference weightsRef = getWeightsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getAllWeightsOfAPetFromDatabase(weightsRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public List<Map<String, Object>> getAllWeightsBetween(String owner, String petName, String initialDate, String finalDate) throws DatabaseAccessException {
        CollectionReference weightsRef = getWeightsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getWeightsBetweenDatesFromDatabase(initialDate, finalDate, weightsRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public void updateWeight(String owner, String petName, String petDate, double value, String field) {
        CollectionReference weightsRef = getWeightsRef(owner, petName);
        DocumentReference weightRef = weightsRef.document(petDate);
        weightRef.update(field, value);
    }

    public CollectionReference getWeightsRef(String owner, String petName) {
        return db.collection("users").document(owner).collection("pets").document(petName)
            .collection("weights");
    }

    /**
     * Gets all the weights of the collection and puts them in the externalList.
     * @param weightsRef Reference to the collection of weights
     * @param externalList list that will contain all the weights
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getAllWeightsOfAPetFromDatabase(CollectionReference weightsRef, List<Map<String, Object>> externalList)
        throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = weightsRef.get();
        List<QueryDocumentSnapshot> weightDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot weightDocument : weightDocuments) {
            Map<String, Object> internalList = new HashMap<>();
            internalList.put("date", weightDocument.getId());
            internalList.put("body", weightDocument.toObject(WeightEntity.class));
            externalList.add(internalList);
        }
    }

    /**
     * Gets all the weights of the collection between the initial and final dates without taking them into account and
     * puts them in the externalList.
     * @param initialDate Initial date
     * @param finalDate Final date
     * @param weightsRef Reference to the collection of weights
     * @param externalList list that will contain all the weights
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getWeightsBetweenDatesFromDatabase(String initialDate, String finalDate, CollectionReference weightsRef,
                                                  List<Map<String, Object>> externalList) throws InterruptedException,
        ExecutionException {
        ApiFuture<QuerySnapshot> future = weightsRef.get();
        List<QueryDocumentSnapshot> weightDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot weightDocument : weightDocuments) {
            String date = weightDocument.getId();
            if (initialDate.compareTo(date) < 0 && finalDate.compareTo(date) > 0) {
                Map<String, Object> internalList = new HashMap<>();
                internalList.put("date", date);
                internalList.put("body", weightDocument.toObject(WeightEntity.class));
                externalList.add(internalList);
            }
        }
    }
}
