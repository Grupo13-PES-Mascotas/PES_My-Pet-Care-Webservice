package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.MealEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MealDAOTest {
    private static List<Map<String, Object>> mealList;
    private static MealEntity mealEntity;
    private static String date;
    private static String date2;
    private static String owner;
    private static String petName;
    private static String field;
    private static Double value;
    private static final String USERS_KEY = "users";
    private static final String PETS_KEY = "pets";
    private static final String MEALS_KEY = "meals";
    private final String EXCECUTION_EXC_MSG = "Should throw DatabaseAccessException when ExecutionException received";
    private final String INTERRUPTED_EXC_MSG = "Should throw DatabaseAccessException when InterruptedException received";
    private final String DOCUMENT_NOT_EXISTS_EXC_MSG;


    @Mock
    private Firestore db;
    @Mock
    private CollectionReference usersRef;
    @Mock
    private DocumentReference ownerRef;
    @Mock
    private CollectionReference petsRef;
    @Mock
    private DocumentReference petRef;
    @Mock
    private CollectionReference mealsRef;
    @Mock
    private DocumentReference mealRef;
    @Mock
    private ApiFuture<QuerySnapshot> futureQuery;
    @Mock
    private QuerySnapshot querySnapshot;
    @Mock
    private ApiFuture<DocumentSnapshot> futureDocument;
    @Mock
    private DocumentSnapshot documentSnapshot;
    @Mock
    private List<QueryDocumentSnapshot> mealsDocuments;
    @Mock
    private Iterator<QueryDocumentSnapshot> it;

    @InjectMocks
    private MealDAO mealDao = new MealDAOImpl();

    public MealDAOTest() {
        DOCUMENT_NOT_EXISTS_EXC_MSG = "Should throw DatabaseAccessException when Meal document doesn't exist";
    }


    @BeforeAll
    public static void setUp() {
        mealList = new ArrayList<>();
        mealEntity = new MealEntity();
        date = "2020-02-13T10:30:00";
        date2 = "2021-02-13T10:30:00";
        owner = "Pepe05";
        petName = "Camper";
        field = "kcal";
        value = 60.0;
    }

    @Test
    public void shouldCreateMealOnDatabaseWhenRequested() {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(mealsRef);
        given(mealsRef.document(anyString())).willReturn(mealRef);
        given(mealRef.set(isA(MealEntity.class))).willReturn(null);

        mealDao.createMeal(owner, petName, date, mealEntity);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(MEALS_KEY));
        verify(mealsRef).document(same(date));
        verify(mealRef).set(same(mealEntity));
    }

    @Test
    public void shouldDeleteMealOnDatabaseWhenRequested() {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(mealsRef);
        given(mealsRef.document(anyString())).willReturn(mealRef);
        given(mealRef.delete()).willReturn(null);

        mealDao.deleteByDate(owner, petName, date);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(MEALS_KEY));
        verify(mealsRef).document(same(date));
        verify(mealRef).delete();
    }

    @Test
    public void shouldDeleteAllMealsOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
        InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(mealsRef);
        given(mealsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(mealsDocuments);
        given(mealsDocuments.iterator()).willReturn(it);

        mealDao.deleteAllMeals(owner, petName);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(MEALS_KEY));
        verify(mealsRef).get();
        verify(futureQuery).get();
        verify(querySnapshot).getDocuments();
        verify(mealsDocuments).iterator();
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            mealDao.deleteAllMeals(owner, petName);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            mealDao.deleteAllMeals(owner, petName);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnMealEntityFromDatabaseWhenRequested() throws ExecutionException, InterruptedException,
        DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(mealsRef);
        given(mealsRef.document(anyString())).willReturn(mealRef);
        given(mealRef.get()).willReturn(futureDocument);
        given(futureDocument.get()).willReturn(documentSnapshot);
        given(documentSnapshot.exists()).willReturn(true);
        given(documentSnapshot.toObject(MealEntity.class)).willReturn(mealEntity);

        MealEntity meal = mealDao.getMealData(owner, petName, date);

        assertSame(mealEntity, meal, "Should return Meal Entity");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenMealDocumentNotExists() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.document(anyString())).willReturn(mealRef);
            given(mealRef.get()).willReturn(futureDocument);
            given(futureDocument.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(false);

            mealDao.getMealData(owner, petName, date);
        }, DOCUMENT_NOT_EXISTS_EXC_MSG);
    }


    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveMealDocumentReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.document(anyString())).willReturn(mealRef);
            given(mealRef.get()).willReturn(futureDocument);
            willThrow(InterruptedException.class).given(futureDocument).get();

            mealDao.getMealData(owner, petName, date);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveMealDocumentReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.document(anyString())).willReturn(mealRef);
            given(mealRef.get()).willReturn(futureDocument);
            willThrow(ExecutionException.class).given(futureDocument).get();


            mealDao.getMealData(owner, petName, date);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnAllMealsDataOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
        InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(mealsRef);
        given(mealsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(mealsDocuments);
        given(mealsDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<Map<String, Object>> list = mealDao.getAllMealData(owner, petName);

        assertEquals(mealList, list, "Should return a List containing all meals Data");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllMealsDataFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            mealDao.getAllMealData(owner, petName);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllMealsDataFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            mealDao.getAllMealData(owner, petName);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnAllMealsBetweenDatesOnDatabaseWhenRequested() throws DatabaseAccessException,
        ExecutionException, InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(mealsRef);
        given(mealsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(mealsDocuments);
        given(mealsDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<Map<String, Object>> list = mealDao.getAllMealsBetween(owner, petName, date, date2);

        assertEquals(mealList, list, "Should return a List containing all meals between two dates");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllMealsBetweenDatesFromDatabaseReceivesInterruptedException()
    {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            mealDao.getAllMealsBetween(owner, petName, date, date2);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllMealsBetweenDatesFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            mealDao.getAllMealsBetween(owner, petName, date, date2);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnMealFieldFromDatabaseWhenRequested() throws ExecutionException, InterruptedException,
        DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(mealsRef);
        given(mealsRef.document(anyString())).willReturn(mealRef);
        given(mealRef.get()).willReturn(futureDocument);
        given(futureDocument.get()).willReturn(documentSnapshot);
        given(documentSnapshot.exists()).willReturn(true);
        given(documentSnapshot.get(anyString())).willReturn(value);

        Object mealValue = mealDao.getMealField(owner, petName, date, field);

        assertSame(value, mealValue, "Should return field value");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenMealDocumentNotExistsInFieldRetrieval() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.document(anyString())).willReturn(mealRef);
            given(mealRef.get()).willReturn(futureDocument);
            given(futureDocument.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(false);

            mealDao.getMealField(owner, petName, date, field);
        }, DOCUMENT_NOT_EXISTS_EXC_MSG);
    }


    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveMealFieldReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.document(anyString())).willReturn(mealRef);
            given(mealRef.get()).willReturn(futureDocument);
            willThrow(InterruptedException.class).given(futureDocument).get();

            mealDao.getMealField(owner, petName, date, field);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveMealFieldReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(mealsRef);
            given(mealsRef.document(anyString())).willReturn(mealRef);
            given(mealRef.get()).willReturn(futureDocument);
            willThrow(ExecutionException.class).given(futureDocument).get();

            mealDao.getMealField(owner, petName, date, field);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldUpdateFieldWhenRequested() {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(mealsRef);
        given(mealsRef.document(anyString())).willReturn(mealRef);
        given(mealRef.update(anyString(), any())).willReturn(null);

        mealDao.updateMealField(owner, petName, date, field, value);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(MEALS_KEY));
        verify(mealsRef).document(same(date));
        verify(mealRef).update(same(field), same(value));
    }
}
