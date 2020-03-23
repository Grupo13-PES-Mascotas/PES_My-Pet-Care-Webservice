package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class PetDaoImpl implements PetDao {
    private Firestore db;

    public PetDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
    }

    @Override
    public void createPet(String owner, String name, PetEntity petEntity) {
        DocumentReference petRef = db.collection("users").document(owner).collection("pets").document(name);
        petRef.set(petEntity);
    }

    @Override
    public void deleteByOwnerAndName(String owner, String name) {
        DocumentReference petRef = db.collection("users").document(owner).collection("pets").document(name);
        petRef.delete();
    }

    @Override
    public void deleteAllPets(String owner) throws DatabaseAccessException {
        CollectionReference petsRef = db.collection("users").document(owner).collection("pets");
        try {
            ApiFuture<QuerySnapshot> future = petsRef.get();
            List<QueryDocumentSnapshot> petsDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot petDocument : petsDocuments) {
                petDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("deletion-failed", e.getMessage());
        }
    }

    @Override
    public PetEntity getPetData(String owner, String name) throws DatabaseAccessException {
        DocumentReference petRef = db.collection("users").document(owner).collection("pets").document(name);
        ApiFuture<DocumentSnapshot> future = petRef.get();
        DocumentSnapshot petDoc;
        try {
            petDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("deletion-failed", e.getMessage());
        }
        if (!petDoc.exists()) {
            throw new DatabaseAccessException("invalid-user", "The user does not exist");
        }
        return petDoc.toObject(PetEntity.class);
    }

    @Override
    public List<PetEntity> getAllPetsData(String owner) throws DatabaseAccessException {
        CollectionReference petsRef = db.collection("users").document(owner).collection("pets");
        List<PetEntity> petEntities = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = petsRef.get();
            List<QueryDocumentSnapshot> petsDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot petDocument : petsDocuments) {
                petEntities.add(petDocument.toObject(PetEntity.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("deletion-failed", e.getMessage());
        }
        return petEntities;
    }

    @Override
    public void updateField(String owner, String name, String field, Object value) {
        DocumentReference petRef = db.collection("users").document(owner).collection("pets").document(name);
        petRef.update(field,value);
    }
}
