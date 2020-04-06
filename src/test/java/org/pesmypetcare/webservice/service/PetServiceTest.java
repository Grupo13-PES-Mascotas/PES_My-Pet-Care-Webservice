package org.pesmypetcare.webservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.PetDao;
import org.pesmypetcare.webservice.entity.GenderType;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {
    private static List<Map<String, Object>> pets;
    private static PetEntity pet;
    private static String owner;
    private static String name;
    private static String field;
    private static GenderType value;

    @Mock
    private PetDao petDao;

    @InjectMocks
    private PetService service = new PetServiceImpl();

    @BeforeAll
    public static void setUp() {
        pets = new ArrayList<>();
        pet = new PetEntity();
        owner = "OwnerUsername";
        name = "PetName";
        field = "gender";
        value = GenderType.Female;
    }

    @Test
    public void shouldReturnNothingWhenPetCreated() {
        service.createPet(owner, name, pet);
        verify(petDao).createPet(isA(String.class), isA(String.class), isA(PetEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenPetDeleted() throws DatabaseAccessException {
        service.deleteByOwnerAndName(owner, name);
        verify(petDao).deleteByOwnerAndName(isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllPetsDeleted() throws DatabaseAccessException {
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
    public void shouldReturnPetEntityWhenPetRetrieved() throws DatabaseAccessException {
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
    public void shouldReturnPetEntityListWhenGetSetOfPetsRetrieved() throws DatabaseAccessException {
        when(petDao.getAllPetsData(owner)).thenReturn(pets);
        List<Map<String, Object>> list = service.getAllPetsData(owner);
        assertSame(pets, list, "Should return a list of pet entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfPetsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(petDao).getAllPetsData(any(String.class));
            service.getAllPetsData(owner);
        }, "Should return an exception when retrieving a set of pets fails");
    }

    @Test
    public void shouldReturnPetFieldWhenPetFieldRetrieved() throws DatabaseAccessException {
        when(petDao.getField(owner, name, field)).thenReturn(value);
        Object obtainedValue = service.getField(owner, name, field);
        assertSame(value, obtainedValue, "Should return an Object");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetPetFieldRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(petDao).getField(any(String.class), any(String.class),
                any(String.class));
            service.getField(owner, name, field);
        }, "Should return an exception when retrieving a pet field fails");
    }

    @Test
    public void shouldReturnNothingWhenPetFieldUpdated() {
        service.updateField(owner, name, field, value);
        verify(petDao).updateField(isA(String.class), isA(String.class), isA(String.class), isA(Object.class));
    }
}
