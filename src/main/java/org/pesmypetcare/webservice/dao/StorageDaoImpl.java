package org.pesmypetcare.webservice.dao;

import com.google.cloud.storage.Bucket;
import org.pesmypetcare.webservice.entity.ImageEntity;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.pesmypetcare.webservice.form.StorageForm;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StorageDaoImpl implements StorageDao {
    private Bucket storageBucket;
    private PetDao petDao;

    public StorageDaoImpl() {
        storageBucket = FirebaseFactory.getInstance().getStorage();
        petDao = new PetDaoImpl();
    }

    public StorageDaoImpl(PetDao petDao) {
        storageBucket = FirebaseFactory.getInstance().getStorage();
        this.petDao = petDao;
    }

    @Override
    public void uploadImage(ImageEntity imageEntity) {
        String path = imageEntity.getUid() + "/" + imageEntity.getImgName();
        byte[] img = imageEntity.getImg();
        storageBucket.create(path, img, "image/png");
    }

    @Override
    public void uploadPetImage(String owner, ImageEntity image) {
        String imageName = image.getImgName();
        String path = image.getUid() + "/pets/" + image.getImgName();
        String name = imageName.substring(0, imageName.indexOf("-"));
        petDao.updateField(owner, name, "profileImageLocation", path);
        storageBucket.create(path, image.getImg(), "image/png");
    }

    @Override
    public byte[] downloadImage(StorageForm form) {
        String image= getImagePath(form);
        return storageBucket.get(image).getContent();
    }

    @Override
    public void deleteImage(StorageForm form) {
        String image = getImagePath(form);
        storageBucket.get(image).delete();
    }

    @Override
    public void deleteImageByName(String imageName) {
        storageBucket.get(imageName).delete();
    }

    @Override
    public Map<String, byte[]> downloadAllPetImages(String owner) throws DatabaseAccessException {
        Map<String, byte[]> result = new HashMap<>();
        List<Map<String, Object>> pets = petDao.getAllPetsData(owner);
        for (Map<String, Object> pet : pets) {
            PetEntity petEntity = (PetEntity) pet.get("body");
            String path = petEntity.getProfileImageLocation();
            getProfileImageIfItExists(result, pet, path);
        }
        return result;
    }

    private String getImagePath(StorageForm form) {
        if (form.getPath().isEmpty()) {
            return form.getImageName();
        }
        return form.getPath() + "/" + form.getImageName();
    }

    private void getProfileImageIfItExists(Map<String, byte[]> result, Map<String, Object> pet, String path) {
        if (path != null) {
            String name = (String) pet.get("name");
            byte[] image = storageBucket.get(path).getContent();
            result.put(name, image);
        }
    }
}
