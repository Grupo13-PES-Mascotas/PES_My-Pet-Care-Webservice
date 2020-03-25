package org.pesmypetcare.webservice.controller.usermanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {
    private static String urlBase;
    private static String username;
    private static UserEntity userEntity;
    private static String jsonEmail;
    private static String jsonPassword;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @BeforeAll
    public static void setUp() {
        urlBase = "/users";
        username = "PotatoMaster";
        userEntity = new UserEntity();
        jsonEmail = "{\n"
            + "  \"newEmail\": \"xavi@nanako.com\"\n"
            + "} ";
        jsonPassword = "{\n"
            + "  \"newPasswordl\": \"hiufdahs\"\n"
            + "} ";
    }

    @Test
    public void deleteAccountShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteById(anyString());
        mockMvc.perform(delete(urlBase + "/" + username + "/delete"))
            .andExpect(status().isOk());
    }

    @Test
    public void getUserDataShouldReturnUserDataAndStatusOk() throws Exception {
        willReturn(userEntity).given(service).getUserData(username);
        mockMvc.perform(get(urlBase + "/" + username))
            .andExpect(status().isOk());
    }

    @Test
    public void updateEmailShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).updateEmail(anyString(),anyString());
        mockMvc.perform(put(urlBase + "/" + username + "/update/email")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonEmail))
            .andExpect(status().isOk());
    }

    @Test
    public void updatePasswordShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).updatePassword(anyString(),anyString());
        mockMvc.perform(put(urlBase + "/" + username + "/update/password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonPassword))
            .andExpect(status().isOk());
    }
}
