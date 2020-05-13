package org.pesmypetcare.webservice.service.medalmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.medalmanager.MedalDao;
import org.pesmypetcare.webservice.entity.medalmanager.MedalEntity;
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
public class MedalServiceTest {
    private static final List<Map<String, Object>> MEDAL_LIST = new ArrayList<>();
    private static final MedalEntity MEDAL_ENTITY = new MedalEntity();
    private static final String MEDAL_NAME = "Walker";
    private static final String FIELD = "description";
    private static final String VALUE = "You have to walk a lot of kilometers!";

    @Mock
    private MedalDao medalDao;

    @InjectMocks
    private MedalService service = new MedalServiceImpl();

    @Test
    public void shouldReturnMedalEntityWhenMedalRetrieved() throws DatabaseAccessException, DocumentException {
        when(medalDao.getMedalData(MEDAL_NAME)).thenReturn(MEDAL_ENTITY);
        MedalEntity entity = service.getMedalData(MEDAL_NAME);
        assertSame(MEDAL_ENTITY, entity, "Should return a medal entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetMedalRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(medalDao).getMedalData(any(String.class));
            service.getMedalData(MEDAL_NAME);
        }, "Should return an exception when retrieving a medal fails");
    }

    @Test
    public void shouldReturnMedalEntityListWhenGetSetOfMedalsRetrieved()
        throws DatabaseAccessException, DocumentException {
        when(medalDao.getAllMedalsData()).thenReturn(MEDAL_LIST);
        List<Map<String, Object>> list = service.getAllMedalsData();
        assertSame(MEDAL_LIST, list, "Should return a list of medal entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfMedalsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(medalDao).getAllMedalsData();
            service.getAllMedalsData();
        }, "Should return an exception when retrieving a set of medals fails");
    }

    @Test
    public void shouldReturnMedalSimpleFieldWhenMedalFieldRetrieved() throws DatabaseAccessException,
        DocumentException {
        when(medalDao.getField(MEDAL_NAME, FIELD)).thenReturn(VALUE);
        Object obtainedValue = service.getField(MEDAL_NAME, FIELD);
        assertSame(VALUE, obtainedValue, "Should return an Object");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetMedalFieldRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(medalDao).getField(any(String.class), any(String.class));
            service.getField(MEDAL_NAME, FIELD);
        }, "Should return an exception when retrieving a medal field fails");
    }
}
