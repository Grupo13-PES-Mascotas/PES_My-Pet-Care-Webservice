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
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class PetDaoImpl implements PetDao {
    private final String USER_KEY;
    private final String PETS_KEY;
    private final String DELFAIL_KEY;
    private Firestore db;

    public PetDaoImpl() {
        db = FirebaseFactory.getInstance().getFirestore();
        USER_KEY = "users";
        PETS_KEY = "pets";
        DELFAIL_KEY = "deletion-failed";
    }

    @Override
    public void createPet(String owner, String name, PetEntity petEntity) {
        DocumentReference petRef = db.collection(USER_KEY).document(owner).collection(PETS_KEY).document(name);
        petRef.set(petEntity);
    }

    @Override
    public void deleteByOwnerAndName(String owner, String name) {
        DocumentReference petRef = db.collection(USER_KEY).document(owner).collection(PETS_KEY).document(name);
        petRef.delete();
    }

    @Override
    public void deleteAllPets(String owner) throws DatabaseAccessException {
        CollectionReference petsRef = db.collection(USER_KEY).document(owner).collection(PETS_KEY);
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
        DocumentReference petRef = db.collection(USER_KEY).document(owner).collection(PETS_KEY).document(name);
        ApiFuture<DocumentSnapshot> future = petRef.get();
        DocumentSnapshot petDoc;
        try {
            petDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!petDoc.exists()) {
            throw new DatabaseAccessException("invalid-user", "The user does not exist");
        }
        return petDoc.toObject(PetEntity.class);
    }

    @Override
    public List<PetEntity> getAllPetsData(String owner) throws DatabaseAccessException {
        CollectionReference petsRef = db.collection(USER_KEY).document(owner).collection(PETS_KEY);
        List<PetEntity> petEntities = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = petsRef.get();
            List<QueryDocumentSnapshot> petsDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot petDocument : petsDocuments) {
                petEntities.add(petDocument.toObject(PetEntity.class));
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return petEntities;
    }

    @Override
    public void updateField(String owner, String name, String field, Object value) {
        DocumentReference petRef = db.collection(USER_KEY).document(owner).collection(PETS_KEY).document(name);
        petRef.update(field, value);
    }
}
