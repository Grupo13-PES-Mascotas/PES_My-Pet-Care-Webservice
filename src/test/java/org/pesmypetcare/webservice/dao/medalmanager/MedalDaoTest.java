package org.pesmypetcare.webservice.dao.medalmanager;


import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.medalmanager.MedalEntity;
import org.pesmypetcare.webservice.entity.medalmanager.UserMedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;

import java.util.ArrayList;
import java.util.HashMap;
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
    private static final String MEDAL_NAME = "MedalName";
    private static final String SIMPLE_FIELD = "description";
    private static final String VALUE = "You have to walk a lot of kilometers!";

    private static MedalEntity medalEntity;
    private static List<Map<String, Object>> medalList;

    @Mock
    private FirestoreCollection dbCol;
    @Mock
    private FirestoreDocument dbDoc;

    @InjectMocks
    private MedalDao medalDao = new MedalDaoImpl();

    @BeforeEach
    public void setUp() {
        medalEntity = new MedalEntity("Walker", new ArrayList<Double>(), "You have to walk a lot!");
        medalList = new ArrayList<>();
        Map<String, Object> auxMap = new HashMap<>();
        auxMap.put("name", MEDAL_NAME);
        auxMap.put("body", medalEntity);
        medalList.add(auxMap);
        medalList.add(auxMap);
        medalList.add(auxMap);
    }

    @Test
    public void shouldReturnMedalEntityFromDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getDocumentDataAsObject(anyString(), any())).willReturn(medalEntity);

        MedalEntity result = medalDao.getMedalData(MEDAL_NAME);

        assertSame(medalEntity, result, "Should return Medal Entity");
    }

    @Test
    public void shouldReturnAllMedalsDataOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getDocumentDataAsObject(anyString(), any())).willReturn(medalEntity);
        List<Map<String, Object>> list = medalDao.getAllMedalsData();

        assertEquals(medalList, list, "Should return a List containing all medals Data");
    }

    @Test
    public void shouldReturnMedalSimpleFieldFromDatabaseWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(dbDoc.getDocumentField(anyString(), anyString())).willReturn(VALUE);

        Object medalValue = medalDao.getField(MEDAL_NAME, SIMPLE_FIELD);

        assertSame(VALUE, medalValue, "Should return field value");
    }
}
