package org.pesmypetcare.webservice.service.petmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.petmanager.FreqTrainingDao;
import org.pesmypetcare.webservice.entity.petmanager.FreqTrainingEntity;
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
public class FreqTrainingServiceTest {

    private static List<Map<String, Object>> freqTrainingList;
    private static FreqTrainingEntity freqTrainingEntity;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static Double value;

    @Mock
    private FreqTrainingDao freqTrainingDao;

    @InjectMocks
    private FreqTrainingService service = new FreqTrainingServiceImpl();

    @BeforeAll
    public static void setUp() {
        freqTrainingList = new ArrayList<>();
        freqTrainingEntity = new FreqTrainingEntity();
        date = "2020-02-13T10:30:00";
        date2 = "2021-02-13T10:30:00";
        owner = "TupoJohn";
        petName = "Perico";
        value = 2.0;
    }

    @Test
    public void shouldReturnNothingWhenFreqTrainingCreated() {
        service.createFreqTraining(owner, petName, date, freqTrainingEntity);
        verify(freqTrainingDao).createFreqTraining(isA(String.class), isA(String.class), isA(String.class),
            isA(FreqTrainingEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenFreqTrainingDeleted() throws DatabaseAccessException {
        service.deleteFreqTrainingByDate(owner, petName, date);
        verify(freqTrainingDao).deleteFreqTrainingByDate(isA(String.class), isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllFreqTrainingsDeleted() throws DatabaseAccessException {
        service.deleteAllFreqTrainings(owner, petName);
        verify(freqTrainingDao).deleteAllFreqTrainings(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenAllFreqTrainingDeleteFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(freqTrainingDao).deleteAllFreqTrainings(any(String.class),
                isA(String.class));
            service.deleteAllFreqTrainings(owner, petName);
        }, "Should return a database access exception when a freqTraining deletion fails");
    }

    @Test
    public void shouldReturnMealEntityWhenFreqTrainingRetrieved() throws DatabaseAccessException {
        when(freqTrainingDao.getFreqTrainingByDate(owner, petName, date)).thenReturn(freqTrainingEntity);
        FreqTrainingEntity meal = service.getFreqTrainingByDate(owner, petName, date);
        assertSame(freqTrainingEntity, meal, "Should return a freqTraining entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetFreqTrainingRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(freqTrainingDao).getFreqTrainingByDate(any(String.class),
                any(String.class), isA(String.class));
            service.getFreqTrainingByDate(owner, petName, date);
        }, "Should return an exception when retrieving a freqTraining fails");
    }

    @Test
    public void shouldReturnMealEntityListWhenGetSetOfFreqTrainingsRetrieved() throws DatabaseAccessException {
        when(freqTrainingDao.getAllFreqTraining(owner, petName)).thenReturn(freqTrainingList);
        List<Map<String, Object>> list = service.getAllFreqTraining(owner, petName);
        assertSame(freqTrainingList, list, "Should return a list of freqTrainings entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfFreqTrainingsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(freqTrainingDao).getAllFreqTraining(any(String.class),
                isA(String.class));
            service.getAllFreqTraining(owner, petName);
        }, "Should return an exception when retrieving a set of freqTrainings fails");
    }

    @Test
    public void shouldReturnFreqTrainingEntityListWhenGetFreqTrainingsBetweenDatesRetrieved()
        throws DatabaseAccessException {
        when(freqTrainingDao.getAllFreqTrainingsBetween(owner, petName, date, date2)).thenReturn(freqTrainingList);
        List<Map<String, Object>> list = service.getAllFreqTrainingsBetween(owner, petName, date, date2);
        assertSame(freqTrainingList, list, "Should return a list of freqTraining entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetFreqTrainingsBetweenDatesRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(freqTrainingDao).getAllFreqTrainingsBetween(any(String.class),
                isA(String.class), isA(String.class), isA(String.class));
            service.getAllFreqTrainingsBetween(owner, petName, date, date2);
        }, "Should return an exception when retrieving a set of freqTrainings between dates fails");
    }

    @Test
    public void shouldReturnNothingWhenFreqTrainingFieldUpdated() throws DatabaseAccessException {
        service.updateFreqTraining(owner, petName, date, value);
        verify(freqTrainingDao).updateFreqTraining(isA(String.class), isA(String.class), isA(String.class),
            isA(Double.class));
    }
}
