package org.pesmypetcare.webservice.service.petmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.petmanager.PetDao;
import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Marc Simó
 */
@ExtendWith(MockitoExtension.class)
class PetServiceTest {
    private static final List<Map<String, Object>> PET_LIST = new ArrayList<>();
    private static final Map<String, Object> COLLECTION_ELEMENT_BODY = new HashMap<>();
    private static final PetEntity PET_ENTITY = new PetEntity();
    private static final String TOKEN = "OwnerToken";
    private static final String UID = "OwnerUID";
    private static final String PET_NAME = "PetName";
    private static final String FIELD = "pathologies";
    private static final String VALUE = "COVID-19";
    private static final String KEY_1 = "1996-01-08T12:20:30";
    private static final String KEY_2 = "1996-01-08T15:20:30";

    @Mock
    private PetDao petDao;
    @Mock
    private UserToken userToken;

    @InjectMocks
    private PetService service = spy(new PetServiceImpl());

    @Test
    public void shouldReturnNothingWhenPetCreated() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        service.createPet(TOKEN, PET_NAME, PET_ENTITY);
        verify(petDao).createPet(isA(String.class), isA(String.class), isA(PetEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenPetDeleted() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        service.deleteByOwnerAndName(TOKEN, PET_NAME);
        verify(petDao).deleteByOwnerAndName(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllPetsDeleted() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        service.deleteAllPets(TOKEN);
        verify(petDao).deleteAllPets(isA(String.class));
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenDeleteFails() {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(petDao).deleteAllPets(any(String.class));
            service.deleteAllPets(TOKEN);
        }, "Should return a database access exception when a pet deletion fails");
    }

    @Test
    public void shouldReturnPetEntityWhenPetRetrieved() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        when(petDao.getPetData(UID, PET_NAME)).thenReturn(PET_ENTITY);
        PetEntity entity = service.getPetData(TOKEN, PET_NAME);
        assertSame(PET_ENTITY, entity, "Should return a pet entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetPetRequestFails() {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(petDao).getPetData(any(String.class), any(String.class));
            service.getPetData(TOKEN, PET_NAME);
        }, "Should return an exception when retrieving a pet fails");
    }

    @Test
    public void shouldReturnPetEntityListWhenGetSetOfPetsRetrieved() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        when(petDao.getAllPetsData(UID)).thenReturn(PET_LIST);
        List<Map<String, Object>> list = service.getAllPetsData(TOKEN);
        assertSame(PET_LIST, list, "Should return a list of pet entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfPetsRequestFails() {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(petDao).getAllPetsData(any(String.class));
            service.getAllPetsData(TOKEN);
        }, "Should return an exception when retrieving a set of pets fails");
    }

    @Test
    public void shouldReturnPetSimpleFieldWhenPetFieldRetrieved() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        when(petDao.getSimpleField(UID, PET_NAME, FIELD)).thenReturn(VALUE);
        Object obtainedValue = service.getSimpleField(TOKEN, PET_NAME, FIELD);
        assertSame(VALUE, obtainedValue, "Should return an Object");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetPetFieldRequestFails() {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(petDao)
                .getSimpleField(any(String.class), any(String.class), any(String.class));
            service.getSimpleField(TOKEN, PET_NAME, FIELD);
        }, "Should return an exception when retrieving a pet field fails");
    }

    @Test
    public void shouldReturnNothingWhenPetSimpleFieldUpdated() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        service.updateSimpleField(TOKEN, PET_NAME, FIELD, VALUE);
        verify(petDao).updateSimpleField(same(UID), same(PET_NAME), same(FIELD), same(VALUE));
    }

    @Test
    public void shouldReturnNothingWhenFieldCollectionDeleted() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        service.deleteFieldCollection(TOKEN, PET_NAME, FIELD);
        verify(petDao).deleteFieldCollection(same(UID), same(PET_NAME), same(FIELD));
    }

    @Test
    public void shouldReturnNothingWhenFieldCollectionElementsPreviousToKeyDeleted()
        throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        service.deleteFieldCollectionElementsPreviousToKey(TOKEN, PET_NAME, FIELD, KEY_1);
        verify(petDao)
            .deleteFieldCollectionElementsPreviousToKey(same(UID), same(PET_NAME), same(FIELD), same(KEY_1));
    }

    @Test
    public void shouldReturnListWhenFieldCollectionRetrieved() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        when(petDao.getFieldCollection(anyString(), anyString(), anyString())).thenReturn(PET_LIST);
        List<Map<String, Object>> list = service.getFieldCollection(TOKEN, PET_NAME, FIELD);
        assertSame(PET_LIST, list, "Should return a list of pet entities");
    }

    @Test
    public void shouldReturnListWhenFieldCollectionElementsBetweenKeysRetrieved()
        throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        when(petDao
            .getFieldCollectionElementsBetweenKeys(anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(PET_LIST);
        List<Map<String, Object>> list = service
            .getFieldCollectionElementsBetweenKeys(TOKEN, PET_NAME, FIELD, KEY_1, KEY_2);
        assertSame(PET_LIST, list, "Should return a list of pet entities");
    }

    @Test
    public void shouldReturnNothingWhenFieldCollectionElementAdded() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        service.addFieldCollectionElement(TOKEN, PET_NAME, FIELD, KEY_1, COLLECTION_ELEMENT_BODY);
        verify(petDao).addFieldCollectionElement(same(UID), same(PET_NAME), same(FIELD), same(KEY_1),
            same(COLLECTION_ELEMENT_BODY));
    }

    @Test
    public void shouldReturnNothingWhenFieldCollectionElementDeleted()
        throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        service.deleteFieldCollectionElement(TOKEN, PET_NAME, FIELD, KEY_1);
        verify(petDao).deleteFieldCollectionElement(same(UID), same(PET_NAME), same(FIELD), same(KEY_1));
    }

    @Test
    public void shouldReturnNothingWhenFieldCollectionElementUpdated()
        throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        service.updateFieldCollectionElement(TOKEN, PET_NAME, FIELD, KEY_1, COLLECTION_ELEMENT_BODY);
        verify(petDao).updateFieldCollectionElement(same(UID), same(PET_NAME), same(FIELD), same(KEY_1),
            same(COLLECTION_ELEMENT_BODY));
    }

    @Test
    public void shouldReturnMapWhenFieldCollectionElementRetrieved() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((PetServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(UID);
        service.getFieldCollectionElement(TOKEN, PET_NAME, FIELD, KEY_1);
        verify(petDao).getFieldCollectionElement(same(UID), same(PET_NAME), same(FIELD), same(KEY_1));
    }
}
