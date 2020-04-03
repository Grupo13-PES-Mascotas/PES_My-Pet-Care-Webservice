package org.pesmypetcare.webservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.MealDao;
import org.pesmypetcare.webservice.entity.MealEntity;
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
public class MealServiceTest {
    private static List<Map<String, Object>> mealList;
    private static MealEntity mealEntity;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static String field;
    private static String value;

    @Mock
    private MealDao mealDao;

    @InjectMocks
    private MealService service = new MealServiceImpl();

    @BeforeAll
    public static void setUp() {
        mealList = new ArrayList<>();
        mealEntity = new MealEntity();
        date = "2020-02-13T10:30:00";
        date2 = "2021-02-13T10:30:00";
        owner = "Pepe05";
        petName = "Camper";
        field = "mealName";
        value = "Asparagus";
    }

    @Test
    public void shouldReturnNothingWhenMealCreated() {
        service.createMeal(owner, petName, date, mealEntity);
        verify(mealDao).createMeal(isA(String.class), isA(String.class), isA(String.class), isA(MealEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenMealDeleted() {
        service.deleteByDate(owner, petName, date);
        verify(mealDao).deleteByDate(isA(String.class), isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllMealsDeleted() throws DatabaseAccessException {
        service.deleteAllMeals(owner, petName);
        verify(mealDao).deleteAllMeals(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenAllMealDeleteFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(mealDao).deleteAllMeals(any(String.class), isA(String.class));
            service.deleteAllMeals(owner, petName);
        }, "Should return a database access exception when a meal deletion fails");
    }

    @Test
    public void shouldReturnMealEntityWhenMealRetrieved() throws DatabaseAccessException {
        when(mealDao.getMealData(owner, petName, date)).thenReturn(mealEntity);
        MealEntity meal = service.getMealData(owner, petName, date);
        assertSame(mealEntity, meal, "Should return a meal entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetMealRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(mealDao).getMealData(any(String.class), any(String.class), isA(String.class));
            service.getMealData(owner, petName, date);
        }, "Should return an exception when retrieving a meal fails");
    }

    @Test
    public void shouldReturnMealEntityListWhenGetSetOfMealsRetrieved() throws DatabaseAccessException {
        when(mealDao.getAllMealData(owner, petName)).thenReturn(mealList);
        List<Map<String, Object>> list = service.getAllMealData(owner, petName);
        assertSame(mealList, list, "Should return a list of meal entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfMealsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(mealDao).getAllMealData(any(String.class), isA(String.class));
            service.getAllMealData(owner, petName);
        }, "Should return an exception when retrieving a set of meals fails");
    }

    @Test
    public void shouldReturnMealEntityListWhenGetMealsBetweenDatesRetrieved() throws DatabaseAccessException {
        when(mealDao.getAllMealsBetween(owner, petName, date, date2)).thenReturn(mealList);
        List<Map<String, Object>> list = service.getAllMealsBetween(owner, petName, date, date2);
        assertSame(mealList, list, "Should return a list of meal entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetMealsBetweenDatesRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(mealDao).getAllMealsBetween(any(String.class),
                isA(String.class), isA(String.class), isA(String.class));
            service.getAllMealsBetween(owner, petName, date, date2);
        }, "Should return an exception when retrieving a set of meals fails");
    }

        @Test
    public void shouldReturnMealFieldWhenMealFieldRetrieved() throws DatabaseAccessException {
        when(mealDao.getMealField(owner, petName, date, field)).thenReturn(value);
        Object obtainedValue = service.getMealField(owner, petName, date, field);
        assertSame(value, obtainedValue, "Should return an Object");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetMealFieldRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(mealDao).getMealField(any(String.class), any(String.class),
                isA(String.class), isA(String.class));
            service.getMealField(owner, petName, date, field);
        }, "Should return an exception when retrieving a meal field fails");
    }

    @Test
    public void shouldReturnNothingWhenMealFieldUpdated() {
        service.updateMealField(owner, petName, date, field, value);
        verify(mealDao).updateMealField(isA(String.class), isA(String.class), isA(String.class), isA(String.class), isA(Object.class));
    }

}
