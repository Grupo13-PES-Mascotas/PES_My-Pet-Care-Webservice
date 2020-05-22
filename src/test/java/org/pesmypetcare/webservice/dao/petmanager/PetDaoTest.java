package org.pesmypetcare.webservice.dao.petmanager;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.entity.petmanager.GenderType;
import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

/**
 * @author Marc Simó
 */
@ExtendWith(MockitoExtension.class)
class PetDaoTest {
    private static final String OWNER = "OwnerUsername";
    private static final String OWNER_ID = "OwnerId";
    private static final String PET_NAME = "PetName";
    private static final String MEAL_KEY = "1997-01-08T15:20:30";
    private static final String SIMPLE_FIELD = "gender";
    private static final GenderType VALUE = GenderType.Other;
    private static final String COLLECTION_FIELD = "illnesses";
    private static final String KEY_1 = "1996-01-08T12:20:30";
    private static final String KEY_2 = "1998-01-08T15:20:30";
    private static PetEntity petEntity;
    private static List<Map<String, Object>> petList;
    private static List<Map<String, Object>> mealList;
    private static List<DocumentSnapshot> snapshotList;
    private static Map<String, Object> collectionElementBody;
    private static Map<String, Object> mealMap;

    @Mock
    private FirestoreCollection dbCol;
    @Mock
    private FirestoreDocument dbDoc;
    @Mock
    private WriteBatch batch;
    @Mock
    private StorageDao storageDao;
    @Mock
    private DocumentSnapshot documentSnapshot;

    @InjectMocks
    private PetDao petDao = new PetDaoImpl();

    @BeforeEach
    public void setUp() {
        petEntity = new PetEntity(GenderType.Female, "Huskie", "1996-01-08T12:20:30",
            "One limb lost", "Constant attention", 85.0, null,
            null);
        petList = new ArrayList<>();
        Map<String, Object> auxMap = new HashMap<>();
        auxMap.put("name", PET_NAME);
        auxMap.put("body", petEntity);
        petList.add(auxMap);
        petList.add(auxMap);
        petList.add(auxMap);
        snapshotList = new ArrayList<>();
        snapshotList.add(documentSnapshot);
        snapshotList.add(documentSnapshot);
        snapshotList.add(documentSnapshot);
        mealMap = new HashMap<>();
        mealMap.put("mealName", "Asparagus");
        mealMap.put("kcal", 84.0);
        collectionElementBody = new HashMap<>();
        collectionElementBody.put("key", MEAL_KEY);
        collectionElementBody.put("body", mealMap);
        mealList = new ArrayList<>();
        mealList.add(collectionElementBody);
        mealList.add(collectionElementBody);
        mealList.add(collectionElementBody);
    }

    @Test
    public void shouldCreatePetOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.createDocumentWithId(anyString(), anyString(), any(PetEntity.class),
            any(WriteBatch.class))).willReturn(null);
        willDoNothing().given(dbDoc).commitBatch(batch);

        petDao.createPet(OWNER, PET_NAME, petEntity);

        verify(dbDoc).createDocumentWithId(isA(String.class), same(PET_NAME), same(petEntity), same(batch));
    }

    @Test
    public void shouldDeletePetOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn("user/pets/pet-profile-image.png");
        willDoNothing().given(dbDoc).commitBatch(batch);
        willDoNothing().given(storageDao).deleteImageByName(anyString());

        petDao.deleteByOwnerAndName(OWNER, PET_NAME);

        verify(dbDoc).deleteDocument(isA(String.class), same(batch));
        verify(storageDao).deleteImageByName(eq("user/pets/pet-profile-image.png"));
    }

    @Test
    public void shouldDeleteAllPetsOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(snapshotList);
        willDoNothing().given(dbDoc).commitBatch(batch);

        petDao.deleteAllPets(OWNER);

        verify(dbCol).deleteCollection(anyString(), same(batch));
    }

    @Test
    public void shouldReturnPetEntityFromDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getDocumentDataAsObject(anyString(), any())).willReturn(petEntity);

        PetEntity result = petDao.getPetData(OWNER, PET_NAME);

        assertSame(petEntity, result, "Should return Pet Entity");
    }

    @Test
    public void shouldReturnAllPetsDataOnDatabaseWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(snapshotList);
        given(documentSnapshot.getId()).willReturn(PET_NAME);
        given(documentSnapshot.toObject(any())).willReturn(petEntity);

        List<Map<String, Object>> list = petDao.getAllPetsData(OWNER);

        assertEquals(petList, list, "Should return a List containing all pets Data");
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
        willDoNothing().given(dbDoc).commitBatch(batch);

        petDao.updateSimpleField(OWNER, PET_NAME, SIMPLE_FIELD, VALUE);

        verify(dbDoc).updateDocumentFields(same(batch), isA(String.class), same(SIMPLE_FIELD), same(VALUE));
    }

    @Test
    public void shouldDeleteFieldCollectionWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        willDoNothing().given(dbDoc).commitBatch(batch);

        petDao.deleteFieldCollection(OWNER, PET_NAME, COLLECTION_FIELD);

        verify(dbCol).deleteCollection(anyString(), same(batch));
    }

    @Test
    public void shouldDeleteFieldCollectionElementsPreviousToKeyWhenRequested() throws DatabaseAccessException,
        DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(new ArrayList<>());
        willDoNothing().given(dbDoc).commitBatch(batch);

        petDao.deleteFieldCollectionElementsPreviousToKey(OWNER, PET_NAME, COLLECTION_FIELD, KEY_1);

        verify(dbDoc).commitBatch(same(batch));
    }

    @Test
    public void shouldGetFieldCollectionWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(snapshotList);
        given(documentSnapshot.getId()).willReturn(MEAL_KEY);
        given(documentSnapshot.getData()).willReturn(mealMap);

        List<Map<String, Object>> list = petDao.getFieldCollection(OWNER, PET_NAME, COLLECTION_FIELD);

        assertEquals(mealList, list, "Should return a List containing all field collection Data");
    }

    @Test
    public void shouldGetFieldCollectionElementsBetweenKeysWhenRequested()
        throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbCol.listAllCollectionDocumentSnapshots(anyString())).willReturn(snapshotList);
        given(documentSnapshot.getId()).willReturn(MEAL_KEY);
        given(documentSnapshot.getData()).willReturn(mealMap);

        List<Map<String, Object>> list = petDao.getFieldCollectionElementsBetweenKeys(OWNER, PET_NAME, COLLECTION_FIELD,
            KEY_1, KEY_2);

        assertEquals(mealList, list, "Should return a List containing the field collection Data between the "
            + "specified keys");
    }

    @Test
    public void shouldAddFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.createDocumentWithId(anyString(), anyString(), any(Map.class),
            any(WriteBatch.class)))
            .willReturn(null);
        willDoNothing().given(dbDoc).commitBatch(batch);

        petDao.addFieldCollectionElement(OWNER, PET_NAME, COLLECTION_FIELD, KEY_1, collectionElementBody);

        verify(dbDoc).createDocumentWithId(isA(String.class), same(KEY_1), same(collectionElementBody), same(batch));
    }

    @Test
    public void shouldDeleteFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        willDoNothing().given(dbDoc).commitBatch(batch);

        petDao.deleteFieldCollectionElement(OWNER, PET_NAME, COLLECTION_FIELD, KEY_1);

        verify(dbDoc).deleteDocument(isA(String.class), same(batch));
    }

    @Test
    public void shouldUpdateFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        willDoNothing().given(dbDoc).commitBatch(batch);

        petDao.updateFieldCollectionElement(OWNER, PET_NAME, COLLECTION_FIELD, KEY_1, collectionElementBody);

        verify(dbDoc).updateDocumentFields(isA(String.class), same(collectionElementBody), same(batch));
    }

    @Test
    public void shouldGetFieldCollectionElementWhenRequested() throws DatabaseAccessException, DocumentException {
        given(dbDoc.getStringFromDocument(anyString(), anyString())).willReturn(OWNER_ID);
        given(dbCol.batch()).willReturn(batch);
        given(dbDoc.getDocumentData(anyString())).willReturn(collectionElementBody);

        Map<String, Object> result = petDao.getFieldCollectionElement(OWNER, PET_NAME, COLLECTION_FIELD, KEY_1);

        assertSame(collectionElementBody, result, "Should return elemnt data in a Map<String, Object>");
    }
}
