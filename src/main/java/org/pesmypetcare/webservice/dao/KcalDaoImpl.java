package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.pesmypetcare.webservice.entity.KcalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class KcalDaoImpl implements KcalDao {
    private static final String DELFAIL_KEY = "deletion-failed";
    private static final String KCAL_DOES_NOT_EXIST_EXC = "The kcal does not exist";
    private static final String INVALID_KCAL_EXC = "invalid-pet";
    private static final String INTERNAL_LIST_STRING_1 = "date";
    private static final String INTERNAL_LIST_STRING_2 = "body";


    private Firestore db;


    public KcalDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public void createKcal(String owner, String petName, String date, KcalEntity kcalEntity) {
        DocumentReference kcalRef = getKcalsRef(owner, petName).document(date);
        kcalRef.set(kcalEntity);
    }

    @Override
    public void deleteAllKcals(String owner, String petName) throws DatabaseAccessException {
        CollectionReference kcalsRef = getKcalsRef(owner, petName);
        try {
            ApiFuture<QuerySnapshot> future = kcalsRef.get();
            List<QueryDocumentSnapshot> kcalDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot kcalDocument : kcalDocuments) {
                kcalDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
    }

    @Override
    public void deleteKcalByDate(String owner, String petName, String petDate) {
        DocumentReference kcalRef = getKcalsRef(owner, petName).document(petDate);
        kcalRef.delete();
    }

    @Override
    public KcalEntity getKcalByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        CollectionReference kcalsRef = getKcalsRef(owner, petName);
        DocumentReference kcalRef = kcalsRef.document(petDate);
        ApiFuture<DocumentSnapshot> future = kcalRef.get();
        DocumentSnapshot mealDoc;
        try {
            mealDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!mealDoc.exists()) {
            throw new DatabaseAccessException(INVALID_KCAL_EXC, KCAL_DOES_NOT_EXIST_EXC);
        }
        return mealDoc.toObject(KcalEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllKcal(String owner, String petName) throws DatabaseAccessException {
        CollectionReference kcalsRef = getKcalsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getAllKcalsOfAPetFromDatabase(kcalsRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public List<Map<String, Object>> getAllKcalsBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        CollectionReference kcalsRef = getKcalsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getKcalsBetweenDatesFromDatabase(initialDate, finalDate, kcalsRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public void updateKcal(String owner, String petName, String petDate, Object value) {
        CollectionReference kcalsRef = getKcalsRef(owner, petName);
        DocumentReference kcalRef = kcalsRef.document(petDate);
        kcalRef.update("kcalValue", value);
    }

    /**
     * Return the kcal collection of one pet.
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return Return the kcal collection of one pet
     */
    public CollectionReference getKcalsRef(String owner, String petName) {
        return db.collection("users").document(owner).collection("pets").document(petName)
            .collection("kcals");
    }

    /**
     * Gets all the kcals of the collection and puts them in the externalList.
     * @param kcalsRef Reference to the collection of kcals
     * @param externalList list that will contain all the kcals
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getAllKcalsOfAPetFromDatabase(CollectionReference kcalsRef, List<Map<String, Object>> externalList)
        throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = kcalsRef.get();
        List<QueryDocumentSnapshot> kcalDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot kcalDocument : kcalDocuments) {
            Map<String, Object> internalList = new HashMap<>();
            internalList.put(INTERNAL_LIST_STRING_1, kcalDocument.getId());
            internalList.put(INTERNAL_LIST_STRING_2, kcalDocument.toObject(KcalEntity.class));
            externalList.add(internalList);
        }
    }

    /**
     * Gets all the kcals of the collection between the initial and final dates without taking them into account and
     * puts them in the externalList.
     * @param initialDate Initial date
     * @param finalDate Final date
     * @param kcalsRef Reference to the collection of kcals
     * @param externalList list that will contain all the kcals
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getKcalsBetweenDatesFromDatabase(String initialDate, String finalDate,
                                                    CollectionReference kcalsRef, List<Map<String,
        Object>> externalList) throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = kcalsRef.get();
        List<QueryDocumentSnapshot> kcalDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot kcalDocument : kcalDocuments) {
            String date = kcalDocument.getId();
            if (initialDate.compareTo(date) < 0 && finalDate.compareTo(date) > 0) {
                Map<String, Object> internalList = new HashMap<>();
                internalList.put(INTERNAL_LIST_STRING_1, date);
                internalList.put(INTERNAL_LIST_STRING_2, kcalDocument.toObject(KcalEntity.class));
                externalList.add(internalList);
            }
        }
    }
}
