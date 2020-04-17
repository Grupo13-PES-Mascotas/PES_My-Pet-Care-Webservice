package org.pesmypetcare.webservice.controller.petmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.petmanager.MealEntity;
import org.pesmypetcare.webservice.service.petmanager.MealService;
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
public class PetMealRestControllerTest {
    private static String jsonMealEntity;
    private static String jsonField;
    private static MealEntity mealEntity;
    private static List<Map<String, Object>> mealEntityList;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static String field;
    private static String value;
    private static String urlBase;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealService service;

    @BeforeAll
    public static void setUp() {
        jsonMealEntity = "{\n"
            + "  \"mealName\": \"Asparagus with milk\",\n"
            + "  \"kcal\": 60.0\n"
            + "}";
        jsonField = "{\n"
            + "  \"value\": \"Asparagus with chocolate\"\n"
            + "} ";
        mealEntity = new MealEntity();
        mealEntityList = new ArrayList<>();
        owner = "Manolo Manolon";
        petName = "Canpeque2";
        date = "2019-02-13T10:30:00";
        date2 = "2021-02-13T10:30:00";
        field = "mealName";
        value = "Asparagus with ketchup";
        urlBase = "/meal";
    }

    @Test
    public void createMealShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).createMeal(anyString(), anyString(), anyString(), isA(MealEntity.class));
        mockMvc.perform(post(urlBase + "/" + owner + "/" + petName + "/" + date)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonMealEntity))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteByDateShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteByDate(anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + "/" + owner + "/" + petName + "/" + date)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteAllMealsShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteAllMeals(anyString(), anyString());
        mockMvc.perform(delete(urlBase + "/" + owner + "/" + petName))
            .andExpect(status().isOk());
    }

    @Test
    public void getMealDataShouldReturnMealEntityAndStatusOk() throws Exception {
        willReturn(mealEntity).given(service).getMealData(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + petName + "/" + date))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllMealDataShouldReturnMealEntityListAndStatusOk() throws Exception {
        willReturn(mealEntityList).given(service).getAllMealData(anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + petName))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllMealsBetweenShouldReturnMealEntityListAndStatusOk() throws Exception {
        willReturn(mealEntityList).given(service).getAllMealsBetween(anyString(), anyString(), anyString(),
            anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + petName + "/between/" + date + "/" + date2))
            .andExpect(status().isOk());
    }

    @Test
    public void getMealFieldShouldReturnFieldValueAndStatusOk() throws Exception {
        willReturn(value).given(service).getMealField(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + petName + "/" + date + "/" + field))
            .andExpect(status().isOk());
    }

    @Test
    public void updateMealFieldShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).updateMealField(anyString(), anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(urlBase + "/" + owner + "/" + petName + "/" + date + "/" + field)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonField))
            .andExpect(status().isOk());
    }
}
