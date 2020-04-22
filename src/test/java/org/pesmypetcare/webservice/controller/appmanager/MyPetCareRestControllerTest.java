package org.pesmypetcare.webservice.controller.appmanager;

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
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class MyPetCareRestControllerTest {
    private static String jsonUser1;
    private static String url;
    private static String key;
    private static String password;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @BeforeAll
    public static void setUp() {
        jsonUser1 = "{\n"
            + "  \"username\": \"user1\",\n"
            + "  \"email\": \"user@mail.com\"\n"
            + "}";
        password = "123456";
        key = "password";
    }

    @Test
    public void signUpShouldReturnStatusOK() throws Exception {
        willDoNothing().given(service).createUserAuth(isA(UserEntity.class), isA(String.class));
        mockMvc.perform(post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonUser1).param(key, password))
            .andExpect(status().isOk());
    }

    @Test
    public void existsUsernameShouldReturnTrueIfUsernameExists() throws Exception {
        given(service.existsUsername(anyString())).willReturn(true);
        MvcResult result = mockMvc.perform(get("/usernames")
            .param("username", "John"))
            .andExpect(status().isOk()).andReturn();

        assertEquals("{\"exists\":true}", result.getResponse().getContentAsString(),
            "Should return exists is true");
    }

    /*
    @Test
    public void shouldReturnBadRequestWhenFirebaseAuthFails() throws Exception {
        willThrow(FirebaseAuthException.class).given(service).createUserAuth(isA(UserEntity.class), isA(String.class));
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonUser1).param(key, password))
            .andExpect(status().isBadRequest());
    }
    */
}
