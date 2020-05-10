package org.pesmypetcare.webservice.controller.petmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.petmanager.GenderType;
import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
import org.pesmypetcare.webservice.service.petmanager.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Marc Simó
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class PetRestControllerTest {
    private static final String JSON_PET_ENTITY = "{\n"
        + "  \"gender\": \"Male\",\n"
        + "  \"breed\":\"Chihuahua\",\n"
        + "  \"birth\":\"2017-12-27T00:00:00\",\n"
        + "  \"pathologies\": \"amnesia\",\n"
        + "  \"needs\": \"vitaminC\",\n"
        + "  \"recommendedKcal\": 300.0\n"
        + "}";
    private static final String JSON_FIELD = "{\n"
        + "  \"value\": \"Other\"\n"
        + "} ";
    private static final String JSON_COLLECTION_FIELD = "{\n"
        + "  \"kcal\": 85.4,\n"
        + "  \"mealName\": \"MaMeal\"\n"
        + "} ";
    private static final PetEntity PET_ENTITY = new PetEntity();
    private static final List< Map<String, Object>> PET_LIST = new ArrayList<>();
    private static final Map<String, Object> PET_FIELD_ELEMENT = new HashMap<>();
    private static final String KEY_1 = "1996-01-08T12:20:30";
    private static final String KEY_2 = "1996-01-08T15:20:30";
    private static final String OWNER = "Pepe Lotas";
    private static final String PET_NAME = "ChihuahuaNator";
    private static final String FIELD = "gender";
    private static final GenderType VALUE = GenderType.Male;
    private static final String urlBase = "/pet";
    private static final String COLLECTION_FIELD = "meals";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService service;

    @Test
    public void createPetShouldReturnStatusCreated() throws Exception {
        willDoNothing().given(service).createPet(anyString(), anyString(), isA(PetEntity.class));
        mockMvc.perform(post(urlBase + "/" + OWNER + "/" + PET_NAME)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSON_PET_ENTITY))
            .andExpect(status().isCreated());
    }

    @Test
    public void deleteByOwnerAndNameShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).deleteByOwnerAndName(anyString(), anyString());
        mockMvc.perform(delete(urlBase + "/" + OWNER + "/" + PET_NAME)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteAllPetsShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).deleteAllPets(anyString());
        mockMvc.perform(delete(urlBase + "/" + OWNER))
            .andExpect(status().isNoContent());
    }

    @Test
    public void getPetDataShouldReturnPetEntityAndStatusOk() throws Exception {
        willReturn(PET_ENTITY).given(service).getPetData(anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + OWNER + "/" + PET_NAME))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllPetsDataShouldReturnPetEntityListAndStatusOk() throws Exception {
        willReturn(PET_LIST).given(service).getAllPetsData(anyString());
        mockMvc.perform(get(urlBase + "/" + OWNER))
            .andExpect(status().isOk());
    }

    @Test
    public void getSimpleFieldShouldReturnFieldValueAndStatusOk() throws Exception {
        willReturn(VALUE).given(service).getSimpleField(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + OWNER + "/" + PET_NAME + "/simple/" + FIELD))
            .andExpect(status().isOk());
    }

    @Test
    public void updateSimpleFieldShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).updateSimpleField(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(urlBase + "/" + OWNER + "/" + PET_NAME + "/simple/" + FIELD)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSON_FIELD))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteFieldCollectionShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).deleteFieldCollection(anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + "/" + OWNER + "/" + PET_NAME + "/collection/" + COLLECTION_FIELD))
            .andExpect(status().isNoContent());
    }

    @Test
    public void getFieldCollectionShouldReturnStatusOk() throws Exception {
        willReturn(PET_LIST).given(service).getFieldCollection(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + OWNER + "/" + PET_NAME + "/collection/" + COLLECTION_FIELD))
            .andExpect(status().isOk());
    }

    @Test
    public void getFieldCollectionElementsBetweenKeysShouldReturnStatusOk() throws Exception {
        willReturn(PET_LIST).given(service).getFieldCollectionElementsBetweenKeys(anyString(), anyString(),
            anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + OWNER + "/" + PET_NAME + "/collection/" +
            COLLECTION_FIELD + "/" + KEY_1
            + "/" + KEY_2))
            .andExpect(status().isOk());
    }

    @Test
    public void addFieldCollectionElementShouldReturnStatusCreated() throws Exception {
        willDoNothing().given(service).addFieldCollectionElement(anyString(), anyString(), anyString(), anyString(),
            any(Map.class));
        mockMvc.perform(post(urlBase + "/" + OWNER + "/" + PET_NAME + "/collection/" + COLLECTION_FIELD + "/"
            + KEY_1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSON_COLLECTION_FIELD))
            .andExpect(status().isCreated());
    }

    @Test
    public void deleteFieldCollectionElementShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).deleteFieldCollectionElement(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + "/" + OWNER + "/" + PET_NAME + "/collection/" +
            COLLECTION_FIELD + "/" + KEY_1))
            .andExpect(status().isNoContent());
    }

    @Test
    public void updateFieldCollectionElementShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).updateFieldCollectionElement(anyString(), anyString(), anyString(),
            anyString(), any(Map.class));
        mockMvc.perform(put(urlBase + "/" + OWNER + "/" + PET_NAME + "/collection/" + COLLECTION_FIELD + "/"
            + KEY_1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSON_COLLECTION_FIELD))
            .andExpect(status().isNoContent());
    }

    @Test
    public void getFieldCollectionElementShouldReturnStatusOk() throws Exception {
        willReturn(PET_FIELD_ELEMENT).given(service).getFieldCollectionElement(anyString(), anyString(), anyString(),
            anyString());
        mockMvc.perform(get(urlBase + "/" + OWNER + "/" + PET_NAME + "/collection/" +
            COLLECTION_FIELD + "/" + KEY_1))
            .andExpect(status().isOk());
    }
}
