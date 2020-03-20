package org.pesmypetcare.webservice.controller.appmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.pesmypetcare.webservice.error.ErrorBody;
import org.pesmypetcare.webservice.securingservice.FirebaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MyPetCareRestControllerTest {
    private static String jsonUser1;
    private static String jsonUser2;
    private static ObjectMapper objectMapper;
    private static String url;
    private static String key;
    private static String password;
    private static boolean deleteUser1;
    private static boolean deleteUser2;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void resetFirebase() {
        if (deleteUser1) {
            deleteUser("user1");
            deleteUser1 = false;
        }
        if (deleteUser2) {
            deleteUser("user2");
            deleteUser2 = false;
        }
    }

    @BeforeAll
    public static void setUp() {
        jsonUser1 = "{\n"
            + "  \"username\": \"user1\",\n"
            + "  \"email\": \"user@mail.com\"\n"
            + "}";
        jsonUser2 = "{\n"
            + "  \"username\": \"user2\",\n"
            + "  \"email\": \"user@mail.com\"\n"
            + "}";
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        url = "/signup";
        password = "123456";
        key = "password";
        deleteUser1 = false;
    }

    @Test
    public void signUpShouldReturnStatusOK() throws Exception {
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonUser1).param(key, password))
            .andExpect(status().isOk());
        deleteUser1 = true;
    }

    @Test
    public void shouldNotPermitSignUpWithAnExistingUid() throws Exception {
        doSignIn(jsonUser1);
        String body = doSignInAndGetResponseBody(jsonUser1);
        String msg = getBodyMessage(body);
        assertEquals("Response message when signing up with a registered username",
            "The specified uid already exists", msg);
        deleteUser1 = true;
    }

    @Test
    public void shouldNotPermitSignUpWithAnExistingEmail() throws Exception {
        doSignIn(jsonUser1);
        String body = doSignInAndGetResponseBody(jsonUser2);
        String msg = getBodyMessage(body);
        assertEquals("Response message when signing up with a registered email",
            "The specified email is already in use", msg);
        deleteUser1 = true;
        deleteUser2 = true;
    }

    @Test
    public void shouldNotPermitSignUpWithAnInvalidEmail() throws Exception {
        String json = "{\n"
            + "  \"username\": \"user1\",\n"
            + "  \"email\": \"user@mail\"\n"
            + "}";
        String body = doSignInAndGetResponseBody(json);
        String msg = getBodyMessage(body);
        assertEquals("Response message when signing up with an invalid email",
            "The email is invalid", msg);
        deleteUser1 = true;
    }

    private static void deleteUser(String user) {
        try {
            FirebaseFactory.getInstance().getFirebaseAuth().deleteUser(user);
        } catch (FirebaseAuthException ignored) { }
    }

    private String doSignInAndGetResponseBody(String json) throws Exception {
        return mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json).param(key, password))
            .andReturn().getResponse().getContentAsString();
    }

    private String getBodyMessage(String body) throws com.fasterxml.jackson.core.JsonProcessingException {
        ErrorBody errorBody = objectMapper.readValue(body, ErrorBody.class);
        return errorBody.getMessage();
    }

    private void doSignIn(String json) throws Exception {
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json).param(key, password));
    }
}
