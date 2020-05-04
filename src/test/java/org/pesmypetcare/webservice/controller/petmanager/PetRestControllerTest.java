package org.pesmypetcare.webservice.controller.petmanager;

import org.junit.jupiter.api.BeforeAll;
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
import java.util.List;
import java.util.Map;

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
    private static String jsonPetEntity;
    private static String jsonField;
    private static PetEntity petEntity;
    private static List< Map<String, Object>> petEntityList;
    private static String owner;
    private static String name;
    private static String field;
    private static GenderType value;
    private static String urlBase;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService service;

    @BeforeAll
    public static void setUp() {
        //language=JSON
        jsonPetEntity = "{\n"
            + "  \"gender\": \"Male\",\n"
            + "  \"breed\":\"Chihuahua\",\n"
            + "  \"birth\":\"2017-12-27T00:00:00\",\n"
            + "  \"pathologies\": \"amnesia\",\n"
            + "  \"needs\": \"vitaminC\",\n"
            + "  \"recommendedKcal\": 300.0\n"
            + "}";
        jsonField = "{\n" +
            "  \"value\": \"Other\"\n" +
            "} ";
        petEntity = new PetEntity();
        petEntityList = new ArrayList<>();
        owner = "Pepe Lotas";
        name = "ChihuahuaNator";
        field = "gender";
        value = GenderType.Male;
        urlBase = "/pet";
    }

    @Test
    public void createPetShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).createPet(anyString(), anyString(), isA(PetEntity.class));
        mockMvc.perform(post(urlBase + "/" + owner + "/" + name)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonPetEntity))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteByOwnerAndNameShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteByOwnerAndName(anyString(), anyString());
        mockMvc.perform(delete(urlBase + "/" + owner + "/" + name)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteAllPetsShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteAllPets(anyString());
        mockMvc.perform(delete(urlBase + "/" + owner ))
            .andExpect(status().isOk());
    }

    @Test
    public void getPetDataShouldReturnPetEntityAndStatusOk() throws Exception {
        willReturn(petEntity).given(service).getPetData(anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + name))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllPetsDataShouldReturnPetEntityListAndStatusOk() throws Exception {
        willReturn(petEntityList).given(service).getAllPetsData(anyString());
        mockMvc.perform(get(urlBase + "/" + owner))
            .andExpect(status().isOk());
    }

    @Test
    public void getFieldShouldReturnFieldValueAndStatusOk() throws Exception {
        willReturn(value).given(service).getSimpleField(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + name + "/" + field))
            .andExpect(status().isOk());
    }

    @Test
    public void updateFieldShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).updateSimpleField(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(urlBase + "/" + owner + "/" + name + "/" + field)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonField))
            .andExpect(status().isOk());
    }
}
