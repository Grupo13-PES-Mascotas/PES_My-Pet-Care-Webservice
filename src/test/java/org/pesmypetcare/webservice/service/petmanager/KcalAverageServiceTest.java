package org.pesmypetcare.webservice.service.petmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.petmanager.KcalAverageDao;
import org.pesmypetcare.webservice.entity.petmanager.KcalAverageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KcalAverageServiceTest {

    private static List<Map<String, Object>> kcalAverageAverageList;
    private static KcalAverageEntity kcalAverageAverageEntity;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static Double value;

    @Mock
    private KcalAverageDao kcalAverageDao;

    @InjectMocks
    private KcalAverageService service = new KcalAverageServiceImpl();

    @BeforeAll
    public static void setUp() {
        kcalAverageAverageList = new ArrayList<>();
        kcalAverageAverageEntity = new KcalAverageEntity();
        date = "2020-02-13T10:30:00";
        date2 = "2021-02-13T10:30:00";
        owner = "TupoJohn";
        petName = "Perico";
        value = 2.0;
    }

    @Test
    public void shouldReturnNothingWhenKcalAverageCreated() {
        service.createKcalAverage(owner, petName, date, kcalAverageAverageEntity);
        verify(kcalAverageDao).createKcalAverage(isA(String.class), isA(String.class), isA(String.class),
            isA(KcalAverageEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenKcalAverageDeleted() throws DatabaseAccessException {
        service.deleteKcalAverageByDate(owner, petName, date);
        verify(kcalAverageDao).deleteKcalAverageByDate(isA(String.class), isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllKcalAveragesDeleted() throws DatabaseAccessException {
        service.deleteAllKcalAverages(owner, petName);
        verify(kcalAverageDao).deleteAllKcalAverages(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenAllKcalAverageDeleteFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(kcalAverageDao).deleteAllKcalAverages(any(String.class),
                isA(String.class));
            service.deleteAllKcalAverages(owner, petName);
        }, "Should return a database access exception when a kcalAverageAverage deletion fails");
    }

    @Test
    public void shouldReturnMealEntityWhenKcalAverageRetrieved() throws DatabaseAccessException {
        when(kcalAverageDao.getKcalAverageByDate(owner, petName, date)).thenReturn(kcalAverageAverageEntity);
        KcalAverageEntity meal = service.getKcalAverageByDate(owner, petName, date);
        assertSame(kcalAverageAverageEntity, meal, "Should return a kcalAverageAverage entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetKcalAverageRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(kcalAverageDao).getKcalAverageByDate(any(String.class),
                any(String.class), isA(String.class));
            service.getKcalAverageByDate(owner, petName, date);
        }, "Should return an exception when retrieving a kcalAverageAverage fails");
    }

    @Test
    public void shouldReturnMealEntityListWhenGetSetOfKcalAveragesRetrieved() throws DatabaseAccessException {
        when(kcalAverageDao.getAllKcalAverage(owner, petName)).thenReturn(kcalAverageAverageList);
        List<Map<String, Object>> list = service.getAllKcalAverage(owner, petName);
        assertSame(kcalAverageAverageList, list, "Should return a list of kcalAverageAverages entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfKcalAveragesRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(kcalAverageDao).getAllKcalAverage(any(String.class),
                isA(String.class));
            service.getAllKcalAverage(owner, petName);
        }, "Should return an exception when retrieving a set of kcalAverageAverages fails");
    }

    @Test
    public void shouldReturnKcalAverageEntityListWhenGetKcalAveragesBetweenDatesRetrieved()
        throws DatabaseAccessException {
        when(kcalAverageDao.getAllKcalAveragesBetween(owner, petName, date, date2)).thenReturn(kcalAverageAverageList);
        List<Map<String, Object>> list = service.getAllKcalAveragesBetween(owner, petName, date, date2);
        assertSame(kcalAverageAverageList, list, "Should return a list of kcalAverageAverage entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetKcalAveragesBetweenDatesRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(kcalAverageDao).getAllKcalAveragesBetween(any(String.class),
                isA(String.class), isA(String.class), isA(String.class));
            service.getAllKcalAveragesBetween(owner, petName, date, date2);
        }, "Should return an exception when retrieving a set of kcalAverageAverages between dates fails");
    }

    @Test
    public void shouldReturnNothingWhenKcalAverageFieldUpdated() throws DatabaseAccessException {
        service.updateKcalAverage(owner, petName, date, value);
        verify(kcalAverageDao).updateKcalAverage(isA(String.class), isA(String.class), isA(String.class),
            isA(Double.class));
    }
}
