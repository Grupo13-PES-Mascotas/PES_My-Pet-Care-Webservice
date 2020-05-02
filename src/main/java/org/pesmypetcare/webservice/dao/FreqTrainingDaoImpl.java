package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.pesmypetcare.webservice.entity.FreqTrainingEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.thirdpartyservices.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class FreqTrainingDaoImpl implements FreqTrainingDao {
    private static final String DELFAIL_KEY = "deletion-failed";
    private static final String FREQTRAINING_DOES_NOT_EXIST_EXC = "The freqTraining does not exist";
    private static final String INVALID_FREQTRAINING_EXC = "invalid-pet";
    private static final String INTERNAL_LIST_STRING_1 = "date";
    private static final String INTERNAL_LIST_STRING_2 = "body";


    private Firestore db;


    public FreqTrainingDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public void createFreqTraining(String owner, String petName, String date, FreqTrainingEntity freqTrainingEntity) {
        DocumentReference freqTrainingRef = getFreqTrainingsRef(owner, petName).document(date);
        freqTrainingRef.set(freqTrainingEntity);
    }

    @Override
    public void deleteAllFreqTrainings(String owner, String petName) throws DatabaseAccessException {
        CollectionReference freqTrainingsRef = getFreqTrainingsRef(owner, petName);
        try {
            ApiFuture<QuerySnapshot> future = freqTrainingsRef.get();
            List<QueryDocumentSnapshot> freqTrainingDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot freqTrainingDocument : freqTrainingDocuments) {
                freqTrainingDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
    }

    @Override
    public void deleteFreqTrainingByDate(String owner, String petName, String petDate) {
        DocumentReference freqTrainingRef = getFreqTrainingsRef(owner, petName).document(petDate);
        freqTrainingRef.delete();
    }

    @Override
    public FreqTrainingEntity getFreqTrainingByDate(String owner, String petName, String petDate)
        throws DatabaseAccessException {
        CollectionReference freqTrainingsRef = getFreqTrainingsRef(owner, petName);
        DocumentReference freqTrainingRef = freqTrainingsRef.document(petDate);
        ApiFuture<DocumentSnapshot> future = freqTrainingRef.get();
        DocumentSnapshot mealDoc;
        try {
            mealDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!mealDoc.exists()) {
            throw new DatabaseAccessException(INVALID_FREQTRAINING_EXC, FREQTRAINING_DOES_NOT_EXIST_EXC);
        }
        return mealDoc.toObject(FreqTrainingEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllFreqTraining(String owner, String petName) throws DatabaseAccessException {
        CollectionReference freqTrainingsRef = getFreqTrainingsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getAllFreqTrainingsOfAPetFromDatabase(freqTrainingsRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public List<Map<String, Object>> getAllFreqTrainingsBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        CollectionReference freqTrainingsRef = getFreqTrainingsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getFreqTrainingsBetweenDatesFromDatabase(initialDate, finalDate, freqTrainingsRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public void updateFreqTraining(String owner, String petName, String petDate, Object value) {
        CollectionReference freqTrainingsRef = getFreqTrainingsRef(owner, petName);
        DocumentReference freqTrainingRef = freqTrainingsRef.document(petDate);
        freqTrainingRef.update("freqTrainingValue", value);
    }

    /**
     * Return the freqTraining collection of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return Return the freqTraining collection of one pet
     */
    public CollectionReference getFreqTrainingsRef(String owner, String petName) {
        return db.collection("users").document(owner).collection("pets").document(petName)
            .collection("freqTrainings");
    }

    /**
     * Gets all the freqTrainings of the collection and puts them in the externalList.
     * @param freqTrainingsRef Reference to the collection of freqTrainings
     * @param externalList list that will contain all the freqTrainings
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getAllFreqTrainingsOfAPetFromDatabase(CollectionReference freqTrainingsRef,
                                                       List<Map<String, Object>> externalList)
        throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = freqTrainingsRef.get();
        List<QueryDocumentSnapshot> freqTrainingDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot freqTrainingDocument : freqTrainingDocuments) {
            Map<String, Object> internalList = new HashMap<>();
            internalList.put(INTERNAL_LIST_STRING_1, freqTrainingDocument.getId());
            internalList.put(INTERNAL_LIST_STRING_2, freqTrainingDocument.toObject(FreqTrainingEntity.class));
            externalList.add(internalList);
        }
    }

    /**
     * Gets all the freqTrainings of the collection between the initial and final dates without taking them into
     * account and puts them in the externalList.
     * @param initialDate Initial date
     * @param finalDate Final date
     * @param freqTrainingsRef Reference to the collection of freqTrainings
     * @param externalList list that will contain all the freqTrainings
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getFreqTrainingsBetweenDatesFromDatabase(String initialDate, String finalDate,
                                                    CollectionReference freqTrainingsRef, List<Map<String,
        Object>> externalList) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = freqTrainingsRef.get();
        List<QueryDocumentSnapshot> freqTrainingDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot freqTrainingDocument : freqTrainingDocuments) {
            String date = freqTrainingDocument.getId();
            if (initialDate.compareTo(date) < 0 && finalDate.compareTo(date) > 0) {
                Map<String, Object> internalList = new HashMap<>();
                internalList.put(INTERNAL_LIST_STRING_1, date);
                internalList.put(INTERNAL_LIST_STRING_2, freqTrainingDocument.toObject(FreqTrainingEntity.class));
                externalList.add(internalList);
            }
        }
    }
}
