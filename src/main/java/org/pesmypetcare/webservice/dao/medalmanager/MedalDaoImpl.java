package org.pesmypetcare.webservice.dao.medalmanager;

import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.entity.medalmanager.Medal;
import org.pesmypetcare.webservice.entity.medalmanager.MedalEntity;
import com.google.cloud.firestore.DocumentSnapshot;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
import org.pesmypetcare.webservice.entity.medalmanager.UserMedalEntity;
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
    private String path;
    private WriteBatch batch;

    @Autowired
    private FirestoreCollection dbCol;
    @Autowired
    private FirestoreDocument dbDoc;
    @Autowired
    private UserMedalDao userMedalDao;

    @Override
    public void createMedal(String name, MedalEntity medal) throws DatabaseAccessException,
        DocumentException {
        initializeWithCollectionPath();
        dbDoc.createDocumentWithId(path, name, medal, batch);
        dbDoc.commitBatch(batch);
        MedalEntity auxiliar = getMedalData(name);
        addMedalsToUsers(auxiliar);
    }

    @Override
    public MedalEntity getMedalData(String name) throws DatabaseAccessException, DocumentException {
        initializeWithDocumentPath(name);
        return dbDoc.getDocumentDataAsObject(path, MedalEntity.class);
    }

    @Override
    public List<Map<String, MedalEntity>> getAllMedalsData() throws DatabaseAccessException {
        initializeWithCollectionPath();
        List<DocumentSnapshot> medalsDocuments = dbCol.listAllCollectionDocumentSnapshots(path);
        List<Map<String, MedalEntity>> externalList = new ArrayList<>();

        for (DocumentSnapshot medalDocument : medalsDocuments) {
            Map<String, MedalEntity> internalList = new HashMap<>();
            internalList.put("body", medalDocument.toObject(MedalEntity.class));
            externalList.add(internalList);
        }
        return externalList;
    }

    @Override
    public Object getField(String name, String field) throws DatabaseAccessException,
        DocumentException {
        String medalPath = Path.ofDocument(Collections.medals, name);
        return dbDoc.getDocumentField(medalPath, field);
    }


    /**
     * Initializes the batch and path variables for the access, the path is set to the medal document.
     * @param medalName Medal name
     */
    private void initializeWithDocumentPath(String medalName) {
        batch = dbCol.batch();
        path = Path.ofDocument(Collections.medals, medalName);
    }

    /**
     * Initializes the batch and path variables for the access, the path is set to the medal collection.
     */
    private void initializeWithCollectionPath() {
        batch = dbCol.batch();
        path = Path.ofCollection(Collections.medals);
    }

    /**
     * Insert the new medal to all users.
     * @param medal The new medal we had created before
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    private void addMedalsToUsers(MedalEntity medal) throws DatabaseAccessException, DocumentException {
        path = Path.ofCollection(Collections.used_usernames);
        List<DocumentSnapshot> allUsers = dbCol.listAllCollectionDocumentSnapshots(path);
        UserMedalEntity medalEntity  = new UserMedalEntity(medal.getName(), 0., 0.,
            new ArrayList<>(), new Medal(medal));;
        for (DocumentSnapshot user: allUsers) {
            userMedalDao.createUserMedal(user.getId(), medal.getName(), medalEntity);
        }
    }
}
