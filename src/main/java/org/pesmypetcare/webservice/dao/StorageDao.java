package org.pesmypetcare.webservice.dao;

import org.pesmypetcare.webservice.entity.ImageEntity;
import org.pesmypetcare.webservice.form.StorageForm;

import java.util.List;

public interface StorageDao {
    /**
     * Uploads an image to the storage.
     * @param image The image entity to upload
     */
    void uploadImage(ImageEntity image);

    /**
     * Downloads an image from the storage.
     * @param form The form with the request data
     * @return The image as a byte array
     */
    byte[] downloadImage(StorageForm form);

    /**
     * Deletes an image from the storage.
     * @param form The form with the request data
     */
    void deleteImage(StorageForm form);

    /**
     * Downloads all the images from the pets folder
     * @param form The form with the request data
     * @return A list with the images as a byte array
     */
    List<byte[]> downloadAllPetImages(StorageForm form);
}
