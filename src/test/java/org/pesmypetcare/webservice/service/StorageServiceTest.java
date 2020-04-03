package org.pesmypetcare.webservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.StorageDao;
import org.pesmypetcare.webservice.entity.ImageEntity;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class StorageServiceTest {
    private static ImageEntity imageEntity;

    @Mock
    private StorageDao storageDao;

    @InjectMocks
    private StorageService service = new StorageServiceImpl();

    @BeforeAll
    public static void setUp() {
        imageEntity = new ImageEntity();
        imageEntity.setImgName("image.png");
    }

    @Test
    public void saveImage() {
        service.saveImage(imageEntity);
        verify(storageDao).uploadImage(same(imageEntity));
    }

    @Test
    public void savePetImage() {
    }

    @Test
    public void getImage() {
    }

    @Test
    public void deleteImage() {
    }

    @Test
    public void getAllImages() {
    }
}
