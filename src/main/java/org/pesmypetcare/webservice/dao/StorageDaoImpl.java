package org.pesmypetcare.webservice.dao;

import com.google.cloud.storage.Bucket;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;

public class StorageDaoImpl implements StorageDao {
    private Bucket storageBucket;

    public StorageDaoImpl() {
        storageBucket = FirebaseFactory.getInstance().getStorage();
    }

    @Override
    public void uploadImage(String id, String imgName, byte[] img) {
        storageBucket.create(id + "-" + imgName, img);
    }

    @Override
    public byte[] downloadImage(String id, String imgName) {
        return storageBucket.get(id + "-" + imgName).getContent();
    }
}
