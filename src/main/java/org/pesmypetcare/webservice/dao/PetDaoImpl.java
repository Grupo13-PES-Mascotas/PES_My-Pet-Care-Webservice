package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class PetDaoImpl implements PetDao {
    private final String PETS_KEY;
    private final String DELFAIL_KEY;
    private final String PET_DOES_NOT_EXIST_EXC;
    private final String INVALID_PET_EXC;
    private CollectionReference usersRef;

    public PetDaoImpl() {
        Firestore db;
        db = FirebaseFactory.getInstance().getFirestore();
        usersRef = db.collection("users");

        PETS_KEY = "pets";
        DELFAIL_KEY = "deletion-failed";
        PET_DOES_NOT_EXIST_EXC = "The pet does not exist";
        INVALID_PET_EXC = "invalid-pet";
    }

    @Override
    public void createPet(String owner, String name, PetEntity petEntity) {
        DocumentReference petRef = usersRef.document(owner).collection(PETS_KEY).document(name);
        petRef.set(petEntity);
    }

    @Override
    public void deleteByOwnerAndName(String owner, String name) {
        DocumentReference petRef = usersRef.document(owner).collection(PETS_KEY).document(name);
        petRef.delete();
    }

    @Override
    public void deleteAllPets(String owner) throws DatabaseAccessException {
        CollectionReference petsRef = usersRef.document(owner).collection(PETS_KEY);
        try {
            ApiFuture<QuerySnapshot> future = petsRef.get();
            List<QueryDocumentSnapshot> petsDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot petDocument : petsDocuments) {
                petDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
    }

    @Override
    public PetEntity getPetData(String owner, String name) throws DatabaseAccessException {
        DocumentReference petRef = usersRef.document(owner).collection(PETS_KEY).document(name);
        ApiFuture<DocumentSnapshot> future = petRef.get();
        DocumentSnapshot petDoc;
        try {
            petDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!petDoc.exists()) {
            throw new DatabaseAccessException(INVALID_PET_EXC, PET_DOES_NOT_EXIST_EXC);
        }
        return petDoc.toObject(PetEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllPetsData(String owner) throws DatabaseAccessException {
        CollectionReference petsRef = usersRef.document(owner).collection(PETS_KEY);
        List<Map<String, Object>> externalList = new ArrayList<>();
        Map<String, Object> internalList = new HashMap<>();
        try {
            ApiFuture<QuerySnapshot> future = petsRef.get();
            List<QueryDocumentSnapshot> petsDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot petDocument : petsDocuments) {
                internalList.put("name", petDocument.getId());
                internalList.put("body", petDocument.toObject(PetEntity.class));
                externalList.add(internalList);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public Object getField(String owner, String name, String field) throws DatabaseAccessException {
        DocumentReference petRef = usersRef.document(owner).collection(PETS_KEY).document(name);
        ApiFuture<DocumentSnapshot> future = petRef.get();
        DocumentSnapshot petDoc;
        try {
            petDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!petDoc.exists()) {
            throw new DatabaseAccessException(INVALID_PET_EXC, PET_DOES_NOT_EXIST_EXC);
        }
        return petDoc.get(field);
    }

    @Override
    public void updateField(String owner, String name, String field, Object value) {
        DocumentReference petRef = usersRef.document(owner).collection(PETS_KEY).document(name);
        petRef.update(field, value);
    }
}
