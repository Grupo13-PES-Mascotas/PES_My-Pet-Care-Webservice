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
import org.pesmypetcare.webservice.entity.MedicationEntity;
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
public class MedicationDaoTest {
    private static List<Map<List<String>, Object>> medList;
    private static MedicationEntity medEntity;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static String dateName;
    private static String field;
    private static int value;


    private static final String USERS_KEY = "users";
    private static final String PETS_KEY = "pets";
    private static final String MEDICATION_KEY = "medications";
    private final String EXCECUTION_EXC_MSG = "Should throw DatabaseAccessException when ExecutionException received";
    private final String INTERRUPTED_EXC_MSG = "Should throw DatabaseAccessException when InterruptedException "
            + "received";
    private final String DOCUMENT_NOT_EXISTS_EXC_MSG = "Should throw DatabaseAccessException when med document"
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
    private CollectionReference medsRef;
    @Mock
    private DocumentReference medRef;
    @Mock
    private ApiFuture<QuerySnapshot> futureQuery;
    @Mock
    private QuerySnapshot querySnapshot;
    @Mock
    private ApiFuture<DocumentSnapshot> futureDocument;
    @Mock
    private DocumentSnapshot documentSnapshot;
    @Mock
    private List<QueryDocumentSnapshot> medsDocuments;
    @Mock
    private Iterator<QueryDocumentSnapshot> it;

    @InjectMocks
    private MedicationDao medDao = new MedicationDaoImpl();

    @BeforeAll
    public static void setUp() {
        medList = new ArrayList<>();
        medEntity = new MedicationEntity();
        owner = "usuario_de_pruebas";
        petName = "linux";
        date = "26-03-2020";
        date2 = "26-04-2020";
        dateName = "26-03-2020%Cloroform";
        field = "duration";
        value = 4;
    }

    @Test
    public void shouldCreateMedOnDatabaseWhenRequested() {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(medsRef);
        given(medsRef.document(anyString())).willReturn(medRef);
        given(medRef.set(isA(MedicationEntity.class))).willReturn(null);

        medDao.createMedication(owner, petName, dateName, medEntity);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(MEDICATION_KEY));
        verify(medsRef).document(same(dateName));
        verify(medRef).set(same(medEntity));
    }

    @Test
    public void shouldDeleteConcreteMedOnDatabaseWhenRequested() { //??
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(medsRef);
        given(medsRef.document(anyString())).willReturn(medRef);
        given(medRef.delete()).willReturn(null);

        medDao.deleteByDateAndName(owner, petName, dateName);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(MEDICATION_KEY));
        verify(medsRef).document(same(dateName));
        verify(medRef).delete();
    }

    @Test
    public void shouldDeleteAllmedsOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
            InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(medsRef);
        given(medsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(medsDocuments);
        given(medsDocuments.iterator()).willReturn(it);

        medDao.deleteAllMedications(owner, petName);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(MEDICATION_KEY));
        verify(medsRef).get();
        verify(futureQuery).get();
        verify(querySnapshot).getDocuments();
        verify(medsDocuments).iterator();
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            medDao.deleteAllMedications(owner, petName);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            medDao.deleteAllMedications(owner, petName);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnMedEntityFromDatabaseWhenRequested() throws ExecutionException, InterruptedException,
            DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(medsRef);
        given(medsRef.document(anyString())).willReturn(medRef);
        given(medRef.get()).willReturn(futureDocument);
        given(futureDocument.get()).willReturn(documentSnapshot);
        given(documentSnapshot.exists()).willReturn(true);
        given(documentSnapshot.toObject(MedicationEntity.class)).willReturn(medEntity);

        MedicationEntity med = medDao.getMedicationData(owner, petName, dateName);

        assertSame(medEntity, med, "Should return med Entity");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenMedDocumentNotExists() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.document(anyString())).willReturn(medRef);
            given(medRef.get()).willReturn(futureDocument);
            given(futureDocument.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(false);

            medDao.getMedicationData(owner, petName, dateName);
        }, DOCUMENT_NOT_EXISTS_EXC_MSG);
    }


    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveMedDocumentReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.document(anyString())).willReturn(medRef);
            given(medRef.get()).willReturn(futureDocument);
            willThrow(InterruptedException.class).given(futureDocument).get();

            medDao.getMedicationData(owner, petName, dateName);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrieveMedDocumentReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.document(anyString())).willReturn(medRef);
            given(medRef.get()).willReturn(futureDocument);
            willThrow(ExecutionException.class).given(futureDocument).get();


            medDao.getMedicationData(owner, petName, dateName);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnAllMedsDataOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
            InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(medsRef);
        given(medsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(medsDocuments);
        given(medsDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<Map<List<String>, Object>> list = medDao.getAllMedicationData(owner, petName);
        assertEquals(medList, list, "Should return a List containing all meds Data");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllMedsDataFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            medDao.getAllMedicationData(owner, petName);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllMedsDataFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            medDao.getAllMedicationData(owner, petName);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnAllMedsBetweenDatesOnDatabaseWhenRequested() throws DatabaseAccessException,
            ExecutionException, InterruptedException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(medsRef);
        given(medsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(medsDocuments);
        given(medsDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<Map<List<String>, Object>> list = medDao.getAllMedicationsBetween(owner, petName, date, date2);

        assertEquals(medList, list, "Should return a List containing all meds between two dates");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllmedsBetweenDatesFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            medDao.getAllMedicationsBetween(owner, petName, date, date2);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetAllmedsBetweenDatesFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            medDao.getAllMedicationsBetween(owner, petName, date, date2);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldReturnMedFieldFromDatabaseWhenRequested() throws ExecutionException, InterruptedException,
            DatabaseAccessException {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(medsRef);
        given(medsRef.document(anyString())).willReturn(medRef);
        given(medRef.get()).willReturn(futureDocument);
        given(futureDocument.get()).willReturn(documentSnapshot);
        given(documentSnapshot.exists()).willReturn(true);
        given(documentSnapshot.get(anyString())).willReturn(value);

        Object medValue = medDao.getMedicationField(owner, petName, dateName, field);

        assertSame(value, medValue, "Should return field value");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenmedDocumentNotExistsInFieldRetrieval() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.document(anyString())).willReturn(medRef);
            given(medRef.get()).willReturn(futureDocument);
            given(futureDocument.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(false);

            medDao.getMedicationField(owner, petName, dateName, field);
        }, DOCUMENT_NOT_EXISTS_EXC_MSG);
    }


    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrievemedFieldReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.document(anyString())).willReturn(medRef);
            given(medRef.get()).willReturn(futureDocument);
            willThrow(InterruptedException.class).given(futureDocument).get();

            medDao.getMedicationField(owner, petName, dateName, field);
        }, INTERRUPTED_EXC_MSG);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrievemedFieldReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(db.collection(anyString())).willReturn(usersRef);
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.collection(anyString())).willReturn(medsRef);
            given(medsRef.document(anyString())).willReturn(medRef);
            given(medRef.get()).willReturn(futureDocument);
            willThrow(ExecutionException.class).given(futureDocument).get();

            medDao.getMedicationField(owner, petName, dateName, field);
        }, EXCECUTION_EXC_MSG);
    }

    @Test
    public void shouldUpdateFieldWhenRequested() {
        given(db.collection(anyString())).willReturn(usersRef);
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.collection(anyString())).willReturn(medsRef);
        given(medsRef.document(anyString())).willReturn(medRef);
        given(medRef.update(anyString(), any())).willReturn(null);

        medDao.updateMedicationField(owner, petName, dateName, field, value);

        verify(db).collection(same(USERS_KEY));
        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(petName));
        verify(petRef).collection(same(MEDICATION_KEY));
        verify(medsRef).document(same(dateName));
        verify(medRef).update(same(field), same(value));
    }
}
