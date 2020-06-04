package org.pesmypetcare.webservice.controller.medalsmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.medalmanager.UserMedalEntity;
import org.pesmypetcare.webservice.service.medalmanager.UserMedalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * @author Oriol Catalán
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserMedalRestControllerTest {
    
    private static final String JSON_FIELD = "{\n"
        + "  \"value\": 2.0\n"
        + "} ";

    private static final UserMedalEntity USER_MEDAL_ENTITY = new UserMedalEntity();
    private static final List<UserMedalEntity> USER_MEDAL_LIST = new ArrayList<>();
    private static final String OWNER = "Pepe Lotas";
    private static final String USER_MEDAL_NAME = "Walker";
    private static final String FIELD = "progress";
    private static final Double VALUE = 2.0;
    private static final String URL_BASE = "/usermedal";
    private static final String SLASH = "/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserMedalService service;

    @Test
    public void getUserMedalDataShouldReturnMedalEntityAndStatusOk() throws Exception {
        willReturn(USER_MEDAL_ENTITY).given(service).getUserMedalData(anyString(), anyString());
        mockMvc.perform(get(URL_BASE + SLASH + OWNER + SLASH + USER_MEDAL_NAME))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllUserMedalsDataShouldReturnMedalEntityListAndStatusOk() throws Exception {
        willReturn(USER_MEDAL_LIST).given(service).getAllUserMedalsData(anyString());
        mockMvc.perform(get(URL_BASE + SLASH + OWNER))
            .andExpect(status().isOk());
    }

    @Test
    public void getFieldShouldReturnFieldValueAndStatusOk() throws Exception {
        willReturn(VALUE).given(service).getField(anyString(), anyString(), anyString());
        mockMvc.perform(get(URL_BASE + SLASH + OWNER + SLASH + USER_MEDAL_NAME + SLASH + FIELD))
            .andExpect(status().isOk());
    }

    @Test
    public void updateFieldShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).updateField(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(URL_BASE + SLASH + OWNER + SLASH + USER_MEDAL_NAME + SLASH + FIELD)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSON_FIELD))
            .andExpect(status().isNoContent());
    }
}
