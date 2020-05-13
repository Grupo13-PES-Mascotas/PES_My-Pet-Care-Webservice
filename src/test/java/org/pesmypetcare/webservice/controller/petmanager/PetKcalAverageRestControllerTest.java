package org.pesmypetcare.webservice.controller.petmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.petmanager.KcalAverageEntity;
import org.pesmypetcare.webservice.service.petmanager.KcalAverageService;
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
public class PetKcalAverageRestControllerTest {
    private static String jsonKcalAverageEntity;
    private static String jsonField;
    private static KcalAverageEntity kcalAverageEntity;
    private static List<Map<String, Object>> kcalAverageEntityList;
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
    private KcalAverageService service;

    @BeforeAll
    public static void setUp() {
        lineBreak = "{\n";
        slash = "/";
        jsonKcalAverageEntity = lineBreak
            + "  \"kcalAverageValue\": 5.3\n" + "}";
        jsonField = lineBreak
            + "  \"value\": 7.5\n" + "} ";
        kcalAverageEntity = new KcalAverageEntity();
        kcalAverageEntityList = new ArrayList<>();
        owner = "Hola";
        petName = "Muy Buenas";
        date = "2018-10-22T00:47:00";
        date2 = "2020-10-22T00:47:00";
        urlBase = "/kcalAverage";
    }

    @Test
    public void createKcalAverageShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).createKcalAverage(anyString(), anyString(), anyString(),
            isA(KcalAverageEntity.class));
        mockMvc.perform(post(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonKcalAverageEntity))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteByDateShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteKcalAverageByDate(anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteAllKcalAveragesShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteAllKcalAverages(anyString(), anyString());
        mockMvc.perform(delete(urlBase + slash + owner + slash + petName))
            .andExpect(status().isOk());
    }

    @Test
    public void getKcalAverageDataShouldReturnKcalAverageEntityAndStatusOk() throws Exception {
        willReturn(kcalAverageEntity).given(service).getKcalAverageByDate(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName + slash + date))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllKcalAverageDataShouldReturnKcalAverageEntityListAndStatusOk() throws Exception {
        willReturn(kcalAverageEntityList).given(service).getAllKcalAverage(anyString(), anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllKcalAveragesBetweenShouldReturnMealEntityListAndStatusOk() throws Exception {
        willReturn(kcalAverageEntityList).given(service).getAllKcalAveragesBetween(anyString(), anyString(),
            anyString(), anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName + "/between/" + date + slash + date2))
            .andExpect(status().isOk());
    }

    @Test
    public void updateKcalAverageShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).updateKcalAverage(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonField))
            .andExpect(status().isOk());
    }
}
