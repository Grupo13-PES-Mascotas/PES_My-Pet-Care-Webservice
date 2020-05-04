package org.pesmypetcare.webservice.dao;

import com.google.cloud.firestore.WriteBatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.GenderType;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private static List<Map<String, Object>> petList;
    private static PetEntity petEntity;
    private static String owner;
    private static String ownerId;
    private static String name;
    private static String field;
    private static GenderType value;
    private final String PETS_KEY = "pets";
    private final String INTERRUPTED_DEL_EXC = "Should throw DatabaseAccessException when the deletion from database "
        + "fails from InterruptedException";
    private final String EXECUTION_DEL_EXC = "Should throw DatabaseAccessException when the deletion from database "
        + "fails from ExecutionException";
    private final String NOT_EXISTS_STR = " doesn't exist";
    private final String EXECUTION_RETR_EXC = "Should throw DatabaseAccessException when the retrieve from database "
        + "fails from ExecutionException";
    private final String INTERRUPTED_RETR_EXC = "Should throw DatabaseAccessException when the retrieve from database"
        + " fails from InterruptedException";

    @Mock
    private FirestoreCollection dbCol;
    @Mock
    private FirestoreDocument dbDoc;
    @Mock
    private WriteBatch batch;

    @InjectMocks
    private PetDao petDao = new PetDaoImpl();

    @BeforeAll
    public static void setUp() {
        petList = new ArrayList<>();
        petEntity = new PetEntity();
        owner = "OwnerUsername";
        ownerId = "OwnerId";
        name = "PetName";
        field = "gender";
        value = GenderType.Other;
    }

    @Test
    public void shouldCreatePetOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.createDocumentWithId(anyString(), anyString(), any(PetEntity.class),
            any(WriteBatch.class))).willReturn(null);
        given(batch.commit()).willReturn(null);

        petDao.createPet(owner, name, petEntity);

        verify(dbDoc).createDocumentWithId(isA(String.class), same(name), same(petEntity), same(batch));
    }
/*
    @Test
    public void shouldDeletePetOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.get()).willReturn(futureDocument);
        try {
            given(futureDocument.get()).willReturn(documentSnapshot);
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("deletion-failed", e.getMessage());
        }
        given(documentSnapshot.get(anyString())).willReturn("user/pets/pet-profile-image.png");
        given(petRef.delete()).willReturn(null);

        petDao.deleteByOwnerAndName(owner, name);

        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(name));
        verify(petRef).delete();
    }

    @Test
    public void shouldDeleteAllPetsOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
        InterruptedException, DocumentException {
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
    public void shouldReturnPetEntityFromDatabaseWhenRequested() throws ExecutionException, InterruptedException,
        DatabaseAccessException, DocumentException {
        retrievePetEntityMock();

        PetEntity petEntity = petDao.getPetData(owner, name);

        assertSame(PetDaoTest.petEntity, petEntity, "Should return Pet Entity");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenPetDocumentNotExists() {
        assertThrows(DatabaseAccessException.class, () -> {
            documentExists(false);

            petDao.getPetData(owner, name);
        }, "Should throw DatabaseAccessException when the retrieve from database fails because the document"
            + NOT_EXISTS_STR);
    }


    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrievePetDocumentReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            throwInterruptionExceptionMock();

            petDao.getPetData(owner, name);
        }, INTERRUPTED_RETR_EXC);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrievePetDocumentReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            throwExecutionExceptionMock();

            petDao.getPetData(owner, name);
        }, EXECUTION_RETR_EXC);
    }

    @Test
    public void shouldReturnAllPetsDataOnDatabaseWhenRequested() throws DatabaseAccessException, ExecutionException,
        InterruptedException, DocumentException {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.get()).willReturn(futureQuery);
        given(futureQuery.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(petsDocuments);
        given(petsDocuments.iterator()).willReturn(it);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(true);
        given(it.hasNext()).willReturn(false);

        List<Map<String, Object>> list = petDao.getAllPetsData(owner);

        assertEquals(petList, list, "Should return a List containing all pets Data");
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
        DatabaseAccessException, DocumentException {
        documentExists(true);
        given(documentSnapshot.get(anyString())).willReturn(value);

        Object petValue = petDao.getSimpleField(owner, name, field);

        assertSame(value, petValue, "Should return field value");
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenPetDocumentNotExistsInFieldRetrieval() {
        assertThrows(DatabaseAccessException.class, () -> {
            documentExists(false);

            petDao.getSimpleField(owner, name, field);
        }, "Should throw DatabaseAccessException when the retrieve from database fails because document"
            + NOT_EXISTS_STR);
    }


    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrievePetFieldReceivesInterruptedException() {
        assertThrows(DatabaseAccessException.class, () -> {
            throwInterruptionExceptionMock();

            petDao.getSimpleField(owner, name, field);
        }, INTERRUPTED_RETR_EXC);
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenRetrievePetFieldReceivesExecutionException() {
        assertThrows(DatabaseAccessException.class, () -> {
            throwExecutionExceptionMock();

            petDao.getSimpleField(owner, name, field);
        }, EXECUTION_RETR_EXC);
    }

    @Test
    public void shouldUpdateFieldWhenRequested() throws DatabaseAccessException, DocumentException {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.update(anyString(), any())).willReturn(null);

        petDao.updateSimpleField(owner, name, field, value);

        verify(usersRef).document(same(owner));
        verify(ownerRef).collection(same(PETS_KEY));
        verify(petsRef).document(same(name));
        verify(petRef).update(same(field), same(value));
    }


    private void documentExists(boolean b) throws InterruptedException, ExecutionException {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.get()).willReturn(futureDocument);
        given(futureDocument.get()).willReturn(documentSnapshot);
        given(documentSnapshot.exists()).willReturn(b);
    }

    private void throwInterruptionExceptionMock() throws InterruptedException, ExecutionException {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.get()).willReturn(futureDocument);
        willThrow(InterruptedException.class).given(futureDocument).get();
    }

    private void throwExecutionExceptionMock() throws InterruptedException, ExecutionException {
        given(usersRef.document(anyString())).willReturn(ownerRef);
        given(ownerRef.collection(anyString())).willReturn(petsRef);
        given(petsRef.document(anyString())).willReturn(petRef);
        given(petRef.get()).willReturn(futureDocument);
        willThrow(ExecutionException.class).given(futureDocument).get();
    }

    private void retrievePetEntityMock() throws InterruptedException, ExecutionException {
        documentExists(true);
        given(documentSnapshot.toObject(PetEntity.class)).willReturn(petEntity);
    }

 */
}
