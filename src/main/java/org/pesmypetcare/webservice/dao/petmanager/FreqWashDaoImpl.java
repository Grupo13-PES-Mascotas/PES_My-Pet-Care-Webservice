package org.pesmypetcare.webservice.dao.petmanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.pesmypetcare.webservice.entity.petmanager.FreqWashEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.thirdpartyservices.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Oriol Catalán
 */
@Repository
public class FreqWashDaoImpl implements FreqWashDao {
    private static final String DELFAIL_KEY = "deletion-failed";
    private static final String FREQWASH_DOES_NOT_EXIST_EXC = "The freqWash does not exist";
    private static final String INVALID_FREQWASH_EXC = "invalid-pet";
    private static final String INTERNAL_LIST_STRING_1 = "date";
    private static final String INTERNAL_LIST_STRING_2 = "body";


    private Firestore db;


    public FreqWashDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public void createFreqWash(String owner, String petName, String date, FreqWashEntity freqWashEntity) {
        DocumentReference freqWashRef = getFreqWashesRef(owner, petName).document(date);
        freqWashRef.set(freqWashEntity);
    }

    @Override
    public void deleteAllFreqWashes(String owner, String petName) throws DatabaseAccessException {
        CollectionReference freqWashesRef = getFreqWashesRef(owner, petName);
        try {
            ApiFuture<QuerySnapshot> future = freqWashesRef.get();
            List<QueryDocumentSnapshot> freqWashDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot freqWashDocument : freqWashDocuments) {
                freqWashDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
    }

    @Override
    public void deleteFreqWashByDate(String owner, String petName, String petDate) {
        DocumentReference freqWashRef = getFreqWashesRef(owner, petName).document(petDate);
        freqWashRef.delete();
    }

    @Override
    public FreqWashEntity getFreqWashByDate(String owner, String petName, String petDate)
        throws DatabaseAccessException {
        CollectionReference freqWashesRef = getFreqWashesRef(owner, petName);
        DocumentReference freqWashRef = freqWashesRef.document(petDate);
        ApiFuture<DocumentSnapshot> future = freqWashRef.get();
        DocumentSnapshot mealDoc;
        try {
            mealDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!mealDoc.exists()) {
            throw new DatabaseAccessException(INVALID_FREQWASH_EXC, FREQWASH_DOES_NOT_EXIST_EXC);
        }
        return mealDoc.toObject(FreqWashEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllFreqWashes(String owner, String petName) throws DatabaseAccessException {
        CollectionReference freqWashesRef = getFreqWashesRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getAllFreqWashesOfAPetFromDatabase(freqWashesRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public List<Map<String, Object>> getAllFreqWashesBetween(String owner, String petName, String initialDate,
                                                             String finalDate) throws DatabaseAccessException {
        CollectionReference freqWashesRef = getFreqWashesRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getFreqWashesBetweenDatesFromDatabase(initialDate, finalDate, freqWashesRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public void updateFreqWash(String owner, String petName, String petDate, Object value) {
        CollectionReference freqWashesRef = getFreqWashesRef(owner, petName);
        DocumentReference freqWashRef = freqWashesRef.document(petDate);
        freqWashRef.update("freqWashValue", value);
    }

    /**
     * Return the freqWash collection of one pet.
     *
     * @param owner Username of the owner of the pet
     * @param petName Name of the pet
     * @return Return the freqWash collection of one pet
     */
    public CollectionReference getFreqWashesRef(String owner, String petName) {
        return db.collection("users").document(owner).collection("pets").document(petName).collection("freqWashes");
    }

    /**
     * Gets all the freqWashes of the collection and puts them in the externalList.
     *
     * @param freqWashesRef Reference to the collection of freqWashes
     * @param externalList list that will contain all the freqWashes
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getAllFreqWashesOfAPetFromDatabase(CollectionReference freqWashesRef,
                                                    List<Map<String, Object>> externalList)
        throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = freqWashesRef.get();
        List<QueryDocumentSnapshot> freqWashDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot freqWashDocument : freqWashDocuments) {
            Map<String, Object> internalList = new HashMap<>();
            internalList.put(INTERNAL_LIST_STRING_1, freqWashDocument.getId());
            internalList.put(INTERNAL_LIST_STRING_2, freqWashDocument.toObject(FreqWashEntity.class));
            externalList.add(internalList);
        }
    }

    /**
     * Gets the freqWashes of the collection between the initial and final dates without taking them into
     * account and
     * puts them in the externalList.
     *
     * @param initialDate Initial date
     * @param finalDate Final date
     * @param freqWashesRef Reference to the collection of freqWashes
     * @param externalList list that will contain all the freqWashes
     * @throws InterruptedException Exception thrown by the DB if the operation is interrupted
     * @throws ExecutionException Exception thrown by the DB if there's an execution problem
     */
    private void getFreqWashesBetweenDatesFromDatabase(String initialDate, String finalDate,
                                                       CollectionReference freqWashesRef,
                                                       List<Map<String, Object>> externalList)
        throws InterruptedException, ExecutionException {
        ApiFuture<QuerySnapshot> future = freqWashesRef.get();
        List<QueryDocumentSnapshot> freqWashDocuments = future.get().getDocuments();
        for (QueryDocumentSnapshot freqWashDocument : freqWashDocuments) {
            String date = freqWashDocument.getId();
            if (initialDate.compareTo(date) < 0 && finalDate.compareTo(date) > 0) {
                Map<String, Object> internalList = new HashMap<>();
                internalList.put(INTERNAL_LIST_STRING_1, date);
                internalList.put(INTERNAL_LIST_STRING_2, freqWashDocument.toObject(FreqWashEntity.class));
                externalList.add(internalList);
            }
        }
    }
}
