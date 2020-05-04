package org.pesmypetcare.webservice.controller.petmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.GenderType;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.service.PetService;
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
    private static final String jsonPetEntity = "{\n"
        + "  \"gender\": \"Male\",\n"
        + "  \"breed\":\"Chihuahua\",\n"
        + "  \"birth\":\"2017-12-27T00:00:00\",\n"
        + "  \"pathologies\": \"amnesia\",\n"
        + "  \"needs\": \"vitaminC\",\n"
        + "  \"recommendedKcal\": 300.0\n"
        + "}";
    private static final String jsonField= "{\n"
        + "  \"value\": \"Other\"\n"
        + "} ";
    private static final String jsonCollectionField= "{\n"
        + "  \"kcal\": 85.4,\n"
        + "  \"mealName\": \"MaMeal\"\n"
        + "} ";
    private static final PetEntity petEntity = new PetEntity();
    private static final List< Map<String, Object>> petList = new ArrayList<>();
    private static final Map<String, Object> petFieldElement = new HashMap<>();
    private static final String key1 = "1996-01-08T12:20:30";
    private static final String key2 = "1996-01-08T15:20:30";
    private static final String owner = "Pepe Lotas";
    private static final String name = "ChihuahuaNator";
    private static final String field = "gender";
    private static final GenderType value = GenderType.Male;
    private static final String urlBase = "/pet";
    private static final String collectionField = "meals";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService service;

    @Test
    public void createPetShouldReturnStatusCreated() throws Exception {
        willDoNothing().given(service).createPet(anyString(), anyString(), isA(PetEntity.class));
        mockMvc.perform(post(urlBase + "/" + owner + "/" + name)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonPetEntity))
            .andExpect(status().isCreated());
    }

    @Test
    public void deleteByOwnerAndNameShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).deleteByOwnerAndName(anyString(), anyString());
        mockMvc.perform(delete(urlBase + "/" + owner + "/" + name)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteAllPetsShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).deleteAllPets(anyString());
        mockMvc.perform(delete(urlBase + "/" + owner ))
            .andExpect(status().isNoContent());
    }

    @Test
    public void getPetDataShouldReturnPetEntityAndStatusOk() throws Exception {
        willReturn(petEntity).given(service).getPetData(anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + name))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllPetsDataShouldReturnPetEntityListAndStatusOk() throws Exception {
        willReturn(petList).given(service).getAllPetsData(anyString());
        mockMvc.perform(get(urlBase + "/" + owner))
            .andExpect(status().isOk());
    }

    @Test
    public void getFieldShouldReturnFieldValueAndStatusOk() throws Exception {
        willReturn(value).given(service).getSimpleField(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + name + "/simple/" + field))
            .andExpect(status().isOk());
    }

    @Test
    public void updateFieldShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).updateSimpleField(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(urlBase + "/" + owner + "/" + name + "/simple/" + field)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonField))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteFieldCollectionShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).deleteFieldCollection(anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + "/" + owner + "/" + name + "/collection/" + field))
            .andExpect(status().isNoContent());
    }

    @Test
    public void getFieldCollectionShouldReturnStatusOk() throws Exception {
        willReturn(petList).given(service).getFieldCollection(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + name + "/collection/" + field))
            .andExpect(status().isOk());
    }

    @Test
    public void getFieldCollectionElementsBetweenKeysShouldReturnStatusOk() throws Exception {
        willReturn(petList).given(service).getFieldCollectionElementsBetweenKeys(anyString(), anyString(),
            anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + name + "/collection/" + field + "/" + key1
            + "/" + key2 ))
            .andExpect(status().isOk());
    }

    @Test
    public void addFieldCollectionElementShouldReturnStatusCreated() throws Exception {
        willDoNothing().given(service).addFieldCollectionElement(anyString(), anyString(), anyString(), anyString(),
            any(Map.class));
        mockMvc.perform(post(urlBase + "/" + owner + "/" + name + "/collection/" + collectionField + "/"
            + key1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonCollectionField))
            .andExpect(status().isCreated());
    }

    @Test
    public void deleteFieldCollectionElementShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).deleteFieldCollectionElement(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + "/" + owner + "/" + name + "/collection/" + field + "/" + key1))
            .andExpect(status().isNoContent());
    }

    @Test
    public void updateFieldCollectionElementShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).updateFieldCollectionElement(anyString(), anyString(), anyString(),
            anyString(), any(Map.class));
        mockMvc.perform(put(urlBase + "/" + owner + "/" + name + "/collection/" + collectionField + "/"
            + key1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonCollectionField))
            .andExpect(status().isNoContent());
    }

    @Test
    public void getFieldCollectionElementShouldReturnStatusOk() throws Exception {
        willReturn(petFieldElement).given(service).getFieldCollectionElement(anyString(), anyString(), anyString(),
            anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + name + "/collection/" + field + "/" + key1))
            .andExpect(status().isOk());
    }
}
