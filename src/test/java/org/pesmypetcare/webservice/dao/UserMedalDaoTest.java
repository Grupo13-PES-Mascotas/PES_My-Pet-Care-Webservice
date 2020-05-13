package org.pesmypetcare.webservice.dao;


import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.UserMedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;

import java.util.ArrayList;
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
    private static final List<Map<String, Object>> USER_MEDAL_LIST = new ArrayList<>();
    private static final List<DocumentSnapshot> USER_MEDAL_SNAPSHOT_LIST = new ArrayList<>();
    private static final UserMedalEntity USER_MEDAL_ENTITY = new UserMedalEntity();
    private static final String OWNER = "OwnerUsername";
    private static final String OWNER_ID = "OwnerId";
    private static final String USER_MEDAL_NAME = "UserMedalName";
    private static final String SIMPLE_FIELD = "progress";
    private static final Double VALUE = 2.0;

    @Mock
    private FirestoreCollection dbCol;
    @Mock
    private FirestoreDocument dbDoc;
    @Mock
    private WriteBatch batch;

    @InjectMocks
    private UserMedalDao userMedalDao = new UserMedalDaoImpl();

    @Test
    public void shouldReturnUserMedalEntityFromDatabaseWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getDocumentDataAsObject(anyString(), any())).willReturn(USER_MEDAL_ENTITY);

        UserMedalEntity result = userMedalDao.getUserMedalData(OWNER, USER_MEDAL_NAME);

        assertSame(USER_MEDAL_ENTITY, result, "Should return UserMedal Entity");
    }

    @Test
    public void shouldReturnAllUserMedalsDataOnDatabaseWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(USER_MEDAL_SNAPSHOT_LIST);

        List<Map<String, Object>> list = userMedalDao.getAllUserMedalsData(OWNER);

        assertEquals(USER_MEDAL_LIST, list, "Should return a List containing all userMedals Data");
    }

    @Test
    public void shouldReturnUserMedalSimpleFieldFromDatabaseWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbDoc.getDocumentField(anyString(), anyString())).willReturn(VALUE);

        Object userMedalValue = userMedalDao.getSimpleField(OWNER, USER_MEDAL_NAME, SIMPLE_FIELD);

        assertSame(VALUE, userMedalValue, "Should return field value");
    }

    @Test
    public void shouldUpdateSimpleFieldWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(batch.commit()).willReturn(null);

        userMedalDao.updateSimpleField(OWNER, USER_MEDAL_NAME, SIMPLE_FIELD, VALUE);

        verify(dbDoc).updateDocumentFields(same(batch), isA(String.class), same(SIMPLE_FIELD), same(VALUE));
    }
}
