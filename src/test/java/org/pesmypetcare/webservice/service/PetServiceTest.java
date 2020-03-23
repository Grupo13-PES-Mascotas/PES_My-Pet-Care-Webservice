package org.pesmypetcare.webservice.service;

import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {
    @Mock
    private PetDao petDao;

    @InjectMocks
    private PetService service = new PetServiceImpl();

    private List<PetEntity> pets;
    private PetEntity pet;
    private String owner;
    private String name;
    private String field;
    private GenderType value;

    @BeforeEach
    public void setUp() {
        pets = new ArrayList<>();
        pet = new PetEntity();
        owner = "OwnerUsername";
        name = "PetName";
        field = "birth";
        value = GenderType.Female;
    }

    @Test
    public void shouldReturnNothingWhenPetCreated() {
        service.createPet(owner, name, pet);
        verify(petDao).createPet(isA(String.class), isA(String.class), isA(PetEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenPetDeleted() {
        service.deleteByOwnerAndName(owner, name);
        verify(petDao).deleteByOwnerAndName(any(String.class), any(String.class));
    }

    @Test
    public void shouldReturnNothingWhenAllPetsDeleted() throws DatabaseAccessException {
        service.deleteAllPets(owner);
        verify(petDao).deleteAllPets(any(String.class));
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
        List<PetEntity> entityList = service.getAllPetsData(owner);
        assertSame(pets, entityList, "Should return a list of pet entities");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenGetSetOfPetsRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(petDao).getAllPetsData(any(String.class));
            service.getAllPetsData(owner);
        }, "Should return an exception when retrieving a set of pets fails");
    }

    @Test
    public void shouldReturnNothingWhenPetFieldUpdated() {
        service.updateField(owner, name, field, value);
        verify(petDao).updateField(any(String.class), any(String.class), any(String.class), any(Object.class));
    }

}
