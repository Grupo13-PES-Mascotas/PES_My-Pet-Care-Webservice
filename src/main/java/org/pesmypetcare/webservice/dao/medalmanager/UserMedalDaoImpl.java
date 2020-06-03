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
    private String ownerId;
    private WriteBatch batch;
    private String path;

    @Autowired
    private FirestoreCollection dbCol;
    @Autowired
    private FirestoreDocument dbDoc;

    @Override
    public void createUserMedal(String owner, String name, UserMedalEntity medal) throws DatabaseAccessException,
        DocumentException {
        initializeWithCollectionPath(owner);
        dbDoc.createDocumentWithId(path, name, medal, batch);
        dbDoc.commitBatch(batch);
    }

    @Override
    public UserMedalEntity getUserMedalData(String owner, String name) throws DatabaseAccessException,
        DocumentException {
        initializeWithDocumentPath(owner, name);
        return dbDoc.getDocumentDataAsObject(path, UserMedalEntity.class);
    }

    @Override
    public List<Map<String, UserMedalEntity>> getAllUserMedalsData(String owner) throws DatabaseAccessException,
        DocumentException {
        initializeWithCollectionPath(owner);
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
    public void updateField(String owner, String name, String field, Object value)
        throws DatabaseAccessException, DocumentException {
        initializeWithDocumentPath(owner, name);
        dbDoc.updateDocumentFields(batch, path, field, value);
        batch.commit();
    }

    @Override
    public Object getField(String owner, String name, String field) throws DatabaseAccessException,
        DocumentException {
        String medalPath = Path.ofDocument(Collections.userMedals, getUserId(owner), name);
        return dbDoc.getDocumentField(medalPath, field);
    }

    /**
     * Create all user medals when user is created.
     * @param username Username of the user.
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    public void createAllUserMedals(String username) throws DatabaseAccessException, DocumentException {
        path = Path.ofCollection(Collections.medals);
        List<DocumentSnapshot> medalsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);
        UserMedalEntity userMedal;
        for (DocumentSnapshot medalDocument : medalsDocuments) {
            MedalEntity medal = medalDocument.toObject(MedalEntity.class);
            userMedal = new UserMedalEntity(medal.getName(), 0., 0.,
                new ArrayList<>(), new Medal(medal));
            createUserMedal(username, medal.getName(), userMedal);
        }
    }

    /**
     * Initializes the ownerId, batch and path variables for the access, the path is set to the medal document.
     * @param owner Owner of the medal
     * @param medalName Medal name
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private void initializeWithDocumentPath(String owner, String medalName) throws DatabaseAccessException,
        DocumentException {
        ownerId = getUserId(owner);
        batch = dbCol.batch();
        path = Path.ofDocument(Collections.userMedals, ownerId, medalName);
    }

    /**
     * Initializes the ownerId, batch and path variables for the access, the path is set to the user's medal collection.
     * @param owner Owner of the medals
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private void initializeWithCollectionPath(String owner) throws DatabaseAccessException,
        DocumentException {
        ownerId = getUserId(owner);
        batch = dbCol.batch();
        path = Path.ofCollection(Collections.userMedals, ownerId);
    }

    /**
     * Returns the id of a user specifying its username.
     * @param username Name of the user
     * @return The user's id
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private String getUserId(String username) throws DatabaseAccessException, DocumentException {
        String usernamePath = Path.ofDocument(Collections.used_usernames, username);
        return dbDoc.getStringFromDocument(usernamePath, "user");
    }

}
