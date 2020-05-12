package org.pesmypetcare.webservice.dao;


import org.pesmypetcare.webservice.entity.MedalEntity;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
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
 * @author Oriol Catalán
 */
@Repository
public class MedalDaoImpl implements MedalDao {
    private String ownerId;
    private WriteBatch batch;
    private String path;

    @Autowired
    private FirestoreCollection dbCol;
    @Autowired
    private FirestoreDocument dbDoc;

    private StorageDao storageDao;

    public MedalDaoImpl() {
    }
    
    @Override
    public void createMedal(String owner, String name, MedalEntity medalEntity) throws DatabaseAccessException, 
        DocumentException {
        initializeWithCollectionPath(owner);
        dbDoc.createDocumentWithId(path, name, medalEntity, batch);
        batch.commit();
        
    }

    @Override
    public MedalEntity getMedalData(String owner, String name) throws DatabaseAccessException, DocumentException {
        initializeWithDocumentPath(owner, name);
        return dbDoc.getDocumentDataAsObject(path, MedalEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllMedalsData(String owner) throws DatabaseAccessException, DocumentException {
        initializeWithCollectionPath(owner);
        List<DocumentSnapshot> medalsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);
        List<Map<String, Object>> externalList = new ArrayList<>();

        for (DocumentSnapshot medalDocument : medalsDocuments) {
            Map<String, Object> internalList = new HashMap<>();
            internalList.put("name", medalDocument.getId());
            internalList.put("body", medalDocument.toObject(MedalEntity.class));
            externalList.add(internalList);
        }
        return externalList;
    }

    @Override
    public Object getSimpleField(String owner, String name, String field) throws DatabaseAccessException, DocumentException {
        String medalPath = Path.ofDocument(Collections.pets, getUserId(owner), name);
        return dbDoc.getDocumentField(medalPath, field);    }

    @Override
    public List<Map<String, Object>> getFieldCollection(String owner, String name, String field) throws DatabaseAccessException, DocumentException {
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
    public Map<String, Object> getFieldCollectionElement(String owner, String name, String field, String key) throws DatabaseAccessException, DocumentException {
        initializeFieldWithDocumentPath(owner, name, field, key);
        return dbDoc.getDocumentData(path);
    }

    /**
     * Initializes the ownerId, batch and path variables for the access, the path is set to the medal document
     * @param owner Owner of the medal
     * @param medalName Medal name
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private void initializeWithDocumentPath(String owner, String medalName) throws DatabaseAccessException,
        DocumentException {
        ownerId = getUserId(owner);
        batch = dbCol.batch();
        path = Path.ofDocument(Collections.medals, ownerId, medalName);
    }

    /**
     * Initializes the ownerId, batch and path variables for the access, the path is set to the user's medal collection
     * @param owner Owner of the medals
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private void initializeWithCollectionPath(String owner) throws DatabaseAccessException,
        DocumentException {
        ownerId = getUserId(owner);
        batch = dbCol.batch();
        path = Path.ofCollection(Collections.medals, ownerId);
    }

    /**
     * Initializes the collection, ownerId, batch and path variables for the access, the path is set to the user's
     * medal field document identified by key
     * @param owner Owner of the medal
     * @param medalName Medal name
     * @param collectionName Name of the collection
     * @param key Id of the document we access
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private void initializeFieldWithDocumentPath(String owner, String medalName, String collectionName, String key)
        throws DatabaseAccessException, DocumentException {
        Collections collection = Path.collectionOfField(collectionName);
        ownerId = getUserId(owner);
        batch = dbCol.batch();
        path = Path.ofDocument(collection, ownerId, medalName, key);
    }

    /**
     * Initializes the collection, ownerId, batch and path variables for the access, the path is set to the user's
     * medal field collection
     * @param owner Owner of the medal
     * @param medalName Medal name
     * @param collectionName Name of the collection
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private void initializeFieldWithCollectionPath(String owner, String medalName, String collectionName)
        throws DatabaseAccessException, DocumentException {
        Collections collection = Path.collectionOfField(collectionName);
        ownerId = getUserId(owner);
        batch = dbCol.batch();
        path = Path.ofCollection(collection, ownerId, medalName);
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
