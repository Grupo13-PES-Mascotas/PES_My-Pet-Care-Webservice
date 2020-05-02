package org.pesmypetcare.webservice.dao.appmanager;

import com.google.api.client.util.Base64;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import org.pesmypetcare.webservice.dao.petmanager.PetDao;
import org.pesmypetcare.webservice.dao.petmanager.PetDaoImpl;
import org.pesmypetcare.webservice.entity.appmanager.ImageEntity;
import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.pesmypetcare.webservice.form.StorageForm;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@Repository
public class StorageDaoImpl implements StorageDao {
    private final String CONTENT_TYPE = "image/png";
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
        storageBucket.create(path, img, CONTENT_TYPE);
    }

    @Override
    public void uploadPetImage(String owner, ImageEntity image) {
        String imageName = image.getImgName();
        String path = image.getUid() + "/pets/" + image.getImgName();
        String name = imageName.substring(0, imageName.indexOf('-'));
        petDao.updateField(owner, name, "profileImageLocation", path);
        storageBucket.create(path, image.getImg(), CONTENT_TYPE);
    }

    @Override
    public String downloadImage(StorageForm form) {
        String image = getImagePath(form);
        byte[] img = storageBucket.get(image).getContent();
        return Base64.encodeBase64String(img);
    }

    @Override
    public void deleteImage(StorageForm form) {
        String image = getImagePath(form);
        storageBucket.get(image).delete();
    }

    @Override
    public void deleteImageByName(String imageName) {
        Blob image = storageBucket.get(imageName);
        if (image != null) {
            image.delete();
        }
    }

    @Override
    public Map<String, String> downloadAllPetImages(String owner) throws DatabaseAccessException {
        Map<String, String> result = new HashMap<>();
        List<Map<String, Object>> pets = petDao.getAllPetsData(owner);
        for (Map<String, Object> pet : pets) {
            PetEntity petEntity = (PetEntity) pet.get("body");
            String path = petEntity.getProfileImageLocation();
            getPetProfileImageIfItExists(result, pet, path);
        }
        return result;
    }

    /**
     * Obtain the image path.
     * @param form The request form
     * @return The path to the image
     */
    private String getImagePath(StorageForm form) {
        if (form.getPath().isEmpty()) {
            return form.getImageName();
        }
        return form.getPath() + "/" + form.getImageName();
    }

    /**
     * Gets the profile image ofDocument a pet if it exists.
     * @param response A map to store the pet name and its image
     * @param pet The pet data as a map
     * @param path The path to the image
     */
    private void getPetProfileImageIfItExists(Map<String, String> response, Map<String, Object> pet, String path) {
        if (path != null) {
            String name = (String) pet.get("name");
            byte[] image = storageBucket.get(path).getContent();
            String base64Image = Base64.encodeBase64String(image);
            response.put(name, base64Image);
        }
    }
}
