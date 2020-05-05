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
    private static final List<Map<String, Object>> PET_LIST = new ArrayList<>();
    private static final List<DocumentSnapshot> PET_SNAPSHOT_LIST = new ArrayList<>();
    private static final PetEntity PET_ENTITY = new PetEntity();
    private static final String OWNER = "OwnerUsername";
    private static final String OWNER_ID = "OwnerId";
    private static final String PET_NAME = "PetName";
    private static final String SIMPLE_FIELD = "gender";
    private static final GenderType VALUE = GenderType.Other;
    private static final String COLLECTION_FIELD = "trainings";
    private static final String KEY_1 = "1996-01-08T12:20:30";
    private static final String KEY_2 = "1996-01-08T15:20:30";
    private static final Map<String, Object> COLLECTION_ELEMENT_BODY = new HashMap<>();

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
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.createDocumentWithId(anyString(), anyString(), any(PetEntity.class),
            any(WriteBatch.class))).willReturn(null);
        given(batch.commit()).willReturn(null);

        petDao.createPet(OWNER, PET_NAME, PET_ENTITY);

        verify(dbDoc).createDocumentWithId(isA(String.class), same(PET_NAME), same(PET_ENTITY), same(batch));
    }

    @Test
    public void shouldDeletePetOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn("user/pets/pet-profile-image.png");
        given(batch.commit()).willReturn(null);

        petDao.deleteByOwnerAndName(OWNER, PET_NAME);

        verify(dbDoc).deleteDocument(isA(String.class), same(batch));
    }

    @Test
    public void shouldDeleteAllPetsOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(PET_SNAPSHOT_LIST);
        given(batch.commit()).willReturn(null);

        petDao.deleteAllPets(OWNER);

        verify(dbCol).deleteCollection(anyString(), same(batch));
    }

    @Test
    public void shouldReturnPetEntityFromDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getDocumentDataAsObject(anyString(), any())).willReturn(PET_ENTITY);

        PetEntity result = petDao.getPetData(OWNER, PET_NAME);

        assertSame(PET_ENTITY, result, "Should return Pet Entity");
    }

    @Test
    public void shouldReturnAllPetsDataOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(PET_SNAPSHOT_LIST);

        List<Map<String, Object>> list = petDao.getAllPetsData(OWNER);

        assertEquals(PET_LIST, list, "Should return a List containing all pets Data");
    }

    @Test
    public void shouldReturnPetSimpleFieldFromDatabaseWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbDoc.getDocumentField(anyString(), anyString())).willReturn(VALUE);

        Object petValue = petDao.getSimpleField(OWNER, PET_NAME, SIMPLE_FIELD);

        assertSame(VALUE, petValue, "Should return field value");
    }

    @Test
    public void shouldUpdateSimpleFieldWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(batch.commit()).willReturn(null);

        petDao.updateSimpleField(OWNER, PET_NAME, SIMPLE_FIELD, VALUE);

        verify(dbDoc).updateDocumentFields(same(batch), isA(String.class), same(SIMPLE_FIELD), same(VALUE));
    }

    @Test
    public void shouldDeleteFieldCollectionWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(batch.commit()).willReturn(null);

        petDao.deleteFieldCollection(OWNER, PET_NAME, COLLECTION_FIELD);

        verify(dbCol).deleteCollection(anyString(), same(batch));
    }

    @Test
    public void shouldGetFieldCollectionWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(PET_SNAPSHOT_LIST);

        List<Map<String, Object>> list = petDao.getFieldCollection(OWNER, PET_NAME, COLLECTION_FIELD);

        assertEquals(PET_LIST, list, "Should return a List containing all field collection Data");
    }

    @Test
    public void shouldGetFieldCollectionElementsBetweenKeysWhenRequested()
        throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(PET_SNAPSHOT_LIST);

        List<Map<String, Object>> list = petDao.getFieldCollectionElementsBetweenKeys(OWNER, PET_NAME, COLLECTION_FIELD,
            KEY_1, KEY_2);

        assertEquals(PET_LIST, list, "Should return a List containing the field collection Data between the "
            + "specified keys");
    }

    @Test
    public void shouldAddFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.createDocumentWithId(anyString(), anyString(), any(Map.class),
            any(WriteBatch.class)))
            .willReturn(null);
        given(batch.commit()).willReturn(null);

        petDao.addFieldCollectionElement(OWNER, PET_NAME, COLLECTION_FIELD, KEY_1, COLLECTION_ELEMENT_BODY);

        verify(dbDoc).createDocumentWithId(isA(String.class), same(KEY_1), same(COLLECTION_ELEMENT_BODY), same(batch));
    }

    @Test
    public void shouldDeleteFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(batch.commit()).willReturn(null);

        petDao.deleteFieldCollectionElement(OWNER, PET_NAME, COLLECTION_FIELD, KEY_1);

        verify(dbDoc).deleteDocument(isA(String.class), same(batch));
    }

    @Test
    public void shouldUpdateFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(batch.commit()).willReturn(null);

        petDao.updateFieldCollectionElement(OWNER, PET_NAME, COLLECTION_FIELD, KEY_1, COLLECTION_ELEMENT_BODY);

        verify(dbDoc).updateDocumentFields(isA(String.class), same(COLLECTION_ELEMENT_BODY), same(batch));
    }

    @Test
    public void shouldGetFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getDocumentData(anyString())).willReturn(COLLECTION_ELEMENT_BODY);

        Map<String, Object> result = petDao.getFieldCollectionElement(OWNER, PET_NAME, COLLECTION_FIELD, KEY_1);

        assertSame(COLLECTION_ELEMENT_BODY, result, "Should return elemnt data in a Map<String, Object>");
    }
}
