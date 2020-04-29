package org.pesmypetcare.webservice.controller.appmanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.ImageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.form.StorageForm;
import org.pesmypetcare.webservice.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Santiago Del Rey
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class StorageRestControllerTest {
    private static final String BASE_URL = "/storage/image";
    private static final String TOKEN = "token";
    private static final String PETS_PICTURES_LOCATION = "/user/pets";
    private static final String ASSERT_MESSAGE = "Should return the image as a base64 encoded string";
    private static String myToken;
    private static String json;
    private static String form;
    private static String downloadExpectedResult;
    private static byte[] img;
    private static Map<String, String> images;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService service;

    @BeforeAll
    public static void setUp() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        json = mapper.writeValueAsString(new ImageEntity());
        form = mapper.writeValueAsString(new StorageForm());
        String content = "Some content";
        img = content.getBytes();
        downloadExpectedResult = Base64.encodeBase64String(img);
        images = new HashMap<>();
        images.put("Linux", downloadExpectedResult);
        myToken = "my-token";
    }

    @Test
    public void saveImage() throws Exception {
        willDoNothing().given(service).saveImage(any(ImageEntity.class));
        mockMvc.perform(put(BASE_URL)
            .header(TOKEN, myToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());
    }

    @Test
    public void savePetImage() throws Exception {
        willDoNothing().given(service).savePetImage(anyString(), any(ImageEntity.class));
        mockMvc.perform(put(BASE_URL + PETS_PICTURES_LOCATION)
            .header(TOKEN, myToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk());
    }

    @Test
    public void downloadImage() throws Exception {
        given(service.getImage(any(StorageForm.class))).willReturn(downloadExpectedResult);
        MvcResult response = mockMvc.perform(get(BASE_URL + "/user")
            .header(TOKEN, myToken)
            .contentType(MediaType.APPLICATION_JSON)
            .param("name", "profile.png"))
            .andExpect(status().isOk()).andReturn();
        String result = getContentAsString(response);
        assertEquals(downloadExpectedResult, result, ASSERT_MESSAGE);
    }

    @Test
    public void downloadPetImage() throws Exception {
        given(service.getImage(any(StorageForm.class))).willReturn(downloadExpectedResult);
        MvcResult response = mockMvc.perform(get(BASE_URL + "/user/pets/Toby-image.png")
            .header(TOKEN, myToken)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
        String result = getContentAsString(response);
        assertEquals(downloadExpectedResult, result, ASSERT_MESSAGE);
    }

    @Test
    public void downloadAllPetsImages() throws Exception {
        given(service.getAllImages(anyString())).willReturn(images);
        MvcResult response = mockMvc.perform(get(BASE_URL + PETS_PICTURES_LOCATION)
            .header(TOKEN, myToken)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();
        String result = getContentAsString(response);
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() { };
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> resultMap = mapper.readValue(result, typeRef);
        assertEquals(images, resultMap, "Should return a map with the pets names ant their profile"
            + " image as a base64 encoded string");
    }

    @Test
    public void shouldReturnErrorCode500WhenDatabaseFails() throws Exception {
        willThrow(DatabaseAccessException.class).given(service).getAllImages(anyString());
        mockMvc.perform(get(BASE_URL + PETS_PICTURES_LOCATION)
            .header(TOKEN, myToken)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
    }

    @Test
    public void deleteImage() throws Exception {
        willDoNothing().given(service).deleteImage(any(StorageForm.class));
        mockMvc.perform(delete(BASE_URL)
            .header(TOKEN, myToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(form))
            .andExpect(status().isOk());
    }

    private String getContentAsString(MvcResult response) throws UnsupportedEncodingException {
        return response.getResponse().getContentAsString();
    }
}
