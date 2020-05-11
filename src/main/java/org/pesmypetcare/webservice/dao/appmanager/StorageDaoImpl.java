package org.pesmypetcare.webservice.dao.appmanager;

import com.google.api.client.util.Base64;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
import org.pesmypetcare.webservice.dao.petmanager.PetDao;
import org.pesmypetcare.webservice.entity.appmanager.ImageEntity;
import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.form.StorageForm;
import org.pesmypetcare.webservice.thirdpartyservices.FirebaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@Repository
public class StorageDaoImpl implements StorageDao {
    private Bucket storageBucket;
    @Autowired
    private PetDao petDao;
    @Autowired
    private GroupDao groupDao;

    public StorageDaoImpl() {
        storageBucket = FirebaseFactory.getInstance().getStorage();
    }

    public StorageDaoImpl(PetDao petDao) {
        storageBucket = FirebaseFactory.getInstance().getStorage();
        this.petDao = petDao;
    }

    @Override
    public void uploadImage(ImageEntity imageEntity) {
        String path = imageEntity.getUid() + "/" + imageEntity.getImgName();
        byte[] img = imageEntity.getImg();
        storageBucket.create(path, img);
    }

    @Override
    public void uploadPetImage(String owner, ImageEntity image) throws DatabaseAccessException, DocumentException {
        String imageName = image.getImgName();
        String path = image.getUid() + "/pets/" + imageName;
        String name = imageName.substring(0, imageName.indexOf('-'));
        petDao.updateSimpleField(owner, name, "profileImageLocation", path);
        storageBucket.create(path, image.getImg());
    }

    @Override
    public void uploadGroupImage(ImageEntity image) throws DatabaseAccessException, DocumentException {
        String imageName = image.getImgName();
        String path = "Groups/" + image.getUid() + "/" + imageName;
        Map<String, String> imageMap = new HashMap<>();
        imageMap.put("path", path);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", new Locale("es", "ES"));
        imageMap.put("lastModified", timeFormatter.format(LocalDateTime.now()));
        groupDao.updateField(image.getUid(), "icon", imageMap);
        storageBucket.create(path, image.getImg());
    }

    @Override
    public String uploadPostImage(String group, String forum, ImageEntity image) {
        String imageName = image.getImgName();
        String path = "Groups/" + group + "/" + forum + "/" + imageName;
        storageBucket.create(path, image.getImg());
        return path;
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
    public Map<String, String> downloadAllPetImages(String owner) throws DatabaseAccessException, DocumentException {
        Map<String, String> result = new HashMap<>();
        List<Map<String, Object>> pets = petDao.getAllPetsData(owner);
        for (Map<String, Object> pet : pets) {
            PetEntity petEntity = (PetEntity) pet.get("body");
            String path = petEntity.getProfileImageLocation();
            getPetProfileImageIfItExists(result, pet, path);
        }
        return result;
    }

    //TODO: downloadAllPostsImages_


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
     * Gets the profile image of a pet if it exists.
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
