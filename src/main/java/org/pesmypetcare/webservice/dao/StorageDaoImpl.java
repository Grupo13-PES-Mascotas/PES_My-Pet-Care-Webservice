package org.pesmypetcare.webservice.dao;

import com.google.cloud.storage.Bucket;
import org.pesmypetcare.webservice.entity.ImageEntity;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.pesmypetcare.webservice.form.StorageForm;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StorageDaoImpl implements StorageDao {
    private Bucket storageBucket;

    public StorageDaoImpl() {
        storageBucket = FirebaseFactory.getInstance().getStorage();
    }

    @Override
    public void uploadImage(ImageEntity imageEntity) {
        String path = imageEntity.getUid() + "/" + imageEntity.getImgName();
        byte[] img = imageEntity.getImg();
        storageBucket.create(path, img, "image/png");
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
    public List<byte[]> downloadAllPetImages(StorageForm form) {
        //TODO
        return null;
    }

    private String getImagePath(StorageForm form) {
        if (form.getPath().isEmpty()) {
            return form.getImageName();
        }
        return form.getPath() + "/" + form.getImageName();
    }
}
