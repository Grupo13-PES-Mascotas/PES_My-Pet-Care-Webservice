package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.KcalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KcalDaoTest {
    private static List<Map<String, Object>> kcalList;
    private static KcalEntity kcalEntity;
    private static String date;
    private static String date2;
    private static String owner;
    private static String petName;
    private static Double value;
    private static String field;
    private static final String USERS_KEY = "users";
    private static final String PETS_KEY = "pets";
    private static final String KCALS_KEY = "kcals";
    private final String EXCECUTION_EXC_MSG = "Should throw DatabaseAccessException when ExecutionException received";
    private final String INTERRUPTED_EXC_MSG = "Should throw DatabaseAccessException when InterruptedException "
        + "received";
    private final String DOCUMENT_NOT_EXISTS_EXC_MSG = "Should throw DatabaseAccessException when Kcal document"
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
    private CollectionReference kcalsRef;
    @Mock
    private DocumentReference kcalRef;
    @Mock
    private ApiFuture<QuerySnapshot> futureQuery;
    @Mock
    private QuerySnapshot querySnapshot;
    @Mock
    private ApiFuture<DocumentSnapshot> futureDocument;
    @Mock
    private DocumentSnapshot documentSnapshot;
    @Mock
    private List<QueryDocumentSnapshot> kcalsDocuments;
    @Mock
    private Iterator<QueryDocumentSnapshot> it;

    @InjectMocks
    private KcalDao kcalDao = new KcalDaoImpl();

    @BeforeAll
    public static void setUp() {
        kcalList = new ArrayList<>();
        kcalEntity = new KcalEntity();
        date = "2019-10-22T00:47:00";
        date2 = "2021-10-22T00:47:00";
        owner = "PericoDeLosPalotes";
        petName = "TupoJohn";
        field = "kcalValue";
        value = 9.0 / 2.0;
    }

    @Test
    public void shouldCreateKcalOnDatabaseWhenRequested() {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalsRef);
        given(kcalsRef.document(anyString())).willReturn(kcalRef);
        given(kcalRef.set(isA(KcalEntity.class))).willReturn(null);

        kcalDao.createKcal(owner, petName, date, kcalEntity);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(KCALS_KEY));
        verify(kcalsRef).document(same(date));
        verify(kcalRef).set(same(kcalEntity));
    }

    @Test
    public void shouldDeleteKcalOnDatabaseWhenRequested() throws DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalsRef);
        given(kcalsRef.document(anyString())).willReturn(kcalRef);
        given(kcalRef.delete()).willReturn(null);

        kcalDao.deleteKcalByDate(owner, petName, date);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(KCALS_KEY));
        verify(kcalsRef).document(same(date));
        verify(kcalRef).delete();
    }

    @Test
    public void shouldDeleteAllKcalsOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
        InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalsRef);
        given(kcalsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(kcalsDocuments);
        given(kcalsDocuments.iterator()).willReturn(it);

        kcalDao.deleteAllKcals(owner, petName);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(KCALS_KEY));
        verify(kcalsRef).get();
        verify(futureQuery).get();
        verify(querySnapshot).getDocuments();
        verify(kcalsDocuments).iterator();
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalsRef);
            given(kcalsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            kcalDao.deleteAllKcals(owner, petName);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalsRef);
            given(kcalsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            kcalDao.deleteAllKcals(owner, petName);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnKcalEntityFromDatabaseWhenRequested() throws ExecutionException, InterruptedException,
        DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalsRef);
        given(kcalsRef.document(anyString())).willReturn(kcalRef);
        given(kcalRef.get()).willReturn(futureDocument);
        given(futureDocument.get()).willReturn(documentSnapshot);
        given(documentSnapshot.exists()).willReturn(true);
        given(documentSnapshot.toObject(KcalEntity.class)).willReturn(kcalEntity);

        KcalEntity meal = kcalDao.getKcalByDate(owner, petName, date);

        assertSame(kcalEntity, meal, "Should return Meal Entity");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenKcalDocumentNotExists() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalsRef);
            given(kcalsRef.document(anyString())).willReturn(kcalRef);
            given(kcalRef.get()).willReturn(futureDocument);
            given(futureDocument.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(false);

            kcalDao.getKcalByDate(owner, petName, date);
        }, DOCUMENT_NOT_EXISTS_EXC_MSG);
    }


    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveKcalDocumentReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalsRef);
            given(kcalsRef.document(anyString())).willReturn(kcalRef);
            given(kcalRef.get()).willReturn(futureDocument);
            willThrow(InterruptedException.class).given(futureDocument).get();

            kcalDao.getKcalByDate(owner, petName, date);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveKcalDocumentReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalsRef);
            given(kcalsRef.document(anyString())).willReturn(kcalRef);
            given(kcalRef.get()).willReturn(futureDocument);
            willThrow(ExecutionException.class).given(futureDocument).get();


            kcalDao.getKcalByDate(owner, petName, date);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnAllKcalsDataOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
        InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalsRef);
        given(kcalsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(kcalsDocuments);
        given(kcalsDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<Map<String, Object>> list = kcalDao.getAllKcal(owner, petName);
        assertEquals(kcalList, list, "Should return a List containing all meals Data");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllKcalsDataFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalsRef);
            given(kcalsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            kcalDao.getAllKcal(owner, petName);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllKcalsDataFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalsRef);
            given(kcalsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            kcalDao.getAllKcal(owner, petName);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnAllKcalsBetweenDatesOnDatabaseWhenRequested() throws DatabaseAccessException,
        ExecutionException, InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalsRef);
        given(kcalsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(kcalsDocuments);
        given(kcalsDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<Map<String, Object>> list = kcalDao.getAllKcalsBetween(owner, petName, date, date2);

        assertEquals(kcalList, list, "Should return a List containing all meals between two dates");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllKcalsBetweenFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalsRef);
            given(kcalsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            kcalDao.getAllKcalsBetween(owner, petName, date, date2);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllKcalsBetweenFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalsRef);
            given(kcalsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            kcalDao.getAllKcalsBetween(owner, petName, date, date2);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldUpdateWhenRequested() throws DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalsRef);
        given(kcalsRef.document(anyString())).willReturn(kcalRef);
        given(kcalRef.update(anyString(), any())).willReturn(null);

        kcalDao.updateKcal(owner, petName, date, value);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(KCALS_KEY));
        verify(kcalsRef).document(same(date));
        verify(kcalRef).update(same(field), same(value));
    }
}
