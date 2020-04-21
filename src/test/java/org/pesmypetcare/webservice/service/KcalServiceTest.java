package org.pesmypetcare.webservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.KcalDao;
import org.pesmypetcare.webservice.entity.KcalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KcalServiceTest {

    private static List<Map<String, Object>> kcalList;
    private static KcalEntity kcalEntity;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static Double value;

    @Mock
    private KcalDao kcalDao;

    @InjectMocks
    private KcalService service = new KcalServiceImpl();

    @BeforeAll
    public static void setUp() {
        kcalList = new ArrayList<>();
        kcalEntity = new KcalEntity();
        date = "2020-02-13T10:30:00";
        date2 = "2021-02-13T10:30:00";
        owner = "TupoJohn";
        petName = "Perico";
        value = 9.0 / 2.0;
    }

    @Test
    public void shouldReturnNothingWhenKcalCreated() {
        service.createKcal(owner, petName, date, kcalEntity);
        verify(kcalDao).createKcal(isA(String.class), isA(String.class), isA(String.class),
            isA(KcalEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenKcalDeleted() throws DatabaseAccessException {
        service.deleteKcalByDate(owner, petName, date);
        verify(kcalDao).deleteKcalByDate(isA(String.class), isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllKcalsDeleted() throws DatabaseAccessException {
        service.deleteAllKcals(owner, petName);
        verify(kcalDao).deleteAllKcals(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenAllKcalDeleteFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(kcalDao).deleteAllKcals(any(String.class),
                isA(String.class));
            service.deleteAllKcals(owner, petName);
        }, "Should return a database access exception when a kcal deletion fails");
    }

    @Test
    public void shouldReturnMealEntityWhenKcalRetrieved() throws DatabaseAccessException {
        when(kcalDao.getKcalByDate(owner, petName, date)).thenReturn(kcalEntity);
        KcalEntity meal = service.getKcalByDate(owner, petName, date);
        assertSame(kcalEntity, meal, "Should return a kcal entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetKcalRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(kcalDao).getKcalByDate(any(String.class), any(String.class),
                isA(String.class));
            service.getKcalByDate(owner, petName, date);
        }, "Should return an exception when retrieving a kcal fails");
    }

    @Test
    public void shouldReturnMealEntityListWhenGetSetOfKcalsRetrieved() throws DatabaseAccessException {
        when(kcalDao.getAllKcal(owner, petName)).thenReturn(kcalList);
        List<Map<String, Object>> list = service.getAllKcal(owner, petName);
        assertSame(kcalList, list, "Should return a list of kcals entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfKcalsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(kcalDao).getAllKcal(any(String.class), isA(String.class));
            service.getAllKcal(owner, petName);
        }, "Should return an exception when retrieving a set of kcals fails");
    }

    @Test
    public void shouldReturnKcalEntityListWhenGetKcalsBetweenDatesRetrieved() throws DatabaseAccessException {
        when(kcalDao.getAllKcalsBetween(owner, petName, date, date2)).thenReturn(kcalList);
        List<Map<String, Object>> list = service.getAllKcalsBetween(owner, petName, date, date2);
        assertSame(kcalList, list, "Should return a list of kcal entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetKcalsBetweenDatesRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(kcalDao).getAllKcalsBetween(any(String.class),
                isA(String.class), isA(String.class), isA(String.class));
            service.getAllKcalsBetween(owner, petName, date, date2);
        }, "Should return an exception when retrieving a set of kcals between dates fails");
    }

    @Test
    public void shouldReturnNothingWhenKcalFieldUpdated() throws DatabaseAccessException {
        service.updateKcal(owner, petName, date, value);
        verify(kcalDao).updateKcal(isA(String.class), isA(String.class), isA(String.class),
            isA(Double.class));
    }
}
