package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.DocumentSnapshot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.KcalAverageEntity;
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
public class KcalAverageDaoTest {
    private static List<Map<String, Object>> kcalAverageList;
    private static KcalAverageEntity kcalAverageEntity;
    private static String date;
    private static String date2;
    private static String owner;
    private static String petName;
    private static Double value;
    private static String field;
    private static final String USERS_KEY = "users";
    private static final String PETS_KEY = "pets";
    private static final String KCALAVERAGES_KEY = "kcalAverages";
    private final String EXCECUTION_EXC_MSG = "Should throw DatabaseAccessException when ExecutionException received";
    private final String INTERRUPTED_EXC_MSG = "Should throw DatabaseAccessException when InterruptedException "
        + "received";
    private final String DOCUMENT_NOT_EXISTS_EXC_MSG = "Should throw DatabaseAccessException when KcalAverage document"
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
    private CollectionReference kcalAveragesRef;
    @Mock
    private DocumentReference kcalAverageRef;
    @Mock
    private ApiFuture<QuerySnapshot> futureQuery;
    @Mock
    private QuerySnapshot querySnapshot;
    @Mock
    private ApiFuture<DocumentSnapshot> futureDocument;
    @Mock
    private DocumentSnapshot documentSnapshot;
    @Mock
    private List<QueryDocumentSnapshot> kcalAveragesDocuments;
    @Mock
    private Iterator<QueryDocumentSnapshot> it;

    @InjectMocks
    private KcalAverageDao kcalAverageDao = new KcalAverageDaoImpl();

    @BeforeAll
    public static void setUp() {
        kcalAverageList = new ArrayList<>();
        kcalAverageEntity = new KcalAverageEntity();
        date = "2019-10-22T00:47:00";
        date2 = "2021-10-22T00:47:00";
        owner = "PericoDeLosPalotes";
        petName = "TupoJohn";
        field = "kcalAverageValue";
        value = 9.0 / 2.0;
    }

    @Test
    public void shouldCreateKcalAverageOnDatabaseWhenRequested() {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
        given(kcalAveragesRef.document(anyString())).willReturn(kcalAverageRef);
        given(kcalAverageRef.set(isA(KcalAverageEntity.class))).willReturn(null);

        kcalAverageDao.createKcalAverage(owner, petName, date, kcalAverageEntity);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(KCALAVERAGES_KEY));
        verify(kcalAveragesRef).document(same(date));
        verify(kcalAverageRef).set(same(kcalAverageEntity));
    }

    @Test
    public void shouldDeleteKcalAverageOnDatabaseWhenRequested() throws DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
        given(kcalAveragesRef.document(anyString())).willReturn(kcalAverageRef);
        given(kcalAverageRef.delete()).willReturn(null);

        kcalAverageDao.deleteKcalAverageByDate(owner, petName, date);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(KCALAVERAGES_KEY));
        verify(kcalAveragesRef).document(same(date));
        verify(kcalAverageRef).delete();
    }

    @Test
    public void shouldDeleteAllKcalAveragesOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
        InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
        given(kcalAveragesRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(kcalAveragesDocuments);
        given(kcalAveragesDocuments.iterator()).willReturn(it);

        kcalAverageDao.deleteAllKcalAverages(owner, petName);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(KCALAVERAGES_KEY));
        verify(kcalAveragesRef).get();
        verify(futureQuery).get();
        verify(querySnapshot).getDocuments();
        verify(kcalAveragesDocuments).iterator();
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
            given(kcalAveragesRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            kcalAverageDao.deleteAllKcalAverages(owner, petName);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
            given(kcalAveragesRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            kcalAverageDao.deleteAllKcalAverages(owner, petName);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnKcalAverageEntityFromDatabaseWhenRequested() throws ExecutionException,
        InterruptedException, DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
        given(kcalAveragesRef.document(anyString())).willReturn(kcalAverageRef);
        given(kcalAverageRef.get()).willReturn(futureDocument);
        given(futureDocument.get()).willReturn(documentSnapshot);
        given(documentSnapshot.exists()).willReturn(true);
        given(documentSnapshot.toObject(KcalAverageEntity.class)).willReturn(kcalAverageEntity);

        KcalAverageEntity meal = kcalAverageDao.getKcalAverageByDate(owner, petName, date);

        assertSame(kcalAverageEntity, meal, "Should return Meal Entity");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenKcalAverageDocumentNotExists() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
            given(kcalAveragesRef.document(anyString())).willReturn(kcalAverageRef);
            given(kcalAverageRef.get()).willReturn(futureDocument);
            given(futureDocument.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(false);

            kcalAverageDao.getKcalAverageByDate(owner, petName, date);
        }, DOCUMENT_NOT_EXISTS_EXC_MSG);
    }


    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveKcalAverageDocumentReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
            given(kcalAveragesRef.document(anyString())).willReturn(kcalAverageRef);
            given(kcalAverageRef.get()).willReturn(futureDocument);
            willThrow(InterruptedException.class).given(futureDocument).get();

            kcalAverageDao.getKcalAverageByDate(owner, petName, date);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveKcalAverageDocumentReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
            given(kcalAveragesRef.document(anyString())).willReturn(kcalAverageRef);
            given(kcalAverageRef.get()).willReturn(futureDocument);
            willThrow(ExecutionException.class).given(futureDocument).get();


            kcalAverageDao.getKcalAverageByDate(owner, petName, date);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnAllKcalAveragesDataOnDatabaseWhenRequested() throws DatabaseAccessException,
        ExecutionException, InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
        given(kcalAveragesRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(kcalAveragesDocuments);
        given(kcalAveragesDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<Map<String, Object>> list = kcalAverageDao.getAllKcalAverage(owner, petName);
        assertEquals(kcalAverageList, list, "Should return a List containing all meals Data");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllKcalAveragesDataFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
            given(kcalAveragesRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            kcalAverageDao.getAllKcalAverage(owner, petName);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllKcalAveragesDataFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
            given(kcalAveragesRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            kcalAverageDao.getAllKcalAverage(owner, petName);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnAllKcalAveragesBetweenDatesOnDatabaseWhenRequested() throws DatabaseAccessException,
        ExecutionException, InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
        given(kcalAveragesRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(kcalAveragesDocuments);
        given(kcalAveragesDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<Map<String, Object>> list = kcalAverageDao.getAllKcalAveragesBetween(owner, petName, date, date2);

        assertEquals(kcalAverageList, list, "Should return a List containing all meals between two dates");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetKcalAveragesBetweenFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
            given(kcalAveragesRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            kcalAverageDao.getAllKcalAveragesBetween(owner, petName, date, date2);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetKcalAveragesBetweenFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
            given(kcalAveragesRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            kcalAverageDao.getAllKcalAveragesBetween(owner, petName, date, date2);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldUpdateWhenRequested() throws DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(kcalAveragesRef);
        given(kcalAveragesRef.document(anyString())).willReturn(kcalAverageRef);
        given(kcalAverageRef.update(anyString(), any())).willReturn(null);

        kcalAverageDao.updateKcalAverage(owner, petName, date, value);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(KCALAVERAGES_KEY));
        verify(kcalAveragesRef).document(same(date));
        verify(kcalAverageRef).update(same(field), same(value));
    }
}
