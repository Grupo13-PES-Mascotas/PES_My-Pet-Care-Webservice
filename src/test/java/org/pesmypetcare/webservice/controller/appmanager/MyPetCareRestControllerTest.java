package org.pesmypetcare.webservice.controller.appmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.service.usermanager.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Santiago Del Rey
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class MyPetCareRestControllerTest {
    private static String jsonUser1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @BeforeAll
    public static void setUp() {
        jsonUser1 = "{\n" + "  \"uid\": \"QABtiSlbB6NkXFCeDa4abRGSopT2\",\n"
            + "  \"email\": \"santi.mypetcare@gmail.com\",\n" + "  \"username\": \"Santiago Del Rey\",\n"
            + "  \"password\": \"123456789$\"\n" + "}";
    }

    @Test
    public void signUpShouldReturnStatusCreated() throws Exception {
        mockMvc.perform(post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonUser1))
            .andExpect(status().isCreated());
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
}
