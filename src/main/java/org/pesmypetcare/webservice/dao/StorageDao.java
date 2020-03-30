package org.pesmypetcare.webservice.dao;


public interface StorageDao {
    /**
     * Uploads an image to Firebase Storage.
     * @param imgName The image name
     * @param img The image as a byte array
     */
    void uploadImage(String imgName, byte[] img);

    /**
     * Downloads an image from Firebase Storage.
     * @param imgName The image name
     * @return The image as a byte array
     */
    byte[] downloadImage(String imgName);
}
