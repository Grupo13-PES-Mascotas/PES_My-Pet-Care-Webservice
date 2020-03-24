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

    private List<PetEntity> pets;
    private PetEntity pet;
    private String owner;
    private String name;
    private String field;
    private GenderType value;
    private String PETS_KEY;

    @BeforeEach
    void setUp() {
        pets = new ArrayList<>();
        pet = new PetEntity();
        owner = "OwnerUsername";
        name = "PetName";
        field = "gender";
        value = GenderType.Other;
        PETS_KEY = "pets";
    }

    @Test
    void shouldCreatePetOnDatabaseWhenRequested() {
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
    void shouldDeletePetOnDatabaseWhenRequested() {
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
    void shouldDeleteAllPetsOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
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
    void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            petDao.deleteAllPets(owner);
        }, "Should throw DatabaseAccessException when the deletion from database fails from InterruptedException");
    }

    @Test
    void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            petDao.deleteAllPets(owner);
        }, "Should throw DatabaseAccessException when the deletion from database fails from ExecutionException");
    }

    @Test
    void shouldReturnPetEntityFromDatabaseWhenRequested() throws ExecutionException, InterruptedException, DatabaseAccessException {
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
    void shouldThrowDatabaseAccessExceptionWhenPetDocumentNotExists() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            given(futureDocument.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(false);

            petDao.getPetData(owner, name);
        }, "Should throw DatabaseAccessException when the retrieve from database fails because the document" +
            " doesn't exist");
    }


    @Test
    void shouldThrowDatabaseAccessExceptionWhenRetrievePetDocumentReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            willThrow(InterruptedException.class).given(futureDocument).get();

            petDao.getPetData(owner, name);
        }, "Should throw DatabaseAccessException when the retrieve from database fails from InterruptedException");
    }

    @Test
    void shouldThrowDatabaseAccessExceptionWhenRetrievePetDocumentReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            willThrow(ExecutionException.class).given(futureDocument).get();

            petDao.getPetData(owner, name);
        }, "Should throw DatabaseAccessException when the retrieve from database fails from ExecutionException");
    }

    @Test
    void shouldReturnAllPetsDataOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
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
    void shouldThrowDatabaseAccessExceptionWhenGetFromDatabaseReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.get()).willReturn(futureQuery);
            willThrow(InterruptedException.class).given(futureQuery).get();

            petDao.getAllPetsData(owner);
        }, "Should throw DatabaseAccessException when the deletion from database fails from InterruptedException");
    }

    @Test
    void shouldThrowDatabaseAccessExceptionWhenGetFromDatabaseReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.get()).willReturn(futureQuery);
            willThrow(ExecutionException.class).given(futureQuery).get();

            petDao.getAllPetsData(owner);
        }, "Should throw DatabaseAccessException when the deletion from database fails from ExecutionException");
    }


    @Test
    void shouldReturnPetFieldFromDatabaseWhenRequested() throws ExecutionException, InterruptedException,
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
    void shouldThrowDatabaseAccessExceptionWhenPetDocumentNotExistsInFieldRetrieval() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            given(futureDocument.get()).willReturn(documentSnapshot);
            given(documentSnapshot.exists()).willReturn(false);

            petDao.getPetData(owner, name);
        }, "Should throw DatabaseAccessException when the retrieve from database fails because document" +
            " doesn't exist");
    }


    @Test
    void shouldThrowDatabaseAccessExceptionWhenRetrievePetFieldReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            willThrow(InterruptedException.class).given(futureDocument).get();

            petDao.getPetData(owner, name);
        }, "Should throw DatabaseAccessException when the retrieve from database fails from InterruptedException");
    }

    @Test
    void shouldThrowDatabaseAccessExceptionWhenRetrievePetFieldReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            given(usersRef.document(anyString())).willReturn(ownerRef);
            given(ownerRef.collection(anyString())).willReturn(petsRef);
            given(petsRef.document(anyString())).willReturn(petRef);
            given(petRef.get()).willReturn(futureDocument);
            willThrow(ExecutionException.class).given(futureDocument).get();

            petDao.getPetData(owner, name);
        }, "Should throw DatabaseAccessException when the retrieve from database fails from ExecutionException");
    }

    @Test
    void shouldUpdateFieldWhenRequested(){
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
