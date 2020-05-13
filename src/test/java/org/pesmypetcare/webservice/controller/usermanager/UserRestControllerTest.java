package org.pesmypetcare.webservice.controller.usermanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.service.usermanager.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Santiago Del Rey
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {
    private static final String URL = "/users/John";
    private static String username;
    private static UserEntity userEntity;
    private static String jsonUpdate;
    private static String token;
    private static String myToken;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @BeforeAll
    public static void setUp() {
        username = "John";
        token = "token";
        myToken = "my-token";
        userEntity = new UserEntity();
        jsonUpdate = "{\n" + "  \"email\": \"xavi@nanako.com\"\n" + "} ";
    }

    @Test
    public void deleteAccountShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).deleteById(anyString(), anyString());
        mockMvc.perform(delete(URL).header(token, myToken)).andExpect(status().isNoContent());
    }

    @Test
    public void getUserDataShouldReturnUserDataAndStatusOk() throws Exception {
        willReturn(userEntity).given(service).getUserData(token, username);
        mockMvc.perform(get(URL).header(token, myToken)).andExpect(status().isOk());
    }

    @Test
    public void updateFieldShouldReturnStatusNoContent() throws Exception {
        willDoNothing().given(service).updateField(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(jsonUpdate).header(token, myToken))
            .andExpect(status().isNoContent());
    }

    @Test
    public void getUserSubscriptions() throws Exception {
        List<String> subscriptions = new ArrayList<>();
        subscriptions.add("Dogs");
        willReturn(subscriptions).given(service).getUserSubscriptions(myToken, username);
        MvcResult mvcResult = mockMvc.perform(get("/users/subscriptions").header(token, myToken)
            .param("username", username)).andExpect(status().isOk()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertEquals("Should return all the group subscriptions of the user.",
            new ObjectMapper().writeValueAsString(subscriptions), result);
    }
}
