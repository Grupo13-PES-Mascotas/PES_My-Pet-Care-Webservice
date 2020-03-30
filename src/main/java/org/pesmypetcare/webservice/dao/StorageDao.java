package org.pesmypetcare.webservice.dao;

public interface StorageDao {
    void uploadImage(String id, String imgName, byte[] img);

    byte[] downloadImage(String id, String imgName);
}
