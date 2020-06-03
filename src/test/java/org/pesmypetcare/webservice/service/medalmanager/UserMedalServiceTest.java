package org.pesmypetcare.webservice.service.medalmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.medalmanager.UserMedalDao;
import org.pesmypetcare.webservice.entity.medalmanager.UserMedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
/**
 * @author Oriol Catalán
 */
@ExtendWith(MockitoExtension.class)
public class UserMedalServiceTest {
    private static final List<Map<String, UserMedalEntity>> USER_MEDAL_LIST = new ArrayList<>();
    private static final UserMedalEntity USER_MEDAL_ENTITY = new UserMedalEntity();
    private static final String UID = "afm3am2";
    private static final String USER_MEDAL_NAME = "Walker";
    private static final String FIELD = "progress";
    private static final String TOKEN = "my-token";
    private static final Double VALUE = 2.0;

    @Mock
    private UserMedalDao userMedalDao;
    @Mock
    private UserToken userToken;

    @InjectMocks
    private UserMedalService service = spy(new UserMedalServiceImpl());

    @Test
    public void shouldReturnUserMedalEntityWhenUserMedalRetrieved() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((UserMedalServiceImpl) service).makeUserToken(eq(TOKEN));
        when(userToken.getUid()).thenReturn(UID);
        when(userMedalDao.getUserMedalData(UID, USER_MEDAL_NAME)).thenReturn(USER_MEDAL_ENTITY);
        UserMedalEntity entity = service.getUserMedalData(TOKEN, USER_MEDAL_NAME);
        assertSame(USER_MEDAL_ENTITY, entity, "Should return a userMedal entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetUserMedalRequestFails() {
        doReturn(userToken).when((UserMedalServiceImpl) service).makeUserToken(eq(TOKEN));
        when(userToken.getUid()).thenReturn(UID);
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(userMedalDao).getUserMedalData(any(String.class),
                any(String.class));
            service.getUserMedalData(TOKEN, USER_MEDAL_NAME);
        }, "Should return an exception when retrieving a userMedal fails");
    }

    @Test
    public void shouldReturnUserMedalEntityListWhenGetSetOfUserMedalsRetrieved()
        throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((UserMedalServiceImpl) service).makeUserToken(eq(TOKEN));
        when(userToken.getUid()).thenReturn(UID);
        when(userMedalDao.getAllUserMedalsData(UID)).thenReturn(USER_MEDAL_LIST);
        List<Map<String, UserMedalEntity>> list = service.getAllUserMedalsData(TOKEN);
        assertSame(USER_MEDAL_LIST, list, "Should return a list of userMedal entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfUserMedalsRequestFails() {
        doReturn(userToken).when((UserMedalServiceImpl) service).makeUserToken(eq(TOKEN));
        when(userToken.getUid()).thenReturn(UID);
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(userMedalDao).getAllUserMedalsData(any(String.class));
            service.getAllUserMedalsData(TOKEN);
        }, "Should return an exception when retrieving a set of userMedals fails");
    }

    @Test
    public void shouldReturnUserMedalFieldWhenUserMedalFieldRetrieved() throws DatabaseAccessException,
        DocumentException {
        doReturn(userToken).when((UserMedalServiceImpl) service).makeUserToken(eq(TOKEN));
        when(userToken.getUid()).thenReturn(UID);
        when(userMedalDao.getField(UID, USER_MEDAL_NAME, FIELD)).thenReturn(VALUE);
        Object obtainedValue = service.getField(TOKEN, USER_MEDAL_NAME, FIELD);
        assertSame(VALUE, obtainedValue, "Should return an Object");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetUserMedalFieldRequestFails() {
        doReturn(userToken).when((UserMedalServiceImpl) service).makeUserToken(eq(TOKEN));
        when(userToken.getUid()).thenReturn(UID);
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(userMedalDao).getField(any(String.class),
                any(String.class), any(String.class));
            service.getField(TOKEN, USER_MEDAL_NAME, FIELD);
        }, "Should return an exception when retrieving a userMedal field fails");
    }

    @Test
    public void shouldReturnNothingWhenUserMedalFieldUpdated() throws DatabaseAccessException,
        DocumentException {
        doReturn(userToken).when((UserMedalServiceImpl) service).makeUserToken(eq(TOKEN));
        when(userToken.getUid()).thenReturn(UID);
        service.updateField(TOKEN, USER_MEDAL_NAME, FIELD, USER_MEDAL_ENTITY);
        verify(userMedalDao).updateField(same(UID), same(USER_MEDAL_NAME), same(FIELD), same(USER_MEDAL_ENTITY));
    }
}
