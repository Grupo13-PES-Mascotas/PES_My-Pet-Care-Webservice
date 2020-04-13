package org.pesmypetcare.webservice.controller.petmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.MedicationEntity;
import org.pesmypetcare.webservice.service.MedicationService;
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
public class PetMedicationRestControllerTest {
    private static String jsonMedicationEntity;
    private static String jsonField;
    private static MedicationEntity medicationEntity;
    private static List<Map<String, Object>> medicationEntityList;
    private static String owner;
    private static String petName;
    private static String dateName;
    private static String date;
    private static String date2;
    private static String field;
    private static int value;
    private static String urlBase;
    private final String DASH = "/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicationService service;

    @BeforeAll
    public static void setUp() {
        jsonMedicationEntity = "{\n"
                + "  \"duration\": 2,\n"
                + "  \"periodicity\": 1\n"
                + "}";
        jsonField = "{\n"
                + "  \"quantity\": 3.0\n"
                + "} ";
        medicationEntity = new MedicationEntity();
        medicationEntityList = new ArrayList<>();
        owner = "Manolo";
        petName = "Canpeque2";
        dateName = "2019-02-13T10:30:00%Cloroform";
        date = "2019-02-13T10:30:00";
        date2 = "2021-02-13T10:30:00";
        field = "quantity";
        value = 3;
        urlBase = "/medication";
    }

    @Test
    public void createMedicationShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).createMedication(anyString(), anyString(), anyString(),
                isA(MedicationEntity.class));
        mockMvc.perform(post(urlBase + DASH + owner + DASH + petName + DASH + dateName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMedicationEntity))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteByDateAndNameShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteByDateAndName(anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + DASH + owner + DASH + petName + DASH + dateName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteAllMedicationsShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteAllMedications(anyString(), anyString());
        mockMvc.perform(delete(urlBase + DASH + owner + DASH + petName))
                .andExpect(status().isOk());
    }

    @Test
    public void getMedicationDataShouldReturnMedicationEntityAndStatusOk() throws Exception {
        willReturn(medicationEntity).given(service).getMedicationData(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + DASH + owner + DASH + petName + DASH + dateName))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllMedicationDataShouldReturnMedicationEntityListAndStatusOk() throws Exception {
        willReturn(medicationEntityList).given(service).getAllMedicationData(anyString(), anyString());
        mockMvc.perform(get(urlBase + DASH + owner + DASH + petName))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllMedicationsBetweenShouldReturnMedicationEntityListAndStatusOk() throws Exception {
        willReturn(medicationEntityList).given(service).getAllMedicationsBetween(anyString(), anyString(), anyString(),
                anyString());
        mockMvc.perform(get(urlBase + DASH + owner + DASH + petName + "/between/" + date + DASH + date2))
                .andExpect(status().isOk());
    }

    @Test
    public void getMedicationFieldShouldReturnFieldValueAndStatusOk() throws Exception {
        willReturn(value).given(service).getMedicationField(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + DASH + owner + DASH + petName + DASH + dateName + DASH + field))
                .andExpect(status().isOk());
    }

    @Test
    public void updateMedicationFieldShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).updateMedicationField(anyString(), anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(urlBase + DASH + owner + DASH + petName + DASH + dateName + DASH + field)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonField))
                .andExpect(status().isOk());
    }
}
