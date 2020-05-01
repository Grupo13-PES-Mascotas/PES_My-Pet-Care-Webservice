package org.pesmypetcare.webservice.controller.petmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.petmanager.KcalEntity;
import org.pesmypetcare.webservice.service.petmanager.KcalService;
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


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class PetKcalRestControllerTest {
    private static String jsonKcalEntity;
    private static String jsonField;
    private static KcalEntity kcalEntity;
    private static List<Map<String, Object>> kcalEntityList;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static String urlBase;
    private static String slash;
    private static String lineBreak;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KcalService service;

    @BeforeAll
    public static void setUp() {
        lineBreak = "{\n";
        slash = "/";
        jsonKcalEntity = lineBreak
            + "  \"kcalValue\": 5.3\n" + "}";
        jsonField = lineBreak
            + "  \"value\": 7.5\n" + "} ";
        kcalEntity = new KcalEntity();
        kcalEntityList = new ArrayList<>();
        owner = "Hola";
        petName = "Muy Buenas";
        date = "2018-10-22T00:47:00";
        date2 = "2020-10-22T00:47:00";
        urlBase = "/kcal";
    }

    @Test
    public void createKcalShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).createKcal(anyString(), anyString(), anyString(), isA(KcalEntity.class));
        mockMvc.perform(post(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonKcalEntity))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteByDateShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteKcalByDate(anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteAllKcalsShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteAllKcals(anyString(), anyString());
        mockMvc.perform(delete(urlBase + slash + owner + slash + petName))
            .andExpect(status().isOk());
    }

    @Test
    public void getKcalDataShouldReturnKcalEntityAndStatusOk() throws Exception {
        willReturn(kcalEntity).given(service).getKcalByDate(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName + slash + date))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllKcalDataShouldReturnKcalEntityListAndStatusOk() throws Exception {
        willReturn(kcalEntityList).given(service).getAllKcal(anyString(), anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllKcalsBetweenShouldReturnMealEntityListAndStatusOk() throws Exception {
        willReturn(kcalEntityList).given(service).getAllKcalsBetween(anyString(), anyString(), anyString(),
            anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName + "/between/" + date + slash + date2))
            .andExpect(status().isOk());
    }

    @Test
    public void updateKcalShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).updateKcal(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonField))
            .andExpect(status().isOk());
    }
}
