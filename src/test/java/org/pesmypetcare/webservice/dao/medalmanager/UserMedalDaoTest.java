package org.pesmypetcare.webservice.dao.medalmanager;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author Oriol Catalán
 */
@ExtendWith(MockitoExtension.class)
public class UserMedalDaoTest {
    private static final String OWNER = "OwnerUsername";
    private static final String OWNER_ID = "OwnerId";
    private static final String USER_MEDAL_NAME = "UserMedalName";
    private static final String SIMPLE_FIELD = "progress";
    private static final Double VALUE = 2.0;

    private static UserMedalEntity userMedalEntity;
    private static List<Map<String, Object>> userMedalList;
    private static List<DocumentSnapshot> snapshotList;


    @Mock
    private FirestoreCollection dbCol;
    @Mock
    private FirestoreDocument dbDoc;
    @Mock
    private WriteBatch batch;
    @Mock
    private DocumentSnapshot documentSnapshot;

    @InjectMocks
    private UserMedalDao userMedalDao = new UserMedalDaoImpl();

    @BeforeEach
    public void setUp() {
        userMedalEntity = new UserMedalEntity("Walker", 1.0, 2.0, new ArrayList<String>());
        userMedalList = new ArrayList<>();
        Map<String, Object> auxMap = new HashMap<>();
        auxMap.put("name", USER_MEDAL_NAME);
        auxMap.put("body", userMedalEntity);
        userMedalList.add(auxMap);
        userMedalList.add(auxMap);
        userMedalList.add(auxMap);
        snapshotList = new ArrayList<>();
        snapshotList.add(documentSnapshot);
        snapshotList.add(documentSnapshot);
        snapshotList.add(documentSnapshot);
    }

    @Test
    public void shouldReturnUserMedalEntityFromDatabaseWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getDocumentDataAsObject(anyString(), any())).willReturn(userMedalEntity);

        UserMedalEntity result = userMedalDao.getUserMedalData(OWNER, USER_MEDAL_NAME);

        assertSame(userMedalEntity, result, "Should return UserMedal Entity");
    }

    @Test
    public void shouldReturnAllUserMedalsDataOnDatabaseWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(documentSnapshot.getId()).willReturn(USER_MEDAL_NAME);
        given(documentSnapshot.toObject(any())).willReturn(userMedalEntity);
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(snapshotList);

        List<Map<String, Object>> list = userMedalDao.getAllUserMedalsData(OWNER);

        assertEquals(userMedalList, list, "Should return a List containing all userMedals Data");
    }

    @Test
    public void shouldReturnUserMedalSimpleFromDatabaseWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbDoc.getDocumentField(anyString(), anyString())).willReturn(VALUE);

        Object userMedalValue = userMedalDao.getField(OWNER, USER_MEDAL_NAME, SIMPLE_FIELD);

        assertSame(VALUE, userMedalValue, "Should return field value");
    }

    @Test
    public void shouldUpdateSimpleFieldWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);

        userMedalDao.updateField(OWNER, USER_MEDAL_NAME, SIMPLE_FIELD, VALUE);

        verify(dbDoc).updateDocumentFields(same(batch), isA(String.class), same(SIMPLE_FIELD), same(VALUE));
    }
}