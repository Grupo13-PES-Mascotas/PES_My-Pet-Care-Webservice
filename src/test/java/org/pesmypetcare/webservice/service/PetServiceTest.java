package org.pesmypetcare.webservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.PetDao;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * @author Marc Simó
 */
@ExtendWith(MockitoExtension.class)
class PetServiceTest {
    private static final List<Map<String, Object>> petList = new ArrayList<>();
    private static final Map<String, Object> collectionElementBody = new HashMap<>();
    private static final PetEntity pet = new PetEntity();
    private static final String owner = "OwnerUsername";
    private static final String name = "PetName";
    private static final String field = "pathologies";
    private static final String value = "COVID-19";
    private static final String key1 = "1996-01-08T12:20:30";
    private static final String key2 = "1996-01-08T15:20:30";

    @Mock
    private PetDao petDao;

    @InjectMocks
    private PetService service = new PetServiceImpl();

    @Test
    public void shouldReturnNothingWhenPetCreated() throws DatabaseAccessException, DocumentException {
        service.createPet(owner, name, pet);
        verify(petDao).createPet(isA(String.class), isA(String.class), isA(PetEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenPetDeleted() throws DatabaseAccessException, DocumentException {
        service.deleteByOwnerAndName(owner, name);
        verify(petDao).deleteByOwnerAndName(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllPetsDeleted() throws DatabaseAccessException, DocumentException {
        service.deleteAllPets(owner);
        verify(petDao).deleteAllPets(isA(String.class));
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenDeleteFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(petDao).deleteAllPets(any(String.class));
            service.deleteAllPets(owner);
        }, "Should return a database access exception when a pet deletion fails");
    }

    @Test
    public void shouldReturnPetEntityWhenPetRetrieved() throws DatabaseAccessException, DocumentException {
        when(petDao.getPetData(owner, name)).thenReturn(pet);
        PetEntity entity = service.getPetData(owner, name);
        assertSame(pet, entity, "Should return a pet entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetPetRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(petDao).getPetData(any(String.class), any(String.class));
            service.getPetData(owner, name);
        }, "Should return an exception when retrieving a pet fails");
    }

    @Test
    public void shouldReturnPetEntityListWhenGetSetOfPetsRetrieved() throws DatabaseAccessException, DocumentException {
        when(petDao.getAllPetsData(owner)).thenReturn(petList);
        List<Map<String, Object>> list = service.getAllPetsData(owner);
        assertSame(petList, list, "Should return a list of pet entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfPetsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(petDao).getAllPetsData(any(String.class));
            service.getAllPetsData(owner);
        }, "Should return an exception when retrieving a set of pets fails");
    }

    @Test
    public void shouldReturnPetSimpleFieldWhenPetFieldRetrieved() throws DatabaseAccessException, DocumentException {
        when(petDao.getSimpleField(owner, name, field)).thenReturn(value);
        Object obtainedValue = service.getSimpleField(owner, name, field);
        assertSame(value, obtainedValue, "Should return an Object");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetPetFieldRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(petDao).getSimpleField(any(String.class), any(String.class),
                any(String.class));
            service.getSimpleField(owner, name, field);
        }, "Should return an exception when retrieving a pet field fails");
    }

    @Test
    public void shouldReturnNothingWhenPetSimpleFieldUpdated() throws DatabaseAccessException, DocumentException {
        service.updateSimpleField(owner, name, field, value);
        verify(petDao).updateSimpleField(same(owner), same(name), same(field), same(value));
    }

    @Test
    public void shouldReturnNothingWhenFieldCollectionDeleted() throws DatabaseAccessException, DocumentException {
        service.deleteFieldCollection(owner, name, field);
        verify(petDao).deleteFieldCollection(same(owner), same(name), same(field));
    }

    @Test
    public void shouldReturnListWhenFieldCollectionRetrieved() throws DatabaseAccessException, DocumentException {
        when(petDao.getFieldCollection(anyString(), anyString(), anyString())).thenReturn(petList);
        List<Map<String, Object>> list = service.getFieldCollection(owner, name, field);
        assertSame(petList, list, "Should return a list of pet entities");
    }

    @Test
    public void shouldReturnListWhenFieldCollectionElementsBetweenKeysRetrieved() throws DatabaseAccessException,
        DocumentException {
        when(petDao.getFieldCollectionElementsBetweenKeys(anyString(), anyString(), anyString(), anyString(),
            anyString())).thenReturn(petList);
        List<Map<String, Object>> list = service.getFieldCollectionElementsBetweenKeys(owner, name, field, key1, key2);
        assertSame(petList, list, "Should return a list of pet entities");
    }

    @Test
    public void shouldReturnNothingWhenFieldCollectionElementAdded() throws DatabaseAccessException, DocumentException {
        service.addFieldCollectionElement(owner, name, field, key1, collectionElementBody);
        verify(petDao).addFieldCollectionElement(same(owner), same(name), same(field), same(key1),
            same(collectionElementBody));
    }

    @Test
    public void shouldReturnNothingWhenFieldCollectionElementDeleted() throws DatabaseAccessException,
        DocumentException {
        service.deleteFieldCollectionElement(owner, name, field, key1);
        verify(petDao).deleteFieldCollectionElement(same(owner), same(name), same(field), same(key1));
    }

    @Test
    public void shouldReturnNothingWhenFieldCollectionElementUpdated() throws DatabaseAccessException,
        DocumentException {
        service.updateFieldCollectionElement(owner, name, field, key1, collectionElementBody);
        verify(petDao).updateFieldCollectionElement(same(owner), same(name), same(field), same(key1),
            same(collectionElementBody));
    }

    @Test
    public void shouldReturnMapWhenFieldCollectionElementRetrieved() throws DatabaseAccessException,
        DocumentException {
        service.getFieldCollectionElement(owner, name, field, key1);
        verify(petDao).getFieldCollectionElement(same(owner), same(name), same(field), same(key1));
    }
}
