package org.pesmypetcare.webservice.service.appmanager;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
import org.pesmypetcare.webservice.entity.appmanager.ImageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.form.StorageForm;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class StorageServiceTest {
    private static ImageEntity imageEntity;
    private static StorageForm storageForm;
    private static String owner;
    private static String groupName;
    private static String expectedDownload;
    private static Map<String, String> images;

    @Mock
    private StorageDao storageDao;
    @Mock
    private GroupDao groupDao;

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
        groupName = "Dogs";
    }

    @Test
    public void saveUserImage() {
        willDoNothing().given(storageDao).uploadImage(any(ImageEntity.class));
        service.saveUserImage(imageEntity);
        verify(storageDao).uploadImage(same(imageEntity));
    }

    @Test
    public void savePetImage() throws DatabaseAccessException, DocumentException {
        willDoNothing().given(storageDao).uploadPetImage(anyString(), any(ImageEntity.class));
        service.savePetImage(owner, imageEntity);
        verify(storageDao).uploadPetImage(same(owner), same(imageEntity));
    }

    @Test
    public void saveGroupImage() throws DatabaseAccessException, DocumentException {
        given(groupDao.groupNameInUse(anyString())).willReturn(true);
        willDoNothing().given(storageDao).uploadGroupImage(any(ImageEntity.class));
        service.saveGroupImage("my-token", groupName, imageEntity);
        verify(storageDao).uploadGroupImage(same(imageEntity));
    }

    @Test
    public void saveGroupImageShouldFailWhenTheGroupDoesNotExist() throws DatabaseAccessException {
        given(groupDao.groupNameInUse(anyString())).willReturn(false);
        assertThrows(DocumentException.class, () -> service.saveGroupImage("my-token", groupName, imageEntity),
            "Should throw an exception when the group does not exist.");
    }

    @Test
    public void getImage() {
        given(storageDao.downloadImage(same(storageForm))).willReturn(expectedDownload);
        String result = service.getImage(storageForm);
        assertEquals(expectedDownload, result, "Should return the image as a base64 encoded string");
    }

    @Test
    public void getAllPetImages() throws DatabaseAccessException, DocumentException {
        given(storageDao.downloadAllPetImages(same(owner))).willReturn(images);
        Map<String, String> resultMap = service.getAllPetImages(owner);
        assertEquals(images, resultMap,
            "Should return a map with the pets names and their profile images as a base64 encoded string");
    }

    @Test
    public void getAllPostImagesFromForum() throws DatabaseAccessException, DocumentException {
        given(storageDao.downloadAllPostsImagesFromForum(anyString(), anyString())).willReturn(images);
        Map<String, String> resultMap = service.getAllPostsImagesFromForum(groupName, "Huskies");
        assertEquals(images, resultMap,
            "Should return a map with the images paths and their images as a base64 encoded string");
    }

    @Test
    public void deleteImage() {
        service.deleteImage(storageForm);
        verify(storageDao).deleteImage(storageForm);
    }
}
