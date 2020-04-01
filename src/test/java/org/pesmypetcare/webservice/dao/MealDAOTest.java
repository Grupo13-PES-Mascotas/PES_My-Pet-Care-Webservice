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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MealDAOTest {
    private static final String USERS_KEY = "users";
    private static final String PETS_KEY = "pets";
    private static final String MEALS_KEY = "meals";
    private static MealEntity mealEntity;
    private static String date;
    private static String owner;
    private static String petName;
    private static String field;
    private static Double value;

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
    private List<QueryDocumentSnapshot> petsDocuments;
    @Mock
    private Iterator<QueryDocumentSnapshot> it;

    @InjectMocks
    private MealDAO mealDAO = new MealDAOImpl();


    @BeforeAll
    public static void setUp() {
        mealEntity = new MealEntity();
        date="2020-02-13T10:30:00";
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

        mealDAO.createMeal(owner, petName, date, mealEntity);

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

        mealDAO.deleteByDate(owner, petName, date);

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
        given(querySnapshot.getDocuments()).willReturn(petsDocuments);
        given(petsDocuments.iterator()).willReturn(it);

        mealDAO.deleteAllMeals(owner, petName);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(MEALS_KEY));
        verify(mealsRef).get();
        verify(futureQuery).get();
        verify(querySnapshot).getDocuments();
        verify(petsDocuments).iterator();
    }
}
