package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.dao.StorageDao;
import org.pesmypetcare.webservice.entity.ImageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.form.StorageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@Service
public class StorageServiceImpl implements StorageService {
    @Autowired
    private StorageDao storageDao;

    @Override
    public void saveImage(ImageEntity image) {
        storageDao.uploadImage(image);
    }

    @Override
    public void savePetImage(String owner, ImageEntity image) throws DatabaseAccessException, DocumentException {
        storageDao.uploadPetImage(owner, image);
    }

    @Override
    public String getImage(StorageForm form) {
        return storageDao.downloadImage(form);
    }

    @Override
    public Map<String, String> getAllImages(String owner) throws DatabaseAccessException, DocumentException {
        return storageDao.downloadAllPetImages(owner);
    }

    @Override
    public void deleteImage(StorageForm form) {
        storageDao.deleteImage(form);
    }
}
