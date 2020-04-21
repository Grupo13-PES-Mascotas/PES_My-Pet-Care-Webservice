package org.pesmypetcare.webservice.controller.petmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.FreqTrainingEntity;
import org.pesmypetcare.webservice.service.FreqTrainingService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class PetFreqTrainingRestControllerTest {
    private static String jsonFreqTrainingEntity;
    private static String jsonField;
    private static FreqTrainingEntity freqTrainingEntity;
    private static List<Map<String, Object>> freqTrainingEntityList;
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
    private FreqTrainingService service;

    @BeforeAll
    public static void setUp() {
        lineBreak = "{\n";
        slash = "/";
        jsonFreqTrainingEntity = lineBreak
            + "  \"freqTrainingValue\": 5.3\n" + "}";
        jsonField = lineBreak
            + "  \"value\": 7.5\n" + "} ";
        freqTrainingEntity = new FreqTrainingEntity();
        freqTrainingEntityList = new ArrayList<>();
        owner = "Hola";
        petName = "Muy Buenas";
        date = "2018-10-22T00:47:00";
        date2 = "2020-10-22T00:47:00";
        urlBase = "/freqTraining";
    }

    @Test
    public void createFreqTrainingShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).createFreqTraining(anyString(), anyString(), anyString(), isA(FreqTrainingEntity.class));
        mockMvc.perform(post(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonFreqTrainingEntity))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteByDateShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteFreqTrainingByDate(anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteAllFreqTrainingsShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteAllFreqTrainings(anyString(), anyString());
        mockMvc.perform(delete(urlBase + slash + owner + slash + petName))
            .andExpect(status().isOk());
    }

    @Test
    public void getFreqTrainingDataShouldReturnFreqTrainingEntityAndStatusOk() throws Exception {
        willReturn(freqTrainingEntity).given(service).getFreqTrainingByDate(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName + slash + date))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllFreqTrainingDataShouldReturnFreqTrainingEntityListAndStatusOk() throws Exception {
        willReturn(freqTrainingEntityList).given(service).getAllFreqTraining(anyString(), anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllFreqTrainingsBetweenShouldReturnMealEntityListAndStatusOk() throws Exception {
        willReturn(freqTrainingEntityList).given(service).getAllFreqTrainingsBetween(anyString(), anyString(), anyString(),
            anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName + "/between/" + date + slash + date2))
            .andExpect(status().isOk());
    }

    @Test
    public void updateFreqTrainingShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).updateFreqTraining(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonField))
            .andExpect(status().isOk());
    }
}
