package org.pesmypetcare.webservice.dao.petmanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.dao.appmanager.StorageDaoImpl;
import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
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
    private static final String PETS_KEY = "pets";
    private static final String DELFAIL_KEY = "deletion-failed";
    private static final String PET_DOES_NOT_EXIST_EXC = "The pet does not exist";
    private static final String INVALID_PET_EXC = "invalid-pet";
    private CollectionReference usersRef;

    private StorageDao storageDao;

    public PetDaoImpl() {
        Firestore db;
        db = FirebaseFactory.getInstance().getFirestore();
        usersRef = db.collection("users");
        storageDao = new StorageDaoImpl(this);

    }

    /**
     * Gets the storage dao.
     * @return The storage dao
     */
    public StorageDao getStorageDao() {
        return storageDao;
    }

    @Override
    public void createPet(String owner, String name, PetEntity petEntity) {
        DocumentReference petRef = usersRef.document(owner).collection(PETS_KEY).document(name);
        petRef.set(petEntity);
        System.out.println("Pet created");
    }

    @Override
    public void deleteByOwnerAndName(String owner, String name) throws DatabaseAccessException {
        DocumentReference petRef = usersRef.document(owner).collection(PETS_KEY).document(name);
        try {
            ApiFuture<DocumentSnapshot> petDoc = petRef.get();
            String imageLocation = (String) petDoc.get().get("profileImageLocation");
            deleteProfileImage(imageLocation);
            petRef.delete();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
    }

    @Override
    public void deleteAllPets(String owner) throws DatabaseAccessException {
        CollectionReference petsRef = usersRef.document(owner).collection(PETS_KEY);
        try {
            ApiFuture<QuerySnapshot> future = petsRef.get();
            List<QueryDocumentSnapshot> petsDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot petDocument : petsDocuments) {
                String imageLocation = (String) petDocument.get("profileImageLocation");
                deleteProfileImage(imageLocation);
                petDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(PET_DOES_NOT_EXIST_EXC, e.getMessage());
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
        try {
            ApiFuture<QuerySnapshot> future = petsRef.get();
            List<QueryDocumentSnapshot> petsDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot petDocument : petsDocuments) {
                Map<String, Object> internalList = new HashMap<>();
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

    /**
     * Deletes the pet profile image.
     * @param imageLocation The image location
     */
    private void deleteProfileImage(String imageLocation) {
        if (imageLocation != null) {
            storageDao.deleteImageByName(imageLocation);
        }
    }
}
