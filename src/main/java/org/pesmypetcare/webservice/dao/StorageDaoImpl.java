package org.pesmypetcare.webservice.dao;

import com.google.cloud.storage.Bucket;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

@Repository
public class StorageDaoImpl implements StorageDao {
    private Bucket storageBucket;

    public StorageDaoImpl() {
        storageBucket = FirebaseFactory.getInstance().getStorage();
    }

    @Override
    public void uploadImage(String imgName, byte[] img) {
        storageBucket.create(imgName, img);
    }

    @Override
    public byte[] downloadImage(String imgName) {
        return storageBucket.get(imgName).getContent();
    }
}
