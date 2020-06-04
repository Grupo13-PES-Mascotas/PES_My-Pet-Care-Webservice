package org.pesmypetcare.webservice.dao.medalmanager;


import org.pesmypetcare.webservice.entity.medalmanager.Medal;
import org.pesmypetcare.webservice.entity.medalmanager.MedalEntity;
import org.pesmypetcare.webservice.entity.medalmanager.UserMedalEntity;
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
public class UserMedalDaoImpl implements UserMedalDao {
    private WriteBatch batch;
    private String path;

    @Autowired
    private FirestoreCollection dbCol;
    @Autowired
    private FirestoreDocument dbDoc;

    @Override
    public void createUserMedal(String userId, String name, UserMedalEntity medal) throws DatabaseAccessException,
        DocumentException {
        initializeWithCollectionPath(userId);
        dbDoc.createDocumentWithId(path, name, medal, batch);
        dbDoc.commitBatch(batch);
    }

    @Override
    public void createUserMedal(String userId, String name, UserMedalEntity medal, WriteBatch batch) {
        initializeWithCollectionPath(userId);
        dbDoc.createDocumentWithId(path, name, medal, batch);
    }

    @Override
    public UserMedalEntity getUserMedalData(String userId, String name) throws DatabaseAccessException,
        DocumentException {
        initializeWithDocumentPath(userId, name);
        return dbDoc.getDocumentDataAsObject(path, UserMedalEntity.class);
    }

    @Override
    public List<Map<String, UserMedalEntity>> getAllUserMedalsData(String userId) throws DatabaseAccessException {
        initializeWithCollectionPath(userId);
        List<DocumentSnapshot> medalsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);
        List<Map<String, UserMedalEntity>> externalList = new ArrayList<>();

        for (DocumentSnapshot medalDocument : medalsDocuments) {
            Map<String, UserMedalEntity> internalList = new HashMap<>();
            internalList.put("body", medalDocument.toObject(UserMedalEntity.class));
            externalList.add(internalList);
        }
        return externalList;
    }

    @Override
    public void updateField(String userId, String name, String field, Object value) {
        initializeWithDocumentPath(userId, name);
        dbDoc.updateDocumentFields(batch, path, field, value);
        batch.commit();
    }

    @Override
    public Object getField(String userId, String name, String field) throws DatabaseAccessException,
        DocumentException {
        String medalPath = Path.ofDocument(Collections.userMedals, userId, name);
        return dbDoc.getDocumentField(medalPath, field);
    }

    @Override
    public void createAllUserMedals(String username, WriteBatch batch) throws DatabaseAccessException {
        path = Path.ofCollection(Collections.medals);
        List<DocumentSnapshot> medalsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);
        UserMedalEntity userMedal;
        for (DocumentSnapshot medalDocument : medalsDocuments) {
            MedalEntity medal = medalDocument.toObject(MedalEntity.class);
            userMedal = new UserMedalEntity(medal.getName(), 0., 0.,
                new ArrayList<>(), new Medal(medal));
            createUserMedal(username, medal.getName(), userMedal, batch);
        }
    }

    /**
     * Initializes the ownerId, batch and path variables for the access, the path is set to the medal document.
     * @param userId The user ID
     * @param medalName Medal name
     */
    private void initializeWithDocumentPath(String userId, String medalName) {
        batch = dbCol.batch();
        path = Path.ofDocument(Collections.userMedals, userId, medalName);
    }

    /**
     * Initializes the ownerId, batch and path variables for the access, the path is set to the user's medal collection.
     * @param userId The user ID
     */
    private void initializeWithCollectionPath(String userId) {
        batch = dbCol.batch();
        path = Path.ofCollection(Collections.userMedals, userId);
    }

}
