package org.pesmypetcare.webservice.controller.appmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.UserDao;
import org.pesmypetcare.webservice.dao.UserDaoImpl;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.ErrorBody;
import org.pesmypetcare.webservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class MyPetCareRestControllerTest {
    private static String jsonUser1;
    private static String jsonUser2;
    private static ObjectMapper objectMapper;
    private static String url;
    private static String key;
    private static String password;
    private static boolean deleteUser1;
    private static boolean deleteUser2;
    private static UserDao dao;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

   /* @InjectMocks
    private MyPetCareRestController restController = new MyPetCareRestController();*/

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
        jsonUser1 = "{\n  \"username\": \"user1\",\n"
            + "  \"email\": \"user@mail.com\"\n"
            + "}";
        jsonUser2 = "{\n  \"username\": \"user2\",\n"
            + "  \"email\": \"user@mail.com\"\n"
            + "}";
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        url = "/signup";
        password = "123456";
        key = "password";
        deleteUser1 = false;
        dao = new UserDaoImpl();
    }

    @Test
    public void signUpShouldReturnStatusOK() throws Exception {
        willDoNothing().given(service).createUserAuth(isA(UserEntity.class), isA(String.class));
        mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonUser1).param(key, password))
            .andExpect(status().isOk());
        //deleteUser1 = true;
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
            dao.deleteById(user);
        } catch (DatabaseAccessException | FirebaseAuthException ignored) {
            //Ignore exception
        }
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
