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
import org.pesmypetcare.webservice.entity.WeightEntity;
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
public class WeightDaoTest {
    private static List<Map<String, Object>> weightList;
    private static WeightEntity weightEntity;
    private static String date;
    private static String date2;
    private static String owner;
    private static String petName;
    private static Double value;
    private static String field;
    private static final String USERS_KEY = "users";
    private static final String PETS_KEY = "pets";
    private static final String WEIGHTS_KEY = "weights";
    private final String EXCECUTION_EXC_MSG = "Should throw DatabaseAccessException when ExecutionException received";
    private final String INTERRUPTED_EXC_MSG = "Should throw DatabaseAccessException when InterruptedException "
        + "received";
    private final String DOCUMENT_NOT_EXISTS_EXC_MSG = "Should throw DatabaseAccessException when Weight document"
        + " doesn't exist";

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
    private CollectionReference weightsRef;
    @Mock
    private DocumentReference weightRef;
    @Mock
    private ApiFuture<QuerySnapshot> futureQuery;
    @Mock
    private QuerySnapshot querySnapshot;
    @Mock
    private ApiFuture<DocumentSnapshot> futureDocument;
    @Mock
    private DocumentSnapshot documentSnapshot;
    @Mock
    private List<QueryDocumentSnapshot> weightsDocuments;
    @Mock
    private Iterator<QueryDocumentSnapshot> it;

    @InjectMocks
    private WeightDao weightDao = new WeightDaoImpl();

    @BeforeAll
    public static void setUp() {
        weightList = new ArrayList<>();
        weightEntity = new WeightEntity();
        date = "2019-10-22T00:47:00";
        date2 = "2021-10-22T00:47:00";
        owner = "PericoDeLosPalotes";
        petName = "TupoJohn";
        field = "weightValue";
        value = 9.0 / 2.0;
    }

    @Test
    public void shouldCreateWeightOnDatabaseWhenRequested() {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(weightsRef);
        given(weightsRef.document(anyString())).willReturn(weightRef);
        given(weightRef.set(isA(WeightEntity.class))).willReturn(null);

        weightDao.createWeight(owner, petName, date, weightEntity);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(WEIGHTS_KEY));
        verify(weightsRef).document(same(date));
        verify(weightRef).set(same(weightEntity));
    }

    @Test
    public void shouldDeleteWeightOnDatabaseWhenRequested() throws DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(weightsRef);
        given(weightsRef.document(anyString())).willReturn(weightRef);
        given(weightRef.delete()).willReturn(null);

        weightDao.deleteWeightByDate(owner, petName, date);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(WEIGHTS_KEY));
        verify(weightsRef).document(same(date));
        verify(weightRef).delete();
    }

    @Test
    public void shouldDeleteAllWeightsOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
        InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(weightsRef);
        given(weightsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(weightsDocuments);
        given(weightsDocuments.iterator()).willReturn(it);

        weightDao.deleteAllWeights(owner, petName);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(WEIGHTS_KEY));
        verify(weightsRef).get();
        verify(futureQuery).get();
        verify(querySnapshot).getDocuments();
        verify(weightsDocuments).iterator();
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(weightsRef);
            given(weightsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            weightDao.deleteAllWeights(owner, petName);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(weightsRef);
            given(weightsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            weightDao.deleteAllWeights(owner, petName);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnWeightEntityFromDatabaseWhenRequested() throws ExecutionException, InterruptedException,
        DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(weightsRef);
        given(weightsRef.document(anyString())).willReturn(weightRef);
        given(weightRef.get()).willReturn(futureDocument);
        given(futureDocument.get()).willReturn(documentSnapshot);
        given(documentSnapshot.exists()).willReturn(true);
        given(documentSnapshot.toObject(WeightEntity.class)).willReturn(weightEntity);

        WeightEntity meal = weightDao.getWeightByDate(owner, petName, date);

        assertSame(weightEntity, meal, "Should return Meal Entity");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenWeightDocumentNotExists() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(weightsRef);
            given(weightsRef.document(anyString())).willReturn(weightRef);
            given(weightRef.get()).willReturn(futureDocument);
            given(futureDocument.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(false);

            weightDao.getWeightByDate(owner, petName, date);
        }, DOCUMENT_NOT_EXISTS_EXC_MSG);
    }


    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveWeightDocumentReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(weightsRef);
            given(weightsRef.document(anyString())).willReturn(weightRef);
            given(weightRef.get()).willReturn(futureDocument);
            willThrow(InterruptedException.class).given(futureDocument).get();

            weightDao.getWeightByDate(owner, petName, date);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveWeightDocumentReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(weightsRef);
            given(weightsRef.document(anyString())).willReturn(weightRef);
            given(weightRef.get()).willReturn(futureDocument);
            willThrow(ExecutionException.class).given(futureDocument).get();


            weightDao.getWeightByDate(owner, petName, date);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnAllWeightsDataOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
        InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(weightsRef);
        given(weightsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(weightsDocuments);
        given(weightsDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<Map<String, Object>> list = weightDao.getAllWeight(owner, petName);
        assertEquals(weightList, list, "Should return a list containing all weights data");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllWeightsDataFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(weightsRef);
            given(weightsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            weightDao.getAllWeight(owner, petName);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllWeightsDataFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(weightsRef);
            given(weightsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            weightDao.getAllWeight(owner, petName);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnAllWeightsBetweenDatesOnDatabaseWhenRequested() throws DatabaseAccessException,
        ExecutionException, InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(weightsRef);
        given(weightsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(weightsDocuments);
        given(weightsDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<Map<String, Object>> list = weightDao.getAllWeightsBetween(owner, petName, date, date2);

        assertEquals(weightList, list, "Should return a list containing all weights between two dates");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllWeightsBetweenFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(weightsRef);
            given(weightsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            weightDao.getAllWeightsBetween(owner, petName, date, date2);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllWeightsBetweenFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(weightsRef);
            given(weightsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            weightDao.getAllWeightsBetween(owner, petName, date, date2);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldUpdateWhenRequested() throws DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(weightsRef);
        given(weightsRef.document(anyString())).willReturn(weightRef);
        given(weightRef.update(anyString(), any())).willReturn(null);

        weightDao.updateWeight(owner, petName, date, value);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(WEIGHTS_KEY));
        verify(weightsRef).document(same(date));
        verify(weightRef).update(same(field), same(value));
    }
}
