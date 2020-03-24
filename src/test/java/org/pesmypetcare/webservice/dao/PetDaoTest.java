package org.pesmypetcare.webservice.dao;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.pesmypetcare.webservice.entity.GenderType;
import org.pesmypetcare.webservice.entity.PetEntity;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class PetDaoTest {
    @Mock
    private Firestore db;

    @InjectMocks
    private PetDao petDao = new PetDaoImpl();

    @Mock
    private CollectionReference usersRef;

    @Mock
    private CollectionReference petsRef;

    @Mock
    private DocumentReference ownerRef;

    @Mock
    private DocumentReference petRef;

    private List<PetEntity> pets;
    private PetEntity pet;
    private String owner;
    private String name;
    private String field;
    private GenderType value;

    @BeforeEach
    void setUp() {
        pets = new ArrayList<>();
        pet = new PetEntity();
        owner = "OwnerUsername";
        name = "PetName";
        field = "gender";
        value = GenderType.Other;

        given(db.collection(same("users"))).willReturn(usersRef);
        given(usersRef.document(same(owner))).willReturn(ownerRef);
        given(ownerRef.collection(same("pets"))).willReturn(petsRef);
        given(petsRef.document(same(name))).willReturn(petRef);
    }

    @Test
    void shouldCreatePetOnDatabase() {
        petDao.createPet(owner, name, pet);
        verify(petsRef).document(same(name));
    }

    @Test
    void deleteByOwnerAndName() {
    }

    @Test
    void deleteAllPets() {
    }

    @Test
    void getPetData() {
    }

    @Test
    void getAllPetsData() {
    }

    @Test
    void updateField() {
    }
}
