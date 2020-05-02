package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;
import org.springframework.beans.factory.annotation.Autowired;
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
    private String ownerId;
    private WriteBatch batch;
    private String path;

    @Autowired
    private FirestoreCollection dbCol;
    @Autowired
    private FirestoreDocument dbDoc;

    private StorageDao storageDao;

    public PetDaoImpl() {
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
    public void createPet(String owner, String name, PetEntity petEntity) throws DatabaseAccessException,
        DocumentException {
        initializeWithDocumentPath(owner, name);
        dbDoc.createDocument(path, petEntity, batch);
        batch.commit();
    }

    @Override
    public void deleteByOwnerAndName(String owner, String name) throws DatabaseAccessException, DocumentException {
        initializeWithDocumentPath(owner, name);
        String imageLocation = dbDoc.getStringFromDocument(path, "profileImageLocation");
        deleteProfileImage(imageLocation);
        dbDoc.deleteDocument(path, batch);
        batch.commit();
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
    public Object getSimpleField(String owner, String name, String field) throws DatabaseAccessException {
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
    public void updateSimpleField(String owner, String name, String field, Object value) {
        DocumentReference petRef = usersRef.document(owner).collection(PETS_KEY).document(name);
        petRef.update(field, value);
    }

    @Override
    public void deleteMapField(String owner, String name, String field) throws DatabaseAccessException {

    }

    @Override
    public Map<String, Object> getMapField(String owner, String name, String field) throws DatabaseAccessException {
        return null;
    }

    @Override
    public void addMapFieldElement(String owner, String name, String field, String key, Object body) throws DatabaseAccessException {

    }

    @Override
    public void deleteMapFieldElement(String owner, String name, String field, String key) throws DatabaseAccessException {

    }

    @Override
    public void updateMapFieldElement(String owner, String name, String field, String key, Object body) throws DatabaseAccessException {

    }

    @Override
    public Object getMapFieldElement(String owner, String name, String field, String key) throws DatabaseAccessException {
        return null;
    }

    @Override
    public Map<String, Object> getMapFieldElementsBetweenKeys(String owner, String name, String field, String key1, String key2) throws DatabaseAccessException {
        return null;
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

    /**
     * Initializes the ownerId, batch and path variables for the access, the path is set to the pet document
     * @param owner Owner of the pet
     * @param petName Pet name
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private void initializeWithDocumentPath(String owner, String petName) throws DatabaseAccessException,
        DocumentException {
        ownerId = getUserId(owner);
        batch = dbCol.batch();
        path = Path.ofDocument(Collections.pets, ownerId, petName);
    }

    /**
     * Initializes the ownerId, batch and path variables for the access, the path is set to the user's pet collection
     * @param owner Owner of the pets
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private void initializeWithCollectionPath(String owner) throws DatabaseAccessException,
        DocumentException {
        ownerId = getUserId(owner);
        batch = dbCol.batch();
        path = Path.ofCollection(Collections.pets, ownerId);
    }

    /**
     * Returns the id of a user specifying its username
     * @param username Name of the user
     * @return The user's id
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private String getUserId(String username) throws DatabaseAccessException, DocumentException {
        String usernamePath = Path.ofDocument(Collections.usernames, username);
        return dbDoc.getStringFromDocument(usernamePath, "user");
    }
}
