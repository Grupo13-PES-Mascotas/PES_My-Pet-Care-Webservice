package org.pesmypetcare.webservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.FreqWashDao;
import org.pesmypetcare.webservice.entity.FreqWashEntity;
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
public class FreqWashServiceTest {

    private static List<Map<String, Object>> freqWashList;
    private static FreqWashEntity freqWashEntity;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static Double value;

    @Mock
    private FreqWashDao freqWashDao;

    @InjectMocks
    private FreqWashService service = new FreqWashServiceImpl();

    @BeforeAll
    public static void setUp() {
        freqWashList = new ArrayList<>();
        freqWashEntity = new FreqWashEntity();
        date = "2020-02-13T10:30:00";
        date2 = "2021-02-13T10:30:00";
        owner = "TupoJohn";
        petName = "Perico";
        value = 2.0;
    }

    @Test
    public void shouldReturnNothingWhenFreqWashCreated() {
        service.createFreqWash(owner, petName, date, freqWashEntity);
        verify(freqWashDao).createFreqWash(isA(String.class), isA(String.class), isA(String.class),
            isA(FreqWashEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenFreqWashDeleted() throws DatabaseAccessException {
        service.deleteFreqWashByDate(owner, petName, date);
        verify(freqWashDao).deleteFreqWashByDate(isA(String.class), isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllFreqWashsDeleted() throws DatabaseAccessException {
        service.deleteAllFreqWashs(owner, petName);
        verify(freqWashDao).deleteAllFreqWashs(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenAllFreqWashDeleteFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(freqWashDao).deleteAllFreqWashs(any(String.class),
                isA(String.class));
            service.deleteAllFreqWashs(owner, petName);
        }, "Should return a database access exception when a freqWash deletion fails");
    }

    @Test
    public void shouldReturnMealEntityWhenFreqWashRetrieved() throws DatabaseAccessException {
        when(freqWashDao.getFreqWashByDate(owner, petName, date)).thenReturn(freqWashEntity);
        FreqWashEntity meal = service.getFreqWashByDate(owner, petName, date);
        assertSame(freqWashEntity, meal, "Should return a freqWash entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetFreqWashRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(freqWashDao).getFreqWashByDate(any(String.class),
                any(String.class), isA(String.class));
            service.getFreqWashByDate(owner, petName, date);
        }, "Should return an exception when retrieving a freqWash fails");
    }

    @Test
    public void shouldReturnMealEntityListWhenGetSetOfFreqWashsRetrieved() throws DatabaseAccessException {
        when(freqWashDao.getAllFreqWash(owner, petName)).thenReturn(freqWashList);
        List<Map<String, Object>> list = service.getAllFreqWash(owner, petName);
        assertSame(freqWashList, list, "Should return a list of freqWashs entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfFreqWashsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(freqWashDao).getAllFreqWash(any(String.class),
                isA(String.class));
            service.getAllFreqWash(owner, petName);
        }, "Should return an exception when retrieving a set of freqWashs fails");
    }

    @Test
    public void shouldReturnFreqWashEntityListWhenGetFreqWashsBetweenDatesRetrieved() throws DatabaseAccessException {
        when(freqWashDao.getAllFreqWashsBetween(owner, petName, date, date2)).thenReturn(freqWashList);
        List<Map<String, Object>> list = service.getAllFreqWashsBetween(owner, petName, date, date2);
        assertSame(freqWashList, list, "Should return a list of freqWash entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetFreqWashsBetweenDatesRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(freqWashDao).getAllFreqWashsBetween(any(String.class),
                isA(String.class), isA(String.class), isA(String.class));
            service.getAllFreqWashsBetween(owner, petName, date, date2);
        }, "Should return an exception when retrieving a set of freqWashs between dates fails");
    }

    @Test
    public void shouldReturnNothingWhenFreqWashFieldUpdated() throws DatabaseAccessException {
        service.updateFreqWash(owner, petName, date, value);
        verify(freqWashDao).updateFreqWash(isA(String.class), isA(String.class), isA(String.class),
            isA(Double.class));
    }
}
