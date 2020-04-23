package org.pesmypetcare.webservice.service.appmanager;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.entity.appmanager.ImageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.form.StorageForm;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class StorageServiceTest {
    private static ImageEntity imageEntity;
    private static StorageForm storageForm;
    private static String owner;
    private static String expectedDownload;
    private static Map<String, String> images;

    @Mock
    private StorageDao storageDao;

    @InjectMocks
    private StorageService service = new StorageServiceImpl();

    @BeforeAll
    public static void setUp() {
        imageEntity = new ImageEntity();
        imageEntity.setImgName("image.png");
        byte[] img = "Some content".getBytes();
        imageEntity.setImg(img);
        storageForm = new StorageForm("user/", "profile.png");
        owner = "user";
        expectedDownload = Base64.encodeBase64String(img);
        images = new HashMap<>();
        images.put("Linux", expectedDownload);
    }

    @Test
    public void saveImage() {
        service.saveImage(imageEntity);
        verify(storageDao).uploadImage(same(imageEntity));
    }

    @Test
    public void savePetImage() {
        service.savePetImage(owner, imageEntity);
        verify(storageDao).uploadPetImage(same(owner), same(imageEntity));
    }

    @Test
    public void getImage() {
        given(storageDao.downloadImage(same(storageForm))).willReturn(expectedDownload);
        String result = service.getImage(storageForm);
        assertEquals(expectedDownload, result, "Should return the image as a base64 encoded string");
    }

    @Test
    public void getAllImages() throws DatabaseAccessException {
        given(storageDao.downloadAllPetImages(same(owner))).willReturn(images);
        Map<String, String> resultMap = service.getAllImages(owner);
        assertEquals(images, resultMap, "Should return a map with the pets names ant their profile"
            + " image as a base64 encoded string");
    }

    @Test
    public void shouldThrowExceptionWhenDatabaseAccessFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            willThrow(DatabaseAccessException.class).given(storageDao).downloadAllPetImages(anyString());
            service.getAllImages(owner);
        }, "Should throw an exception when database access fails");
    }

    @Test
    public void deleteImage() {
        service.deleteImage(storageForm);
        verify(storageDao).deleteImage(storageForm);
    }
}
