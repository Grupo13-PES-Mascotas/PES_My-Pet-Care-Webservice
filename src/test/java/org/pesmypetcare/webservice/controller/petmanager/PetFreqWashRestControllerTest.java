package org.pesmypetcare.webservice.controller.petmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.petmanager.FreqWashEntity;
import org.pesmypetcare.webservice.service.petmanager.FreqWashService;
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
public class PetFreqWashRestControllerTest {
    private static String jsonFreqWashEntity;
    private static String jsonField;
    private static FreqWashEntity freqWashEntity;
    private static List<Map<String, Object>> freqWashEntityList;
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
    private FreqWashService service;

    @BeforeAll
    public static void setUp() {
        lineBreak = "{\n";
        slash = "/";
        jsonFreqWashEntity = lineBreak
            + "  \"freqWashValue\": 5.3\n" + "}";
        jsonField = lineBreak
            + "  \"value\": 7.5\n" + "} ";
        freqWashEntity = new FreqWashEntity();
        freqWashEntityList = new ArrayList<>();
        owner = "Hola";
        petName = "Muy Buenas";
        date = "2018-10-22T00:47:00";
        date2 = "2020-10-22T00:47:00";
        urlBase = "/freqWash";
    }

    @Test
    public void createFreqWashShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).createFreqWash(anyString(), anyString(), anyString(), isA(FreqWashEntity.class));
        mockMvc.perform(post(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonFreqWashEntity))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteByDateShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteFreqWashByDate(anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteAllFreqWashsShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteAllFreqWashs(anyString(), anyString());
        mockMvc.perform(delete(urlBase + slash + owner + slash + petName))
            .andExpect(status().isOk());
    }

    @Test
    public void getFreqWashDataShouldReturnFreqWashEntityAndStatusOk() throws Exception {
        willReturn(freqWashEntity).given(service).getFreqWashByDate(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName + slash + date))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllFreqWashDataShouldReturnFreqWashEntityListAndStatusOk() throws Exception {
        willReturn(freqWashEntityList).given(service).getAllFreqWash(anyString(), anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllFreqWashsBetweenShouldReturnMealEntityListAndStatusOk() throws Exception {
        willReturn(freqWashEntityList).given(service).getAllFreqWashsBetween(anyString(), anyString(), anyString(),
            anyString());
        mockMvc.perform(get(urlBase + slash + owner + slash + petName + "/between/" + date + slash + date2))
            .andExpect(status().isOk());
    }

    @Test
    public void updateFreqWashShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).updateFreqWash(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(urlBase + slash + owner + slash + petName + slash + date)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonField))
            .andExpect(status().isOk());
    }
}
