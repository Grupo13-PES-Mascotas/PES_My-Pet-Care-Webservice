package org.pesmypetcare.webservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.WeightDao;
import org.pesmypetcare.webservice.entity.WeightEntity;
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
public class WeightServiceTest {

    private static List<Map<String, Object>> weightList;
    private static WeightEntity weightEntity;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static String field;
    private static Double value;

    @Mock
    private WeightDao weightDao;

    @InjectMocks
    private WeightService service = new WeightServiceImpl();

    @BeforeAll
    public static void setUp() {
        weightList = new ArrayList<>();
        weightEntity = new WeightEntity();
        date = "2020-02-13T10:30:00";
        date2 = "2021-02-13T10:30:00";
        owner = "TupoJohn";
        petName = "Perico";
        field = "weightValue";
        value = 5.3;
    }

    @Test
    public void shouldReturnNothingWhenWeightCreated() {
        service.createWeight(owner, petName, date, weightEntity);
        verify(weightDao).createWeight(isA(String.class), isA(String.class), isA(String.class), isA(WeightEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenWeightDeleted() throws DatabaseAccessException {
        service.deleteWeightByDate(owner, petName, date);
        verify(weightDao).deleteWeightByDate(isA(String.class), isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllWeightsDeleted() throws DatabaseAccessException {
        service.deleteAllWeights(owner, petName);
        verify(weightDao).deleteAllWeights(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenAllWeightDeleteFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(weightDao).deleteAllWeights(any(String.class), isA(String.class));
            service.deleteAllWeights(owner, petName);
        }, "Should return a database access exception when a weight deletion fails");
    }

    @Test
    public void shouldReturnMealEntityWhenWeightRetrieved() throws DatabaseAccessException {
        when(weightDao.getWeightByDate(owner, petName, date)).thenReturn(weightEntity);
        WeightEntity meal = service.getWeightByDate(owner, petName, date);
        assertSame(weightEntity, meal, "Should return a weight entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetWeightRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(weightDao).getWeightByDate(any(String.class), any(String.class),
                isA(String.class));
            service.getWeightByDate(owner, petName, date);
        }, "Should return an exception when retrieving a weight fails");
    }

    @Test
    public void shouldReturnMealEntityListWhenGetSetOfWeightsRetrieved() throws DatabaseAccessException {
        when(weightDao.getAllWeight(owner, petName)).thenReturn(weightList);
        List<Map<String, Object>> list = service.getAllWeight(owner, petName);
        assertSame(weightList, list, "Should return a list of weights entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfWeightsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(weightDao).getAllWeight(any(String.class), isA(String.class));
            service.getAllWeight(owner, petName);
        }, "Should return an exception when retrieving a set of weights fails");
    }

    @Test
    public void shouldReturnWeightEntityListWhenGetWeightsBetweenDatesRetrieved() throws DatabaseAccessException {
        when(weightDao.getAllWeightsBetween(owner, petName, date, date2)).thenReturn(weightList);
        List<Map<String, Object>> list = service.getAllWeightsBetween(owner, petName, date, date2);
        assertSame(weightList, list, "Should return a list of weight entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetWeightsBetweenDatesRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(weightDao).getAllWeightsBetween(any(String.class),
                isA(String.class), isA(String.class), isA(String.class));
            service.getAllWeightsBetween(owner, petName, date, date2);
        }, "Should return an exception when retrieving a set of weights between dates fails");
    }

    @Test
    public void shouldReturnNothingWhenWeightFieldUpdated() throws DatabaseAccessException {
        service.updateWeight(owner, petName, date, value);
        verify(weightDao).updateWeight(isA(String.class), isA(String.class), isA(String.class),
            isA(Double.class));
    }
}
