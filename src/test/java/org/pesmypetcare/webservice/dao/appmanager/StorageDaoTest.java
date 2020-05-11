package org.pesmypetcare.webservice.dao.appmanager;

import com.google.api.client.util.Base64;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.petmanager.PetDao;
import org.pesmypetcare.webservice.entity.appmanager.ImageEntity;
import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.form.StorageForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class StorageDaoTest {
    private static final String CONTENT_TYPE = "image/png";
    private static ImageEntity imageEntity;
    private static StorageForm storageForm;
    private static byte[] img;
    private static String owner;
    private static String entityPath;
    private static String formPath;
    private static String expectedDownload;
    private static String petName;
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
        petName = "Linux";
        owner = "user";
        entityPath = "user/Linux-image.png";
        formPath = "user/pets/Linux-image.png";
        initializeImageEntity();
        storageForm = new StorageForm("user/pets", "Linux-image.png");
        expectedDownload = Base64.encodeBase64String(img);
        images = new HashMap<>();
        images.put(petName, expectedDownload);
        initializePetsList();
    }

    @Test
    public void uploadImage() {
        dao.uploadImage(imageEntity);
        verify(bucket).create(entityPath, img);
    }

    @Test
    public void uploadPetImage() throws DatabaseAccessException, DocumentException {
        dao.uploadPetImage(owner, imageEntity);
        verify(petDao).updateSimpleField(owner, petName, "profileImageLocation", formPath);
        verify(bucket).create(formPath, img);
    }

    @Test
    public void downloadImage() {
        given(bucket.get(formPath)).willReturn(blob);
        given(blob.getContent()).willReturn(img);
        String result = dao.downloadImage(storageForm);
        assertEquals(expectedDownload, result, "Should return the image as a base64 encoded string");
    }

    @Test
    public void downloadAllPetImages() throws DatabaseAccessException, DocumentException {
        given(petDao.getAllPetsData(owner)).willReturn(pets);
        given(bucket.get(formPath)).willReturn(blob);
        given(blob.getContent()).willReturn(img);
        Map<String, String> resultMap = dao.downloadAllPetImages(owner);
        assertEquals(images, resultMap, "Should return a map with the pets names ant their profile"
            + " image as a base64 encoded string");
    }

    @Test
    public void shouldThrowExceptionWhenDatabaseAccessFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            willThrow(DatabaseAccessException.class).given(petDao).getAllPetsData(anyString());
            dao.downloadAllPetImages(owner);
        }, "Should throw an exception when database access fails");
    }

    @Test
    public void deleteImage() {
        given(bucket.get(formPath)).willReturn(blob);
        dao.deleteImage(storageForm);
        verify(blob).delete();
    }

    @Test
    public void deleteImageByName() {
        given(bucket.get(entityPath)).willReturn(blob);
        dao.deleteImageByName(entityPath);
        verify(blob).delete();
    }

    private static void initializeImageEntity() {
        imageEntity = new ImageEntity();
        imageEntity.setUid(owner);
        imageEntity.setImgName("Linux-image.png");
        img = "Some content".getBytes();
        imageEntity.setImg(img);
    }

    private static void initializePetsList() {
        pets = new ArrayList<>();
        Map<String, Object> pet = new HashMap<>();
        pet.put("name", petName);
        PetEntity entity = new PetEntity();
        entity.setProfileImageLocation(formPath);
        pet.put("body", entity);
        pets.add(pet);
    }
}
