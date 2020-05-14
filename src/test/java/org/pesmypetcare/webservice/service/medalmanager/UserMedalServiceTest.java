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
    private static final List<Map<String, Object>> USER_MEDAL_LIST = new ArrayList<>();
    private static final UserMedalEntity USER_MEDAL_ENTITY = new UserMedalEntity();
    private static final String OWNER = "Benito Camela";
    private static final String USER_MEDAL_NAME = "Walker";
    private static final String FIELD = "progress";
    private static final Double VALUE = 2.0;

    @Mock
    private UserMedalDao userMedalDao;

    @InjectMocks
    private UserMedalService service = new UserMedalServiceImpl();

    @Test
    public void shouldReturnUserMedalEntityWhenUserMedalRetrieved() throws DatabaseAccessException, DocumentException {
        when(userMedalDao.getUserMedalData(OWNER, USER_MEDAL_NAME)).thenReturn(USER_MEDAL_ENTITY);
        UserMedalEntity entity = service.getUserMedalData(OWNER, USER_MEDAL_NAME);
        assertSame(USER_MEDAL_ENTITY, entity, "Should return a userMedal entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetUserMedalRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(userMedalDao).getUserMedalData(any(String.class),
                any(String.class));
            service.getUserMedalData(OWNER, USER_MEDAL_NAME);
        }, "Should return an exception when retrieving a userMedal fails");
    }

    @Test
    public void shouldReturnUserMedalEntityListWhenGetSetOfUserMedalsRetrieved()
        throws DatabaseAccessException, DocumentException {
        when(userMedalDao.getAllUserMedalsData(OWNER)).thenReturn(USER_MEDAL_LIST);
        List<Map<String, Object>> list = service.getAllUserMedalsData(OWNER);
        assertSame(USER_MEDAL_LIST, list, "Should return a list of userMedal entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfUserMedalsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(userMedalDao).getAllUserMedalsData(any(String.class));
            service.getAllUserMedalsData(OWNER);
        }, "Should return an exception when retrieving a set of userMedals fails");
    }

    @Test
    public void shouldReturnUserMedalFieldWhenUserMedalFieldRetrieved() throws DatabaseAccessException,
        DocumentException {
        when(userMedalDao.getField(OWNER, USER_MEDAL_NAME, FIELD)).thenReturn(VALUE);
        Object obtainedValue = service.getField(OWNER, USER_MEDAL_NAME, FIELD);
        assertSame(VALUE, obtainedValue, "Should return an Object");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetUserMedalFieldRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(userMedalDao).getField(any(String.class),
                any(String.class), any(String.class));
            service.getField(OWNER, USER_MEDAL_NAME, FIELD);
        }, "Should return an exception when retrieving a userMedal field fails");
    }

    @Test
    public void shouldReturnNothingWhenUserMedalFieldUpdated() throws DatabaseAccessException,
        DocumentException {
        service.updateField(OWNER, USER_MEDAL_NAME, FIELD, VALUE);
        verify(userMedalDao).updateField(same(OWNER), same(USER_MEDAL_NAME), same(FIELD), same(VALUE));
    }
}
