package org.pesmypetcare.webservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.PathologiesDao;
import org.pesmypetcare.webservice.entity.PathologiesEntity;
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
public class PathologiesServiceTest {

    private static List<Map<String, Object>> pathologiesList;
    private static PathologiesEntity pathologiesEntity;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static Double value;

    @Mock
    private PathologiesDao pathologiesDao;

    @InjectMocks
    private PathologiesService service = new PathologiesServiceImpl();

    @BeforeAll
    public static void setUp() {
        pathologiesList = new ArrayList<>();
        pathologiesEntity = new PathologiesEntity();
        date = "2020-02-13T10:30:00";
        date2 = "2021-02-13T10:30:00";
        owner = "TupoJohn";
        petName = "Perico";
        value = 9.0 / 2.0;
    }

    @Test
    public void shouldReturnNothingWhenPathologiesCreated() {
        service.createPathologies(owner, petName, date, pathologiesEntity);
        verify(pathologiesDao).createPathologies(isA(String.class), isA(String.class), isA(String.class),
            isA(PathologiesEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenPathologiesDeleted() throws DatabaseAccessException {
        service.deletePathologiesByDate(owner, petName, date);
        verify(pathologiesDao).deletePathologiesByDate(isA(String.class), isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllPathologiessDeleted() throws DatabaseAccessException {
        service.deleteAllPathologiess(owner, petName);
        verify(pathologiesDao).deleteAllPathologiess(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenAllPathologiesDeleteFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(pathologiesDao).deleteAllPathologiess(any(String.class),
                isA(String.class));
            service.deleteAllPathologiess(owner, petName);
        }, "Should return a database access exception when a pathologies deletion fails");
    }

    @Test
    public void shouldReturnMealEntityWhenPathologiesRetrieved() throws DatabaseAccessException {
        when(pathologiesDao.getPathologiesByDate(owner, petName, date)).thenReturn(pathologiesEntity);
        PathologiesEntity meal = service.getPathologiesByDate(owner, petName, date);
        assertSame(pathologiesEntity, meal, "Should return a pathologies entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetPathologiesRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(pathologiesDao).getPathologiesByDate(any(String.class), any(String.class),
                isA(String.class));
            service.getPathologiesByDate(owner, petName, date);
        }, "Should return an exception when retrieving a pathologies fails");
    }

    @Test
    public void shouldReturnMealEntityListWhenGetSetOfPathologiessRetrieved() throws DatabaseAccessException {
        when(pathologiesDao.getAllPathologies(owner, petName)).thenReturn(pathologiesList);
        List<Map<String, Object>> list = service.getAllPathologies(owner, petName);
        assertSame(pathologiesList, list, "Should return a list of pathologiess entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfPathologiessRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(pathologiesDao).getAllPathologies(any(String.class), isA(String.class));
            service.getAllPathologies(owner, petName);
        }, "Should return an exception when retrieving a set of pathologiess fails");
    }

    @Test
    public void shouldReturnPathologiesEntityListWhenGetPathologiessBetweenDatesRetrieved() throws DatabaseAccessException {
        when(pathologiesDao.getAllPathologiessBetween(owner, petName, date, date2)).thenReturn(pathologiesList);
        List<Map<String, Object>> list = service.getAllPathologiessBetween(owner, petName, date, date2);
        assertSame(pathologiesList, list, "Should return a list of pathologies entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetPathologiessBetweenDatesRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(pathologiesDao).getAllPathologiessBetween(any(String.class),
                isA(String.class), isA(String.class), isA(String.class));
            service.getAllPathologiessBetween(owner, petName, date, date2);
        }, "Should return an exception when retrieving a set of pathologiess between dates fails");
    }

    @Test
    public void shouldReturnNothingWhenPathologiesFieldUpdated() throws DatabaseAccessException {
        service.updatePathologies(owner, petName, date, value);
        verify(pathologiesDao).updatePathologies(isA(String.class), isA(String.class), isA(String.class),
            isA(Double.class));
    }
}
