package org.pesmypetcare.webservice.controller.appmanager;

import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.pesmypetcare.webservice.dao.UserDaoImpl;
import org.pesmypetcare.webservice.securingservice.FirebaseFactory;
import org.pesmypetcare.webservice.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MyPetCareRestControllerTest {
    String json;

    @Autowired
    private MockMvc mockMvc;

   @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private MyPetCareRestController restController;

    @AfterEach
    public void resetFirebase() {
        try {
            FirebaseFactory.getInstance().getFirebaseAuth().deleteUser("user");
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        json = "{\n" +
            "  \"username\": \"user\",\n" +
            "  \"email\": \"user@mail.com\"\n" +
            "}";
    }

    @Test
    public void signUpShouldReturnStatusOK() throws Exception {
        doSignIn(json);
    }

    @Test
    void shouldNotPermitSignUpWithAnExistingUid() throws Exception {
        //TODO
        Assertions.assertThrows(FirebaseAuthException.class, () -> {
            doSignIn(json);
            doSignIn(json);
        });
    }

    private void doSignIn(String json) throws Exception {
        mockMvc.perform(post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json).param("password", "123456"));
    }
}
