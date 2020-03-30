package org.pesmypetcare.webservice.dao;

public class StorageDaoImpl implements StorageDao {
    @Override
    public void uploadImage(String id, String imgName, byte[] img) {

    }

    @Override
    public byte[] downloadImage(String id, String imgName) {
        return new byte[0];
    }
}
