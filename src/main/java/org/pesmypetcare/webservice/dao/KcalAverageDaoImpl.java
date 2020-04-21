package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.pesmypetcare.webservice.entity.KcalAverageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class KcalAverageDaoImpl implements KcalAverageDao {
    private static final String DELFAIL_KEY = "deletion-failed";
    private static final String KCALAVERAGE_DOES_NOT_EXIST_EXC = "The kcalAverage does not exist";
    private static final String INVALID_KCALAVERAGE_EXC = "invalid-pet";
    private static final String INTERNAL_LIST_STRING_1 = "date";
    private static final String INTERNAL_LIST_STRING_2 = "body";


    private Firestore db;


    public KcalAverageDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public void createKcalAverage(String owner, String petName, String date, KcalAverageEntity kcalAverageEntity) {
        DocumentReference kcalAverageRef = getKcalAveragesRef(owner, petName).document(date);
        kcalAverageRef.set(kcalAverageEntity);
    }

    @Override
    public void deleteAllKcalAverages(String owner, String petName) throws DatabaseAccessException {
        CollectionReference kcalAveragesRef = getKcalAveragesRef(owner, petName);
        try {
            ApiFuture<QuerySnapshot> future = kcalAveragesRef.get();
            List<QueryDocumentSnapshot> kcalAverageDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot kcalAverageDocument : kcalAverageDocuments) {
                kcalAverageDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
    }

    @Override
    public void deleteKcalAverageByDate(String owner, String petName, String petDate) {
        DocumentReference kcalAverageRef = getKcalAveragesRef(owner, petName).document(petDate);
        kcalAverageRef.delete();
    }

    @Override
    public KcalAverageEntity getKcalAverageByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        CollectionReference kcalAveragesRef = getKcalAveragesRef(owner, petName);
        DocumentReference kcalAverageRef = kcalAveragesRef.document(petDate);
        ApiFuture<DocumentSnapshot> future = kcalAverageRef.get();
        DocumentSnapshot mealDoc;
        try {
            mealDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!mealDoc.exists()) {
            throw new DatabaseAccessException(INVALID_KCALAVERAGE_EXC, KCALAVERAGE_DOES_NOT_EXIST_EXC);
        }
        return mealDoc.toObject(KcalAverageEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllKcalAverage(String owner, String petName) throws DatabaseAccessException {
        CollectionReference kcalAveragesRef = getKcalAveragesRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getAllKcalAveragesOfAPetFromDatabase(kcalAveragesRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public List<Map<String, Object>> getAllKcalAveragesBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        CollectionReference kcalAveragesRef = getKcalAveragesRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getKcalAveragesBetweenDatesFromDatabase(initialDate, finalDate, kcalAveragesRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public void updateKcalAverage(String owner, String petName, String petDate, Object value) {
        CollectionReference kcalAveragesRef = getKcalAveragesRef(owner, petName);
        DocumentReference kcalAverageRef = kcalAveragesRef.document(petDate);
        kcalAverageRef.update("kcalAverageValue", value);
    }

    /**
     * Return the kcalAverage collection of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return Return the kcalAverage collection of one pet
     */
    public CollectionReference getKcalAveragesRef(String owner, String petName) {
        return db.collection("users").document(owner).collection("pets").document(petName)
            .collection("kcalAverages");
    }

    /**
     * Gets all the kcalAverages of the collection and puts them in the externalList.
     * @param kcalAveragesRef Reference to the collection of kcalAverages
     * @param externalList list that will contain all the kcalAverages
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getAllKcalAveragesOfAPetFromDatabase(CollectionReference kcalAveragesRef, List<Map<String, Object>> externalList)
        throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = kcalAveragesRef.get();
        List<QueryDocumentSnapshot> kcalAverageDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot kcalAverageDocument : kcalAverageDocuments) {
            Map<String, Object> internalList = new HashMap<>();
            internalList.put(INTERNAL_LIST_STRING_1, kcalAverageDocument.getId());
            internalList.put(INTERNAL_LIST_STRING_2, kcalAverageDocument.toObject(KcalAverageEntity.class));
            externalList.add(internalList);
        }
    }

    /**
     * Gets all the kcalAverages of the collection between the initial and final dates without taking them into account and
     * puts them in the externalList.
     * @param initialDate Initial date
     * @param finalDate Final date
     * @param kcalAveragesRef Reference to the collection of kcalAverages
     * @param externalList list that will contain all the kcalAverages
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getKcalAveragesBetweenDatesFromDatabase(String initialDate, String finalDate,
                                                    CollectionReference kcalAveragesRef, List<Map<String,
        Object>> externalList) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = kcalAveragesRef.get();
        List<QueryDocumentSnapshot> kcalAverageDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot kcalAverageDocument : kcalAverageDocuments) {
            String date = kcalAverageDocument.getId();
            if (initialDate.compareTo(date) < 0 && finalDate.compareTo(date) > 0) {
                Map<String, Object> internalList = new HashMap<>();
                internalList.put(INTERNAL_LIST_STRING_1, date);
                internalList.put(INTERNAL_LIST_STRING_2, kcalAverageDocument.toObject(KcalAverageEntity.class));
                externalList.add(internalList);
            }
        }
    }
}
