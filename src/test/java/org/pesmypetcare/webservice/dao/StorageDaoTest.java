package org.pesmypetcare.webservice.dao;

import com.google.api.client.util.Base64;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.ImageEntity;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.form.StorageForm;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class StorageDaoTest {
    private static ImageEntity imageEntity;
    private static StorageForm storageForm;
    private static byte[] img;
    private static String owner;
    private static String path;
    private static String petPath;
    private static String expectedDownload;
    private static Map<String, String> images;
    private static List<Map<String, Object>> pets;

    @Mock
    private Bucket bucket;

    @Mock
    private PetDao petDao;

    @Mock
    private Blob blob;

    @InjectMocks
    private StorageDao dao = new StorageDaoImpl();

    @BeforeAll
    public static void setUp() {
        initializeImageEntity();
        storageForm = new StorageForm("user/pets", "Linux-image.png");
        owner = "user";
        path = "user/Linux-image.png";
        petPath = "user/pets/Linux-image.png";
        expectedDownload = Base64.encodeBase64String(img);
        images = new HashMap<>();
        images.put("Linux", expectedDownload);
        initializePetsList();
    }

    @Test
    public void uploadImage() {
        dao.uploadImage(imageEntity);
        verify(bucket).create(path, img, "image/png");
    }

    @Test
    public void uploadPetImage() {
        dao.uploadPetImage(owner, imageEntity);
        verify(petDao).updateField(owner, "Linux", "profileImageLocation", petPath);
        verify(bucket).create(petPath, img, "image/png");
    }

    @Test
    public void downloadImage() {
        given(bucket.get(petPath)).willReturn(blob);
        given(blob.getContent()).willReturn(img);
        String result = dao.downloadImage(storageForm);
        assertEquals(expectedDownload, result, "Should return the image as a base64 encoded string");
    }

    @Test
    public void downloadAllPetImages() throws DatabaseAccessException {
        given(petDao.getAllPetsData(owner)).willReturn(pets);
        given(bucket.get(petPath)).willReturn(blob);
        given(blob.getContent()).willReturn(img);
        Map<String, String> resultMap = dao.downloadAllPetImages(owner);
        assertEquals(images, resultMap, "Should return a map with the pets names ant their profile"
            + " image as a base64 encoded string");
    }

    @Test
    public void deleteImage() {
    }

    @Test
    public void deleteImageByName() {
    }

    private static void initializeImageEntity() {
        imageEntity = new ImageEntity();
        imageEntity.setUid("user");
        imageEntity.setImgName("Linux-image.png");
        img = "Some content".getBytes();
        imageEntity.setImg(img);
    }

    private static void initializePetsList() {
        pets = new ArrayList<>();
        Map<String, Object> pet = new HashMap<>();
        pet.put("name", "Linux");
        PetEntity entity = new PetEntity();
        entity.setProfileImageLocation(petPath);
        pet.put("body", entity);
        pets.add(pet);
    }
}
