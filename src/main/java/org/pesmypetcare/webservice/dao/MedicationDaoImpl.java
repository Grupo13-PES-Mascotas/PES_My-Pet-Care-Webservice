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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MedicationDaoImpl implements MedicationDao{
    private final String DELFAIL_KEY;
    private final String MEDICATION_DOES_NOT_EXIST_EXC;
    private final String INVALID_MEDICATION_EXC;

    Firestore db;

    public MedicationDaoImpl() {
        db =  FirebaseFactory.getInstance().getFirestore();

        DELFAIL_KEY = "deletion-failed";
        MEDICATION_DOES_NOT_EXIST_EXC = "The med does not exist";
        INVALID_MEDICATION_EXC = "invalid-med";
    }

    @Override
    public void createMedication(String owner, String petName, String dateName, MedicationEntity medication) {
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
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
    }

    @Override
    public MedicationEntity getMedicationData(String owner, String petName, String dateName) throws DatabaseAccessException {
        CollectionReference medicationsRef = getMedicationsRef(owner, petName);
        DocumentReference medicationsRefDoc = medicationsRef.document(dateName);
        ApiFuture<DocumentSnapshot> future = medicationsRefDoc.get();
        DocumentSnapshot medicationDoc;
        try {
            medicationDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!medicationDoc.exists()) {
            throw new DatabaseAccessException(INVALID_MEDICATION_EXC, MEDICATION_DOES_NOT_EXIST_EXC);
        }
        return medicationDoc.toObject(MedicationEntity.class);
    }

    @Override
    public List<Map<List<String>, Object>> getAllMedicationData(String owner, String petName) throws DatabaseAccessException {
        CollectionReference medicationsRef = getMedicationsRef(owner, petName);
        List<Map<List<String>, Object>> externalList = new ArrayList<>();
        String aux;
        try {
            ApiFuture<QuerySnapshot> future = medicationsRef.get();
            List<QueryDocumentSnapshot> medicationDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot medicationDocument : medicationDocuments) {
                Map<List<String>, Object> internalList = new HashMap<>();
                List<String> pks = new ArrayList<String>();
                aux = medicationDocument.getId();
                pks.add(pkToDate(aux));
                pks.add(pkToName(aux));
                internalList.put(Collections.singletonList("date"), pks);
                internalList.put(Collections.singletonList("body"), medicationDocument.toObject(MedicationEntity.class));
                externalList.add(internalList);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public List<Map<List<String>, Object>> getAllMMedicationsBetween(String owner, String petName, String initialDate, String finalDate) throws DatabaseAccessException {
        CollectionReference medicationsRef = getMedicationsRef(owner, petName);
        List<Map<List<String>, Object>> externalList = new ArrayList<>();
        String aux;
        try {
            ApiFuture<QuerySnapshot> future = medicationsRef.get();
            List<QueryDocumentSnapshot> medicationDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot medicationDocument : medicationDocuments) {
                Map<List<String>, Object> internalList = new HashMap<>();
                List<String> pks = new ArrayList<String>();
                aux = medicationDocument.getId();
                if (initialDate.compareTo(pkToDate(aux)) < 0 && finalDate.compareTo(pkToDate(aux)) > 0){
                    pks.add(pkToDate(aux));
                    pks.add(pkToName(aux));
                    internalList.put(Collections.singletonList("date"), pks);
                    internalList.put(Collections.singletonList("body"), medicationDocument.toObject(MedicationEntity.class));
                    externalList.add(internalList);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public Object getMedicationField(String owner, String petName, String dateName, String field) throws DatabaseAccessException {
        CollectionReference medicationsRef = getMedicationsRef(owner, petName);
        DocumentReference medicationDocRef = medicationsRef.document(dateName);
        ApiFuture<DocumentSnapshot> future = medicationDocRef.get();
        DocumentSnapshot medicationDoc;
        try {
            medicationDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!medicationDoc.exists()) {
            throw new DatabaseAccessException(INVALID_MEDICATION_EXC, MEDICATION_DOES_NOT_EXIST_EXC);
        }
        return medicationDoc.get(field);
    }

    @Override
    public void updateMedicationField(String owner, String petName, String dateName, String field, Object value) {
        CollectionReference medicationsRef = getMedicationsRef(owner, petName);
        DocumentReference medRef = medicationsRef.document(dateName);
        medRef.update(field, value);
    }


    public CollectionReference getMedicationsRef(String owner, String petName) {
        return db.collection("users").document(owner).collection("pets").document(petName).collection("medications");
    }

    public String toPK(String date, String name){
        return date + "/" + name;
    }

    public String pkToDate(String pk){
        String[] parts = pk.split("/");
        return parts[0];
    }

    public String pkToName(String pk){
        String[] parts = pk.split("/");
        return parts[1];
    }
}