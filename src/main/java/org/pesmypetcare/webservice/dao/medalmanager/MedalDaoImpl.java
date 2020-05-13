package org.pesmypetcare.webservice.dao.medalmanager;

import org.pesmypetcare.webservice.entity.medalmanager.MedalEntity;
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
    private WriteBatch batch;
    private String path;

    @Autowired
    private FirestoreCollection dbCol;
    @Autowired
    private FirestoreDocument dbDoc;

    @Override
    public MedalEntity getMedalData(String name) throws DatabaseAccessException, DocumentException {
        initializeWithDocumentPath(name);
        return dbDoc.getDocumentDataAsObject(path, MedalEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllMedalsData() throws DatabaseAccessException, DocumentException {
        initializeWithCollectionPath();
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
    public Object getSimpleField(String name, String field) throws DatabaseAccessException,
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
}
