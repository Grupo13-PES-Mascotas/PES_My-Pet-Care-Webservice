package org.pesmypetcare.webservice.dao.medalmanager;


import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.medalmanager.MedalDao;
import org.pesmypetcare.webservice.dao.medalmanager.MedalDaoImpl;
import org.pesmypetcare.webservice.entity.medalmanager.MedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
/**
 * @author Oriol Catalán
 */
@ExtendWith(MockitoExtension.class)
public class MedalDaoTest {
    private static final List<Map<String, Object>> MEDAL_LIST = new ArrayList<>();
    private static final List<DocumentSnapshot> MEDAL_SNAPSHOT_LIST = new ArrayList<>();
    private static final MedalEntity MEDAL_ENTITY = new MedalEntity();
    private static final String MEDAL_NAME = "MedalName";
    private static final String SIMPLE_FIELD = "description";
    private static final String VALUE = "You have to walk a lot of kilometers!";

    @Mock
    private FirestoreCollection dbCol;
    @Mock
    private FirestoreDocument dbDoc;
    @Mock
    private WriteBatch batch;

    @InjectMocks
    private MedalDao medalDao = new MedalDaoImpl();

    @Test
    public void shouldReturnMedalEntityFromDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getDocumentDataAsObject(anyString(), any())).willReturn(MEDAL_ENTITY);

        MedalEntity result = medalDao.getMedalData(MEDAL_NAME);

        assertSame(MEDAL_ENTITY, result, "Should return Medal Entity");
    }

    @Test
    public void shouldReturnAllMedalsDataOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(MEDAL_SNAPSHOT_LIST);

        List<Map<String, Object>> list = medalDao.getAllMedalsData();

        assertEquals(MEDAL_LIST, list, "Should return a List containing all medals Data");
    }

    @Test
    public void shouldReturnMedalSimpleFieldFromDatabaseWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(dbDoc.getDocumentField(anyString(), anyString())).willReturn(VALUE);

        Object medalValue = medalDao.getSimpleField(MEDAL_NAME, SIMPLE_FIELD);

        assertSame(VALUE, medalValue, "Should return field value");
    }
}
