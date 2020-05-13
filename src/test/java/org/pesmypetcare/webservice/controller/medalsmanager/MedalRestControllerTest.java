package org.pesmypetcare.webservice.controller.medalsmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.medalmanager.MedalEntity;
import org.pesmypetcare.webservice.service.medalmanager.MedalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Oriol Catalán
 */

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class MedalRestControllerTest {

    private static final MedalEntity MEDAL_ENTITY = new MedalEntity();
    private static final List<Map<String, Object>> MEDAL_LIST = new ArrayList<>();
    private static final String MEDAL_NAME = "Walker";
    private static final String FIELD = "description";
    private static final String VALUE = "You have to walk a lot of kilometers!";
    private static final String URL_BASE = "/medal";
    private static final String SLASH = "/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedalService service;

    @Test
    public void getUserMedalDataShouldReturnPetEntityAndStatusOk() throws Exception {
        willReturn(MEDAL_ENTITY).given(service).getMedalData(anyString());
        mockMvc.perform(get(URL_BASE + SLASH + MEDAL_NAME))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllUserMedalsDataShouldReturnUserMedalEntityListAndStatusOk() throws Exception {
        willReturn(MEDAL_LIST).given(service).getAllMedalsData();
        mockMvc.perform(get(URL_BASE))
            .andExpect(status().isOk());
    }

    @Test
    public void getSimpleFieldShouldReturnFieldValueAndStatusOk() throws Exception {
        willReturn(VALUE).given(service).getField(anyString(), anyString());
        mockMvc.perform(get(URL_BASE + SLASH + MEDAL_NAME + "/simple/" + FIELD))
            .andExpect(status().isOk());
    }
}
