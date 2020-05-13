package org.pesmypetcare.webservice.dao.appmanager;

import com.google.api.client.util.Base64;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.communitymanager.ForumDao;
import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class StorageDaoTest {
    private ImageEntity imageEntity;
    private StorageForm storageForm;
    private byte[] img;
    private String owner;
    private String entityPath;
    private String formPath;
    private String expectedDownload;
    private String petName;
    private Map<String, String> images;
    private List<Map<String, Object>> pets;

    @Mock
    private Bucket bucket;
    @Mock
    private PetDao petDao;
    @Mock
    private GroupDao groupDao;
    @Mock
    private ForumDao forumDao;
    @Mock
    private Blob blob;

    @InjectMocks
    private StorageDao dao = new StorageDaoImpl();

    @BeforeEach
    public void setUp() {
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
        given(bucket.create(anyString(), any(byte[].class))).willReturn(blob);

        dao.uploadImage(imageEntity);
        verify(bucket).create(entityPath, img);
    }

    @Test
    public void uploadPetImage() throws DatabaseAccessException, DocumentException {
        given(bucket.create(anyString(), any(byte[].class))).willReturn(blob);

        dao.uploadPetImage(owner, imageEntity);
        verify(petDao).updateSimpleField(owner, petName, "profileImageLocation", formPath);
        verify(bucket).create(formPath, img);
    }

    @Test
    public void uploadGroupImage() throws DatabaseAccessException, DocumentException {
        willDoNothing().given(groupDao).updateField(anyString(), anyString(), anyMap());
        given(bucket.create(anyString(), any(byte[].class))).willReturn(blob);

        dao.uploadGroupImage(imageEntity);
        verify(groupDao).updateField(same(imageEntity.getUid()), eq("icon"), isA(Map.class));
        verify(bucket).create(eq("Groups/" + imageEntity.getUid() + "/" + imageEntity.getImgName()),
            same(imageEntity.getImg()));
    }

    @Test
    public void uploadPostImage() {
        given(bucket.create(anyString(), any(byte[].class))).willReturn(blob);

        String group = "Dogs";
        String forum = "Huskies";
        dao.uploadPostImage(group, forum, imageEntity);
        verify(bucket).create(isA(String.class),
            same(imageEntity.getImg()));
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
        assertEquals(images, resultMap,
            "Should return a map with the pets names and their profile images as a base64 encoded string");
    }

    @Test
    public void downloadAllPostsImagesFromForum() throws DatabaseAccessException, DocumentException {
        List<String> imagesPaths = new ArrayList<>();
        imagesPaths.add("some/image/path");
        given(forumDao.getAllPostImagesPaths(anyString(), anyString())).willReturn(imagesPaths);
        given(bucket.get(anyString())).willReturn(blob);
        given(blob.getContent()).willReturn(img);

        Map<String, String> resultMap = dao.downloadAllPostsImagesFromForum("Dogs", "Huskies");
        images.clear();
        images.put("some/image/path", expectedDownload);
        assertEquals(images, resultMap,
            "Should return a map with the posts paths and their images as a base64 encoded string");
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

    private void initializeImageEntity() {
        imageEntity = new ImageEntity();
        imageEntity.setUid(owner);
        imageEntity.setImgName("Linux-image.png");
        img = "Some content".getBytes();
        imageEntity.setImg(img);
    }

    private void initializePetsList() {
        pets = new ArrayList<>();
        Map<String, Object> pet = new HashMap<>();
        pet.put("name", petName);
        PetEntity entity = new PetEntity();
        entity.setProfileImageLocation(formPath);
        pet.put("body", entity);
        pets.add(pet);
    }
}
