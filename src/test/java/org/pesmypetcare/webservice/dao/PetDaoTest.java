package org.pesmypetcare.webservice.dao;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author Marc Simó
 */
@ExtendWith(MockitoExtension.class)
class PetDaoTest {
    private static final List<Map<String, Object>> petList = new ArrayList<>();
    private static final List<DocumentSnapshot> petSnapshotList = new ArrayList<>();
    private static final PetEntity petEntity = new PetEntity();
    private static final String owner = "OwnerUsername";
    private static final String ownerId = "OwnerId";
    private static final String name = "PetName";
    private static final String field = "gender";
    private static final GenderType value = GenderType.Other;
    private static final String collectionField = "trainings";
    private static final String key1 = "1996-01-08T12:20:30";
    private static final String key2 = "1996-01-08T15:20:30";
    private static final Map<String, Object> collectionElementBody = new HashMap<>();

    @Mock
    private FirestoreCollection dbCol;
    @Mock
    private FirestoreDocument dbDoc;
    @Mock
    private WriteBatch batch;
    @Mock
    private StorageDao storageDao;

    @InjectMocks
    private PetDao petDao = new PetDaoImpl();

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

    @Test
    public void shouldDeletePetOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn("user/pets/pet-profile-image.png");
        given(batch.commit()).willReturn(null);

        petDao.deleteByOwnerAndName(owner, name);

        verify(dbDoc).deleteDocument(isA(String.class), same(batch));
    }

    @Test
    public void shouldDeleteAllPetsOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(petSnapshotList);
        given(batch.commit()).willReturn(null);

        petDao.deleteAllPets(owner);

        verify(dbCol).deleteCollection(anyString(), same(batch));
    }

    @Test
    public void shouldReturnPetEntityFromDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getDocumentDataAsObject(anyString(), any())).willReturn(petEntity);

        PetEntity result = petDao.getPetData(owner, name);

        assertSame(petEntity, result, "Should return Pet Entity");
    }

    @Test
    public void shouldReturnAllPetsDataOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(petSnapshotList);

        List<Map<String, Object>> list = petDao.getAllPetsData(owner);

        assertEquals(petList, list, "Should return a List containing all pets Data");
    }

    @Test
    public void shouldReturnPetSimpleFieldFromDatabaseWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbDoc.getDocumentField(anyString(), anyString())).willReturn(value);

        Object petValue = petDao.getSimpleField(owner, name, field);

        assertSame(value, petValue, "Should return field value");
    }

    @Test
    public void shouldUpdateSimpleFieldWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(batch.commit()).willReturn(null);

        petDao.updateSimpleField(owner, name, field, value);

        verify(dbDoc).updateDocumentFields(same(batch), isA(String.class), same(field), same(value));
    }

    @Test
    public void shouldDeleteFieldCollectionWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(batch.commit()).willReturn(null);

        petDao.deleteFieldCollection(owner, name, collectionField);

        verify(dbCol).deleteCollection(anyString(), same(batch));
    }

    @Test
    public void shouldGetFieldCollectionWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(petSnapshotList);

        List<Map<String, Object>> list = petDao.getFieldCollection(owner, name, collectionField);

        assertEquals(petList, list, "Should return a List containing all field collection Data");
    }

    @Test
    public void shouldGetFieldCollectionElementsBetweenKeysWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(petSnapshotList);

        List<Map<String, Object>> list = petDao.getFieldCollectionElementsBetweenKeys(owner, name, collectionField,
            key1, key2);

        assertEquals(petList, list, "Should return a List containing all field collection Data");
    }

    @Test
    public void shouldAddFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.createDocumentWithId(anyString(), anyString(), any(Map.class),
            any(WriteBatch.class)))
            .willReturn(null);
        given(batch.commit()).willReturn(null);

        petDao.addFieldCollectionElement(owner,name, collectionField, key1, collectionElementBody);

        verify(dbDoc).createDocumentWithId(isA(String.class), same(key1), same(collectionElementBody), same(batch));
    }

    @Test
    public void shouldDeleteFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(batch.commit()).willReturn(null);

        petDao.deleteFieldCollectionElement(owner, name, collectionField, key1);

        verify(dbDoc).deleteDocument(isA(String.class), same(batch));
    }

    @Test
    public void shouldUpdateFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(batch.commit()).willReturn(null);

        petDao.updateFieldCollectionElement(owner, name, collectionField, key1, collectionElementBody);

        verify(dbDoc).updateDocumentFields(isA(String.class), same(collectionElementBody), same(batch));
    }

    @Test
    public void shouldGetFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(ownerId);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getDocumentData(anyString())).willReturn(collectionElementBody);

        Map<String, Object> result = petDao.getFieldCollectionElement(owner, name, collectionField, key1);

        assertSame(collectionElementBody, result, "Should return elemnt data in a Map<String, Object>");
    }
}
