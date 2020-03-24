package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.GenderType;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PetDaoTest {
    @Mock
    private CollectionReference usersRef;
    @Mock
    private DocumentReference ownerRef;
    @Mock
    private CollectionReference petsRef;
    @Mock
    private DocumentReference petRef;
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
    private PetDao petDao = new PetDaoImpl();

    private static List<PetEntity> pets;
    private static PetEntity pet;
    private static String owner;
    private static String name;
    private static String field;
    private static GenderType value;
    private static String PETS_KEY;
    private static String INTERRUPTED_DEL_EXC;
    private static String EXECUTION_DEL_EXC;
    private static String NOT_EXISTS_STR;
    private static String EXECUTION_RETR_EXC;
    private static String INTERRUPTED_RETR_EXC;

    @BeforeEach
    public void setUp() {
        pets = new ArrayList<>();
        pet = new PetEntity();
        owner = "OwnerUsername";
        name = "PetName";
        field = "gender";
        value = GenderType.Other;
        PETS_KEY = "pets";
        INTERRUPTED_DEL_EXC = "Should throw DatabaseAccessException when the deletion from database fails from "
            + "InterruptedException";
        EXECUTION_DEL_EXC = "Should throw DatabaseAccessException when the deletion from database fails from "
            + "ExecutionException";
        NOT_EXISTS_STR = " doesn't exist";
        EXECUTION_RETR_EXC = "Should throw DatabaseAccessException when the retrieve from database fails from "
            + "ExecutionException";
        INTERRUPTED_RETR_EXC = "Should throw DatabaseAccessException when the retrieve from database fails from "
            + "InterruptedException";
    }

    @Test
    public void shouldCreatePetOnDatabaseWhenRequested() {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.set(isA(PetEntity.class))).willReturn(null);

        petDao.createPet(owner, name, pet);

        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(name));
        verify(petRef).set(same(pet));
    }

    @Test
    public void shouldDeletePetOnDatabaseWhenRequested() {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.delete()).willReturn(null);

        petDao.deleteByOwnerAndName(owner, name);

        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(name));
        verify(petRef).delete();
    }

    @Test
    public void shouldDeleteAllPetsOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
        InterruptedException {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(petsDocuments);
        given(petsDocuments.iterator()).willReturn(it);

        petDao.deleteAllPets(owner);

        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).get();
        verify(futureQuery).get();
        verify(querySnapshot).getDocuments();
        verify(petsDocuments).iterator();
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            petDao.deleteAllPets(owner);
        }, INTERRUPTED_DEL_EXC);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            petDao.deleteAllPets(owner);
        }, EXECUTION_DEL_EXC);
    }

    @Test
    public void shouldReturnPetEntityFromDatabaseWhenRequested() throws ExecutionException, InterruptedException
        , DatabaseAccessException {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.get()).willReturn(futureDocument);
        given(futureDocument.get()).willReturn(documentSnapshot);
        given(documentSnapshot.exists()).willReturn(true);
        given(documentSnapshot.toObject(PetEntity.class)).willReturn(pet);

        PetEntity petEntity = petDao.getPetData(owner, name);

        assertSame(pet, petEntity, "Should return Pet Entity");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenPetDocumentNotExists() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            given(futureDocument.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(false);

            petDao.getPetData(owner, name);
        }, "Should throw DatabaseAccessException when the retrieve from database fails because the document"
            + NOT_EXISTS_STR);
    }


    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrievePetDocumentReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            willThrow(InterruptedException.class).given(futureDocument).get();

            petDao.getPetData(owner, name);
        }, INTERRUPTED_RETR_EXC);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrievePetDocumentReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            willThrow(ExecutionException.class).given(futureDocument).get();

            petDao.getPetData(owner, name);
        }, EXECUTION_RETR_EXC);
    }

    @Test
    public void shouldReturnAllPetsDataOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
        InterruptedException {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(petsDocuments);
        given(petsDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<PetEntity> list = petDao.getAllPetsData(owner);

        assertEquals(pets, list, "Should return a Pet Entity List");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            petDao.getAllPetsData(owner);
        }, INTERRUPTED_DEL_EXC);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenGetFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            petDao.getAllPetsData(owner);
        }, EXECUTION_DEL_EXC);
    }


    @Test
    public void shouldReturnPetFieldFromDatabaseWhenRequested() throws ExecutionException, InterruptedException,
        DatabaseAccessException {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.get()).willReturn(futureDocument);
        given(futureDocument.get()).willReturn(documentSnapshot);
        given(documentSnapshot.exists()).willReturn(true);
        given(documentSnapshot.get(anyString())).willReturn(value);

        Object petValue = petDao.getField(owner, name, field);

        assertSame(value, petValue, "Should return field value");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenPetDocumentNotExistsInFieldRetrieval() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            given(futureDocument.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(false);

            petDao.getPetData(owner, name);
        }, "Should throw DatabaseAccessException when the retrieve from database fails because document"
            + NOT_EXISTS_STR);
    }


    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrievePetFieldReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            willThrow(InterruptedException.class).given(futureDocument).get();

            petDao.getPetData(owner, name);
        }, INTERRUPTED_RETR_EXC);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrievePetFieldReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            willThrow(ExecutionException.class).given(futureDocument).get();

            petDao.getPetData(owner, name);
        }, EXECUTION_RETR_EXC);
    }

    @Test
    public void shouldUpdateFieldWhenRequested() {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.update(anyString(), any())).willReturn(null);

        petDao.updateField(owner, name, field, value);

        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(name));
        verify(petRef).update(same(field), same(value));
    }
}
