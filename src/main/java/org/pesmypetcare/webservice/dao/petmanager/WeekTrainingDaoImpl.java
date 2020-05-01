package org.pesmypetcare.webservice.dao.petmanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.pesmypetcare.webservice.entity.petmanager.WeekTrainingEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class WeekTrainingDaoImpl implements WeekTrainingDao {
    private static final String DELFAIL_KEY = "deletion-failed";
    private static final String WEEKTRAINING_DOES_NOT_EXIST_EXC = "The weekTraining does not exist";
    private static final String INVALID_WEEKTRAINING_EXC = "invalid-pet";
    private static final String INTERNAL_LIST_STRING_1 = "date";
    private static final String INTERNAL_LIST_STRING_2 = "body";


    private Firestore db;


    public WeekTrainingDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public void createWeekTraining(String owner, String petName, String date, WeekTrainingEntity weekTrainingEntity) {
        DocumentReference weekTrainingRef = getWeekTrainingsRef(owner, petName).document(date);
        weekTrainingRef.set(weekTrainingEntity);
    }

    @Override
    public void deleteAllWeekTrainings(String owner, String petName) throws DatabaseAccessException {
        CollectionReference weekTrainingsRef = getWeekTrainingsRef(owner, petName);
        try {
            ApiFuture<QuerySnapshot> future = weekTrainingsRef.get();
            List<QueryDocumentSnapshot> weekTrainingDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot weekTrainingDocument : weekTrainingDocuments) {
                weekTrainingDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
    }

    @Override
    public void deleteWeekTrainingByDate(String owner, String petName, String petDate) {
        DocumentReference weekTrainingRef = getWeekTrainingsRef(owner, petName).document(petDate);
        weekTrainingRef.delete();
    }

    @Override
    public WeekTrainingEntity getWeekTrainingByDate(String owner, String petName, String petDate)
        throws DatabaseAccessException {
        CollectionReference weekTrainingsRef = getWeekTrainingsRef(owner, petName);
        DocumentReference weekTrainingRef = weekTrainingsRef.document(petDate);
        ApiFuture<DocumentSnapshot> future = weekTrainingRef.get();
        DocumentSnapshot mealDoc;
        try {
            mealDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!mealDoc.exists()) {
            throw new DatabaseAccessException(INVALID_WEEKTRAINING_EXC, WEEKTRAINING_DOES_NOT_EXIST_EXC);
        }
        return mealDoc.toObject(WeekTrainingEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllWeekTraining(String owner, String petName) throws DatabaseAccessException {
        CollectionReference weekTrainingsRef = getWeekTrainingsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getAllWeekTrainingsOfAPetFromDatabase(weekTrainingsRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public List<Map<String, Object>> getAllWeekTrainingsBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        CollectionReference weekTrainingsRef = getWeekTrainingsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getWeekTrainingsBetweenDatesFromDatabase(initialDate, finalDate, weekTrainingsRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public void updateWeekTraining(String owner, String petName, String petDate, Object value) {
        CollectionReference weekTrainingsRef = getWeekTrainingsRef(owner, petName);
        DocumentReference weekTrainingRef = weekTrainingsRef.document(petDate);
        weekTrainingRef.update("weekTrainingValue", value);
    }

    /**
     * Return the weekTraining collection of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return Return the weekTraining collection of one pet
     */
    public CollectionReference getWeekTrainingsRef(String owner, String petName) {
        return db.collection("users").document(owner).collection("pets").document(petName)
            .collection("weekTrainings");
    }

    /**
     * Gets all the weekTrainings of the collection and puts them in the externalList.
     * @param weekTrainingsRef Reference to the collection of weekTrainings
     * @param externalList list that will contain all the weekTrainings
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getAllWeekTrainingsOfAPetFromDatabase(CollectionReference weekTrainingsRef,
                                                       List<Map<String, Object>> externalList)
        throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = weekTrainingsRef.get();
        List<QueryDocumentSnapshot> weekTrainingDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot weekTrainingDocument : weekTrainingDocuments) {
            Map<String, Object> internalList = new HashMap<>();
            internalList.put(INTERNAL_LIST_STRING_1, weekTrainingDocument.getId());
            internalList.put(INTERNAL_LIST_STRING_2, weekTrainingDocument.toObject(WeekTrainingEntity.class));
            externalList.add(internalList);
        }
    }

    /**
     * Gets all the weekTrainings of the collection between the initial and final dates without taking
     * them into account and puts them in the externalList.
     * @param initialDate Initial date
     * @param finalDate Final date
     * @param weekTrainingsRef Reference to the collection of weekTrainings
     * @param externalList list that will contain all the weekTrainings
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getWeekTrainingsBetweenDatesFromDatabase(String initialDate, String finalDate,
                                                    CollectionReference weekTrainingsRef, List<Map<String,
        Object>> externalList) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = weekTrainingsRef.get();
        List<QueryDocumentSnapshot> weekTrainingDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot weekTrainingDocument : weekTrainingDocuments) {
            String date = weekTrainingDocument.getId();
            if (initialDate.compareTo(date) < 0 && finalDate.compareTo(date) > 0) {
                Map<String, Object> internalList = new HashMap<>();
                internalList.put(INTERNAL_LIST_STRING_1, date);
                internalList.put(INTERNAL_LIST_STRING_2, weekTrainingDocument.toObject(WeekTrainingEntity.class));
                externalList.add(internalList);
            }
        }
    }
}
