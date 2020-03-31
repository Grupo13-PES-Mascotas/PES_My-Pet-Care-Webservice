package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.dao.StorageDao;
import org.pesmypetcare.webservice.entity.ImageEntity;
import org.pesmypetcare.webservice.form.StorageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageServiceImpl implements StorageService {
    @Autowired
    private StorageDao storageDao;

    @Override
    public void saveImage(ImageEntity image) {
        storageDao.uploadImage(image);
    }

    @Override
    public byte[] getImage(StorageForm form) {
        return storageDao.downloadImage(form);
    }

    @Override
    public void deleteImage(StorageForm form) {
        storageDao.deleteImage(form);
    }
}
