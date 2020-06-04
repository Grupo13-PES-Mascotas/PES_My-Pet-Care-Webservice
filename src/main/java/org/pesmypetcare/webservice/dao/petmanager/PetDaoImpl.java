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
     *
     * @return The storage dao
     */
    public StorageDao getStorageDao() {
        return storageDao;
    }

    @Override
    public void createPet(String ownerId, String name, PetEntity petEntity)
        throws DatabaseAccessException, DocumentException {
        initializeWithCollectionPath(ownerId);
        dbDoc.createDocumentWithId(path, name, petEntity, batch);
        dbDoc.commitBatch(batch);
    }

    @Override
    public void deleteByOwnerAndName(String ownerId, String name) throws DatabaseAccessException, DocumentException {
        initializeWithDocumentPath(ownerId, name);
        String imageLocation = dbDoc.getStringFromDocument(path, "profileImageLocation");
        dbDoc.deleteDocument(path, batch);
        dbDoc.commitBatch(batch);
        deleteProfileImage(imageLocation);
    }

    @Override
    public void deleteAllPets(String ownerId) throws DatabaseAccessException, DocumentException {
        initializeWithCollectionPath(ownerId);
        List<DocumentSnapshot> petsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);
        dbCol.deleteCollection(path, batch);
        dbDoc.commitBatch(batch);
        for (DocumentSnapshot petDocument : petsDocuments) {
            String imageLocation = petDocument.getString("profileImageLocation");
            deleteProfileImage(imageLocation);
        }
    }

    @Override
    public PetEntity getPetData(String ownerId, String name) throws DatabaseAccessException, DocumentException {
        initializeWithDocumentPath(ownerId, name);
        return dbDoc.getDocumentDataAsObject(path, PetEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllPetsData(String ownerId) throws DatabaseAccessException {
        initializeWithCollectionPath(ownerId);
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
    public Object getSimpleField(String ownerId, String name, String field)
        throws DatabaseAccessException, DocumentException {
        String petPath = Path.ofDocument(Collections.pets, ownerId, name);
        return dbDoc.getDocumentField(petPath, field);
    }

    @Override
    public void updateSimpleField(String ownerId, String name, String field, Object value)
        throws DatabaseAccessException, DocumentException {
        initializeWithDocumentPath(ownerId, name);
        dbDoc.updateDocumentFields(batch, path, field, value);
        dbDoc.commitBatch(batch);
    }

    @Override
    public void deleteFieldCollection(String ownerId, String name, String field)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithCollectionPath(ownerId, name, field);
        dbCol.deleteCollection(path, batch);
        dbDoc.commitBatch(batch);
    }

    @Override
    public void deleteFieldCollectionElementsPreviousToKey(String ownerId, String name, String field, String key)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithCollectionPath(ownerId, name, field);
        List<DocumentSnapshot> fieldsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);

        for (DocumentSnapshot fieldDocument : fieldsDocuments) {
            String documentKey = fieldDocument.getId();
            if (documentKey.compareTo(key) < 0) {
                batch.delete(fieldDocument.getReference());
            }
        }
        dbDoc.commitBatch(batch);
    }

    @Override
    public List<Map<String, Object>> getFieldCollection(String ownerId, String name, String field)
        throws DatabaseAccessException {
        initializeFieldWithCollectionPath(ownerId, name, field);
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
    public List<Map<String, Object>> getFieldCollectionElementsBetweenKeys(String ownerId, String name, String field,
                                                                           String key1, String key2)
        throws DatabaseAccessException {
        initializeFieldWithCollectionPath(ownerId, name, field);
        List<DocumentSnapshot> fieldsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);
        List<Map<String, Object>> externalList = new ArrayList<>();

        for (DocumentSnapshot fieldDocument : fieldsDocuments) {
            String key = fieldDocument.getId();
            if (key1.compareTo(key) <= 0 && key2.compareTo(key) >= 0) {
                Map<String, Object> internalList = new HashMap<>();
                internalList.put("key", fieldDocument.getId());
                internalList.put("body", fieldDocument.getData());
                externalList.add(internalList);
            }
        }
        return externalList;
    }

    @Override
    public void addFieldCollectionElement(String ownerId, String name, String field, String key,
                                          Map<String, Object> body) throws DatabaseAccessException, DocumentException {
        initializeFieldWithCollectionPath(ownerId, name, field);
        dbDoc.createDocumentWithId(path, key, body, batch);
        dbDoc.commitBatch(batch);
    }

    @Override
    public void deleteFieldCollectionElement(String ownerId, String name, String field, String key)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithDocumentPath(ownerId, name, field, key);
        dbDoc.deleteDocument(path, batch);
        dbDoc.commitBatch(batch);
    }

    @Override
    public void updateFieldCollectionElement(String ownerId, String name, String field, String key,
                                             Map<String, Object> body)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithDocumentPath(ownerId, name, field, key);
        dbDoc.updateDocumentFields(path, body, batch);
        dbDoc.commitBatch(batch);
    }

    @Override
    public Map<String, Object> getFieldCollectionElement(String ownerId, String name, String field, String key)
        throws DatabaseAccessException, DocumentException {
        initializeFieldWithDocumentPath(ownerId, name, field, key);
        return dbDoc.getDocumentData(path);
    }

    /**
     * Deletes the pet profile image.
     *
     * @param imageLocation The image location
     */
    private void deleteProfileImage(String imageLocation) {
        if (imageLocation != null) {
            storageDao.deleteImageByName(imageLocation);
        }
    }

    /**
     * Initializes the ownerId, batch and path variables for the access, the path is set to the pet document.
     *
     * @param ownerId UID of the owner of the pets
     * @param petName Pet name
     */
    private void initializeWithDocumentPath(String ownerId, String petName) {
        batch = dbCol.batch();
        path = Path.ofDocument(Collections.pets, ownerId, petName);
    }

    /**
     * Initializes the ownerId, batch and path variables for the access, the path is set to the user's pet collection.
     *
     * @param ownerId UID of the owner of the pets
     */
    private void initializeWithCollectionPath(String ownerId) {
        batch = dbCol.batch();
        path = Path.ofCollection(Collections.pets, ownerId);
    }

    /**
     * Initializes the collection, ownerId, batch and path variables for the access, the path is set to the user's
     * pet field document identified by key.
     *
     * @param ownerId UID of the owner of the pets
     * @param petName Pet name
     * @param collectionName Name of the collection
     * @param key Id of the document we access
     */
    private void initializeFieldWithDocumentPath(String ownerId, String petName, String collectionName, String key) {
        Collections collection = Path.collectionOfField(collectionName);
        batch = dbCol.batch();
        path = Path.ofDocument(collection, ownerId, petName, key);
    }

    /**
     * Initializes the collection, ownerId, batch and path variables for the access, the path is set to the user's
     * pet field collection.
     *
     * @param ownerId UID of the owner of the pets
     * @param petName Pet name
     * @param collectionName Name of the collection
     */
    private void initializeFieldWithCollectionPath(String ownerId, String petName, String collectionName) {
        Collections collection = Path.collectionOfField(collectionName);
        batch = dbCol.batch();
        path = Path.ofCollection(collection, ownerId, petName);
    }
}
