package org.pesmypetcare.webservice.controller.appmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.pesmypetcare.webservice.error.ErrorBody;
import org.pesmypetcare.webservice.securingservice.FirebaseFactory;
import org.pesmypetcare.webservice.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class MyPetCareRestControllerTest {
    private String jsonUser1;
    private String jsonUser2;
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

   @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private MyPetCareRestController restController;

    @AfterEach
    public void resetFirebase() {
        try {
            deleteUser("user1");
            deleteUser("user2");
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
    }

    private void deleteUser(String user1) throws FirebaseAuthException {
        FirebaseFactory.getInstance().getFirebaseAuth().deleteUser(user1);
    }

    @BeforeEach
    void setUp() {
        jsonUser1 = "{\n" +
            "  \"username\": \"user1\",\n" +
            "  \"email\": \"user@mail.com\"\n" +
            "}";
        jsonUser2 = "{\n" +
            "  \"username\": \"user2\",\n" +
            "  \"email\": \"user@mail.com\"\n" +
            "}";
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void signUpShouldReturnStatusOK() throws Exception {
        doSignIn(jsonUser1);
    }

    @Test
    public void shouldNotPermitSignUpWithAnExistingUid() throws Exception {
        doSignIn(jsonUser1);
        String body = doSignInAndGetResponseBody(jsonUser1);
        String msg = getBodyMessage(body);
        assertEquals("Response message when signing up with a registered username",
            "The specified uid already exists", msg);
    }

    @Test
    public void shouldNotPermitSignUpWithAnExistingEmail() throws Exception {
        doSignIn(jsonUser1);
        String body = doSignInAndGetResponseBody(jsonUser2);
        String msg = getBodyMessage(body);
        assertEquals("Response message when signing up with a registered email",
            "The specified email is already in use", msg);
    }

    @Test
    public void shouldNotPermitSignUpWithAnInvalidEmail() throws Exception {
        String json = "{\n" +
            "  \"username\": \"user1\",\n" +
            "  \"email\": \"user@mail\"\n" +
            "}";
        String body = doSignInAndGetResponseBody(json);
        String msg = getBodyMessage(body);
        assertEquals("Response message when signing up with an invalid email",
            "The email is invalid", msg);
    }

    private String doSignInAndGetResponseBody(String json) throws Exception {
        return mockMvc.perform(post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json).param("password", "123456"))
            .andReturn().getResponse().getContentAsString();
    }

    private String getBodyMessage(String body) throws com.fasterxml.jackson.core.JsonProcessingException {
        ErrorBody errorBody = objectMapper.readValue(body, ErrorBody.class);
        return errorBody.getMessage();
    }

    private void doSignIn(String json) throws Exception {
        mockMvc.perform(post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json).param("password", "123456"));
    }
}
