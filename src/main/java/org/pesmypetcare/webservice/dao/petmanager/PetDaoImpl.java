package org.pesmypetcare.webservice.dao.petmanager;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.dao.appmanager.StorageDaoImpl;
import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
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

/**
 * @author Marc Simó
 */
@Repository
public class PetDaoImpl implements PetDao {
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
        initializeWithCollectionPath(owner);
        dbDoc.createDocumentWithId(path, name, petEntity, batch);
        batch.commit();
    }

    @Override
    public void deleteByOwnerAndName(String owner, String name) throws DatabaseAccessException, DocumentException {
        initializeWithDocumentPath(owner, name);
        String imageLocation = dbDoc.getStringFromDocument(path, "profileImageLocation");
        dbDoc.deleteDocument(path, batch);
        batch.commit();
        deleteProfileImage(imageLocation);
    }

    @Override
    public void deleteAllPets(String owner) throws DatabaseAccessException, DocumentException {
        initializeWithCollectionPath(owner);
        List<DocumentSnapshot> petsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);
        dbCol.deleteCollection(path, batch);
        batch.commit();
        for (DocumentSnapshot petDocument : petsDocuments) {
            String imageLocation = petDocument.getString("profileImageLocation");
            deleteProfileImage(imageLocation);
        }
    }

    @Override
    public PetEntity getPetData(String owner, String name) throws DatabaseAccessException, DocumentException {
        initializeWithDocumentPath(owner, name);
        return dbDoc.getDocumentDataAsObject(path, PetEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllPetsData(String owner) throws DatabaseAccessException, DocumentException {
        initializeWithCollectionPath(owner);
        List<DocumentSnapshot> petsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);
        List<Map<String, Object>> externalList = new ArrayList<>();

        for (DocumentSnapshot petDocument : petsDocuments) {
            Map<String, Object> internalList = new HashMap<>();
            internalList.put("name", petDocument.getId());
            internalList.put("body", petDocument.toObject(PetEntity.class));
            externalList.add(internalList);
        }
        return externalList;
    }

    @Override
    public Object getSimpleField(String owner, String name, String field)
        throws DatabaseAccessException, DocumentException {
        String petPath = Path.ofDocument(Collections.pets, getUserId(owner), name);
        return dbDoc.getDocumentField(petPath, field);
    }

    @Override
    public void updateSimpleField(String owner, String name, String field, Object value)
        throws DatabaseAccessException, DocumentException {
        initializeWithDocumentPath(owner, name);
        dbDoc.updateDocumentFields(batch, path, field, value);
        batch.commit();
    }

    @Override
    public void deleteFieldCollection(String owner, String name, String field)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithCollectionPath(owner, name, field);
        dbCol.deleteCollection(path, batch);
        batch.commit();
    }

    @Override
    public List<Map<String, Object>> getFieldCollection(String owner, String name, String field)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithCollectionPath(owner, name, field);
        List<DocumentSnapshot> fieldsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);
        List<Map<String, Object>> externalList = new ArrayList<>();

        for (DocumentSnapshot fieldDocument : fieldsDocuments) {
            Map<String, Object> internalList = new HashMap<>();
            internalList.put("key", fieldDocument.getId());
            internalList.put("body", fieldDocument.getData());
            externalList.add(internalList);
        }
        return externalList;
    }

    @Override
    public List<Map<String, Object>> getFieldCollectionElementsBetweenKeys(String owner, String name, String field,
                                                                           String key1, String key2)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithCollectionPath(owner, name, field);
        List<DocumentSnapshot> fieldsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);
        List<Map<String, Object>> externalList = new ArrayList<>();

        Map<String, Object> internalList = new HashMap<>();
        for (DocumentSnapshot fieldDocument : fieldsDocuments) {
            String key = fieldDocument.getId();
            if (key1.compareTo(key) <= 0 && key2.compareTo(key) >= 0) {
                internalList.put("key", fieldDocument.getId());
                internalList.put("body", fieldDocument.getData());
                externalList.add(internalList);
            }
        }
        return externalList;
    }

    @Override
    public void addFieldCollectionElement(String owner, String name, String field, String key, Map<String, Object> body)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithCollectionPath(owner, name, field);
        dbDoc.createDocumentWithId(path, key, body, batch);
        batch.commit();
    }

    @Override
    public void deleteFieldCollectionElement(String owner, String name, String field, String key)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithDocumentPath(owner, name, field, key);
        dbDoc.deleteDocument(path, batch);
        batch.commit();
    }

    @Override
    public void updateFieldCollectionElement(String owner, String name, String field, String key,
                                             Map<String, Object> body)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithDocumentPath(owner, name, field, key);
        dbDoc.updateDocumentFields(path, body, batch);
        batch.commit();
    }

    @Override
    public Map<String, Object> getFieldCollectionElement(String owner, String name, String field, String key)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithDocumentPath(owner, name, field, key);
        return dbDoc.getDocumentData(path);
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
     * Initializes the collection, ownerId, batch and path variables for the access, the path is set to the user's
     * pet field document identified by key
     * @param owner Owner of the pet
     * @param petName Pet name
     * @param collectionName Name of the collection
     * @param key Id of the document we access
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private void initializeFieldWithDocumentPath(String owner, String petName, String collectionName, String key)
        throws DatabaseAccessException, DocumentException {
        Collections collection = Path.collectionOfField(collectionName);
        ownerId = getUserId(owner);
        batch = dbCol.batch();
        path = Path.ofDocument(collection, ownerId, petName, key);
    }

    /**
     * Initializes the collection, ownerId, batch and path variables for the access, the path is set to the user's
     * pet field collection
     * @param owner Owner of the pet
     * @param petName Pet name
     * @param collectionName Name of the collection
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private void initializeFieldWithCollectionPath(String owner, String petName, String collectionName)
        throws DatabaseAccessException, DocumentException {
        Collections collection = Path.collectionOfField(collectionName);
        ownerId = getUserId(owner);
        batch = dbCol.batch();
        path = Path.ofCollection(collection, ownerId, petName);
    }

    /**
     * Returns the id of a user specifying its username.
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
