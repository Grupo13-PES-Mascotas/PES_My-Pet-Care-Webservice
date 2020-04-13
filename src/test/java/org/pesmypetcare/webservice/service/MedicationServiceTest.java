package org.pesmypetcare.webservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.MedicationDao;
import org.pesmypetcare.webservice.entity.MedicationEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicationServiceTest {
    private static List<Map<List<String>, Object>> MedicationList;
    private static MedicationEntity MedicationEntity;
    private static String owner;
    private static String petName;
    private static String dateName;
    private static String date;
    private static String date2;
    private static String field;
    private static int value;

    @Mock
    private MedicationDao MedicationDao;

    @InjectMocks
    private MedicationService service = new MedicationServiceImp();

    @BeforeAll
    public static void setUp() {
        MedicationList = new ArrayList<>();
        MedicationEntity = new MedicationEntity();
        dateName = "2020-02-13/Cloroform";
        date = "2020-02-13";
        date2 = "2021-02-13";
        owner = "Pepe05";
        petName = "Camper";
        field = "quantity";
        value = 3;
    }

    @Test
    public void shouldReturnNothingWhenMedicationCreated() {
        service.createMedication(owner, petName, dateName, MedicationEntity);
        verify(MedicationDao).createMedication(isA(String.class), isA(String.class), isA(String.class),
                isA(MedicationEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenMedicationDeleted() {
        service.deleteByDateAndName(owner, petName, dateName);
        verify(MedicationDao).deleteByDateAndName(isA(String.class), isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllMedicationsDeleted() throws DatabaseAccessException {
        service.deleteAllMedications(owner, petName);
        verify(MedicationDao).deleteAllMedications(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenAllMedicationDeleteFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(MedicationDao).deleteAllMedications(any(String.class),
                    isA(String.class));
            service.deleteAllMedications(owner, petName);
        }, "Should return a database access exception when a Medication deletion fails");
    }

    @Test
    public void shouldReturnMedicationEntityWhenMedicationRetrieved() throws DatabaseAccessException {
        when(MedicationDao.getMedicationData(owner, petName, dateName)).thenReturn(MedicationEntity);
        MedicationEntity Medication = service.getMedicationData(owner, petName, dateName);
        assertSame(MedicationEntity, Medication, "Should return a Medication entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetMedicationRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(MedicationDao).getMedicationData(any(String.class),
                    any(String.class), isA(String.class));
            service.getMedicationData(owner, petName, dateName);
        }, "Should return an exception when retrieving a Medication fails");
    }

    @Test
    public void shouldReturnMedicationEntityListWhenGetSetOfMedicationsRetrieved() throws DatabaseAccessException,
            ExecutionException, InterruptedException {
        when(MedicationDao.getAllMedicationData(owner, petName)).thenReturn(MedicationList);
        List<Map<List<String>, Object>> list = service.getAllMedicationData(owner, petName);
        assertSame(MedicationList, list, "Should return a list of Medication entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfMedicationsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(MedicationDao).getAllMedicationData(any(String.class), isA(String.class));
            service.getAllMedicationData(owner, petName);
        }, "Should return an exception when retrieving a set of Medications fails");
    }

    @Test
    public void shouldReturnMedicationEntityListWhenGetMedicationsBetweenDatesRetrieved() throws DatabaseAccessException,
            ExecutionException, InterruptedException {
        when(MedicationDao.getAllMedicationsBetween(owner, petName, date, date2)).thenReturn(MedicationList);
        List<Map<List<String>, Object>> list = service.getAllMedicationsBetween(owner, petName, date, date2);
        assertSame(MedicationList, list, "Should return a list of Medication entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetMedicationsBetweenDatesRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(MedicationDao).getAllMedicationsBetween(any(String.class),
                    isA(String.class), isA(String.class), isA(String.class));
            service.getAllMedicationsBetween(owner, petName, date, date2);
        }, "Should return an exception when retrieving a set of Medications between dates fails");
    }

    @Test
    public void shouldReturnMedicationFieldWhenMedicationFieldRetrieved() throws DatabaseAccessException {
        when(MedicationDao.getMedicationField(owner, petName, date, field)).thenReturn(value);
        Object obtainedValue = service.getMedicationField(owner, petName, date, field);
        assertSame(value, obtainedValue, "Should return an Object");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetMedicationFieldRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(MedicationDao).getMedicationField(any(String.class), any(String.class),
                    isA(String.class), isA(String.class));
            service.getMedicationField(owner, petName, date, field);
        }, "Should return an exception when retrieving a Medication field fails");
    }

    @Test
    public void shouldReturnNothingWhenMedicationFieldUpdated() {
        service.updateMedicationField(owner, petName, date, field, value);
        verify(MedicationDao).updateMedicationField(isA(String.class), isA(String.class), isA(String.class), isA(String.class),
                isA(Object.class));
    }

}