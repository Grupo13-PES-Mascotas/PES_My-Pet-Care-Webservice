package org.pesmypetcare.webservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.WeekTrainingDao;
import org.pesmypetcare.webservice.entity.WeekTrainingEntity;
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
public class WeekTrainingServiceTest {

    private static List<Map<String, Object>> weekTrainingList;
    private static WeekTrainingEntity weekTrainingEntity;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static Double value;

    @Mock
    private WeekTrainingDao weekTrainingDao;

    @InjectMocks
    private WeekTrainingService service = new WeekTrainingServiceImpl();

    @BeforeAll
    public static void setUp() {
        weekTrainingList = new ArrayList<>();
        weekTrainingEntity = new WeekTrainingEntity();
        date = "2020-02-13T10:30:00";
        date2 = "2021-02-13T10:30:00";
        owner = "TupoJohn";
        petName = "Perico";
        value = 2.0;
    }

    @Test
    public void shouldReturnNothingWhenWeekTrainingCreated() {
        service.createWeekTraining(owner, petName, date, weekTrainingEntity);
        verify(weekTrainingDao).createWeekTraining(isA(String.class), isA(String.class), isA(String.class),
            isA(WeekTrainingEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenWeekTrainingDeleted() throws DatabaseAccessException {
        service.deleteWeekTrainingByDate(owner, petName, date);
        verify(weekTrainingDao).deleteWeekTrainingByDate(isA(String.class), isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllWeekTrainingsDeleted() throws DatabaseAccessException {
        service.deleteAllWeekTrainings(owner, petName);
        verify(weekTrainingDao).deleteAllWeekTrainings(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenAllWeekTrainingDeleteFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(weekTrainingDao).deleteAllWeekTrainings(any(String.class),
                isA(String.class));
            service.deleteAllWeekTrainings(owner, petName);
        }, "Should return a database access exception when a weekTraining deletion fails");
    }

    @Test
    public void shouldReturnMealEntityWhenWeekTrainingRetrieved() throws DatabaseAccessException {
        when(weekTrainingDao.getWeekTrainingByDate(owner, petName, date)).thenReturn(weekTrainingEntity);
        WeekTrainingEntity meal = service.getWeekTrainingByDate(owner, petName, date);
        assertSame(weekTrainingEntity, meal, "Should return a weekTraining entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetWeekTrainingRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(weekTrainingDao).getWeekTrainingByDate(any(String.class),
                any(String.class), isA(String.class));
            service.getWeekTrainingByDate(owner, petName, date);
        }, "Should return an exception when retrieving a weekTraining fails");
    }

    @Test
    public void shouldReturnMealEntityListWhenGetSetOfWeekTrainingsRetrieved() throws DatabaseAccessException {
        when(weekTrainingDao.getAllWeekTraining(owner, petName)).thenReturn(weekTrainingList);
        List<Map<String, Object>> list = service.getAllWeekTraining(owner, petName);
        assertSame(weekTrainingList, list, "Should return a list of weekTrainings entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfWeekTrainingsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(weekTrainingDao).getAllWeekTraining(any(String.class),
                isA(String.class));
            service.getAllWeekTraining(owner, petName);
        }, "Should return an exception when retrieving a set of weekTrainings fails");
    }

    @Test
    public void shouldReturnWeekTrainingEntityListWhenGetWeekTrainingsBetweenDatesRetrieved()
        throws DatabaseAccessException {
        when(weekTrainingDao.getAllWeekTrainingsBetween(owner, petName, date, date2)).thenReturn(weekTrainingList);
        List<Map<String, Object>> list = service.getAllWeekTrainingsBetween(owner, petName, date, date2);
        assertSame(weekTrainingList, list, "Should return a list of weekTraining entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetWeekTrainingsBetweenDatesRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(weekTrainingDao).getAllWeekTrainingsBetween(any(String.class),
                isA(String.class), isA(String.class), isA(String.class));
            service.getAllWeekTrainingsBetween(owner, petName, date, date2);
        }, "Should return an exception when retrieving a set of weekTrainings between dates fails");
    }

    @Test
    public void shouldReturnNothingWhenWeekTrainingFieldUpdated() throws DatabaseAccessException {
        service.updateWeekTraining(owner, petName, date, value);
        verify(weekTrainingDao).updateWeekTraining(isA(String.class), isA(String.class), isA(String.class),
            isA(Double.class));
    }
}
