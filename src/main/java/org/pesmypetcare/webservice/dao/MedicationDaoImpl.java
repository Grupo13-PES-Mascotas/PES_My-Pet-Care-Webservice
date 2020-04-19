package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.pesmypetcare.webservice.entity.MedicationEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class MedicationDaoImpl implements MedicationDao {
    private static final String DELETION_FAILED = "deletion-failed";
    private static final String MEDICATION_DOES_NOT_EXIST_EXC = "The med does not exist";
    private static final String INVALID_MEDICATION_EXC = "invalid-med";
    private static final String DATE_NAME = "dateName";
    private static final String BODY = "body";
    private static final String SEPARATOR = "%";

    private Firestore db;

    public MedicationDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public void createMedication(String owner, String petName, String dateName,
                                 MedicationEntity medication) {
        DocumentReference medicationRef = getMedicationsRef(owner, petName).document(dateName);
        medicationRef.set(medication);
    }

    @Override
    public void deleteByDateAndName(String owner, String petName, String dateName) {
        DocumentReference medRef = getMedicationsRef(owner, petName).document(dateName);
        medRef.delete();
    }

    @Override
    public void deleteAllMedications(String owner, String petName) throws DatabaseAccessException {
        CollectionReference medicationsRef = getMedicationsRef(owner, petName);
        try {
            ApiFuture<QuerySnapshot> future = medicationsRef.get();
            List<QueryDocumentSnapshot> medicationDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot medicationDocument : medicationDocuments) {
                medicationDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELETION_FAILED, e.getMessage());
        }
    }

    @Override
    public MedicationEntity getMedicationData(String owner, String petName, String dateName)
            throws DatabaseAccessException {
        CollectionReference medicationsRef = getMedicationsRef(owner, petName);
        DocumentReference medicationsRefDoc = medicationsRef.document(dateName);
        ApiFuture<DocumentSnapshot> future = medicationsRefDoc.get();
        DocumentSnapshot medicationDoc;
        try {
            medicationDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELETION_FAILED, e.getMessage());
        }
        if (!medicationDoc.exists()) {
            throw new DatabaseAccessException(INVALID_MEDICATION_EXC, MEDICATION_DOES_NOT_EXIST_EXC);
        }
        return medicationDoc.toObject(MedicationEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllMedicationData(String owner, String petName)
            throws DatabaseAccessException {
        CollectionReference medicationsRef = getMedicationsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        ApiFuture<QuerySnapshot> future = medicationsRef.get();
        List<QueryDocumentSnapshot> medicationDocuments;
        try {
            medicationDocuments = future.get().getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELETION_FAILED, e.getMessage());
        }
        for (QueryDocumentSnapshot medicationDocument : medicationDocuments) {
            externalList.add(getEachInternalList(medicationDocument));
        }
        return externalList;
    }

    private Map<String, Object> getEachInternalList(QueryDocumentSnapshot medicationDocument) {
        Map<String, Object> internalList = new HashMap<>();
        internalList.put(DATE_NAME, medicationDocument.getId());
        internalList.put(BODY, medicationDocument.toObject(MedicationEntity.class));
        return internalList;
    }

    @Override
    public List<Map<String, Object>> getAllMedicationsBetween(String owner, String petName,
                                                                    String initialDate, String finalDate)
            throws DatabaseAccessException {
        CollectionReference medsRef = getMedicationsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            getMedicationsBetweenDatesFromDatabase(initialDate, finalDate, medsRef, externalList);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELETION_FAILED, e.getMessage());
        }
        return externalList;
    }

    private void getMedicationsBetweenDatesFromDatabase(String initialDate, String finalDate,
                                                         CollectionReference medsRef,
                                                         List<Map<String, Object>> externalList)
            throws InterruptedException, ExecutionException {
        List<QueryDocumentSnapshot> medDocuments = medsRef.get().get().getDocuments();
        for (QueryDocumentSnapshot medDocument : medDocuments) {
            if (initialDate.compareTo(pkToDate(medDocument.getId())) < 0
                    && finalDate.compareTo(pkToDate(medDocument.getId())) > 0) {
                Map<String, Object> internalList = new HashMap<>();
                internalList.put(DATE_NAME, medDocument.getId());
                internalList.put(BODY, medDocument.toObject(MedicationEntity.class));
                externalList.add(internalList);
            }
        }
    }

    @Override
    public Object getMedicationField(String owner, String petName, String dateName, String field)
            throws DatabaseAccessException {
        ApiFuture<DocumentSnapshot> future = getMedicationsRef(owner, petName).document(dateName).get();
        DocumentSnapshot medicationDoc;
        try {
            medicationDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELETION_FAILED, e.getMessage());
        }
        if (!medicationDoc.exists()) {
            throw new DatabaseAccessException(INVALID_MEDICATION_EXC,
                    MEDICATION_DOES_NOT_EXIST_EXC);
        }
        return medicationDoc.get(field);
    }

    @Override
    public void updateMedicationField(String owner, String petName, String dateName, String field,
                                      Object value) {
        CollectionReference medicationsRef = getMedicationsRef(owner, petName);
        DocumentReference medRef = medicationsRef.document(dateName);
        medRef.update(field, value);
    }


    public CollectionReference getMedicationsRef(String owner, String petName) {
        return db.collection("users").document(owner).collection("pets").document(petName).collection("medications");
    }

    public String pkToDate(String pk) {
        String[] parts = pk.split(SEPARATOR);
        return parts[0];
    }

}
