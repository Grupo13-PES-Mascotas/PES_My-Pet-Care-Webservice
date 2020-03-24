package org.pesmypetcare.webservice.controller.petmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class PetRestControllerTest {
    private static String jsonPetEntity;
    private static String owner;
    private static String name;
    private static String urlBase;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService service;

    @BeforeAll
    public static void setUp() {
        jsonPetEntity = "{\n" +
            "  \"gender\": \"Male\",\n" +
            "  \"breed\":\"Chihuahua\",\n" +
            "  \"weight\": 10,\n" +
            "  \"birth\":\"2017-12-27\",\n" +
            "  \"patologies\": \"amnesia\",\n" +
            "  \"recommendedKcal\": 300,\n" +
            "  \"washFreq\": 3\n" +
            "}";
        owner = "Pepe Lotas";
        name = "ChihuahuaNator";
        urlBase="/pet";
    }

    @Test
    public void createPetShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).createPet(anyString(), anyString(), isA(PetEntity.class));
        mockMvc.perform(post(urlBase+"/"+owner+"/"+name)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonPetEntity))
            .andExpect(status().isOk());
    }
}
