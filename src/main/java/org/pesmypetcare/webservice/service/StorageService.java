package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.entity.ImageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.form.StorageForm;

import java.util.Map;

/**
 * @author Santiago Del Rey
 */
public interface StorageService {
    /**
     * Saves an image to the storage.
     * @param image The image to save
     */
    void saveImage(ImageEntity image);

    /**
     * Saves a pet image to the storage.
     * @param owner The pet's owner
     * @param image The image save
     */
    void savePetImage(String owner, ImageEntity image);

    /**
     * Gets an image from the storage.
     * @param form The form with the request data
     * @return The image as a base64 encoded byte array
     */
    String getImage(StorageForm form);

    /**
     * Deletes an image from the storage.
     * @param form The form with the request data
     */
    void deleteImage(StorageForm form);

    /**
     * Downloads all the images from the pets folder.
     * @param owner The path with the requested data
     * @return A map with pets names and the their images as a byte array
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    Map<String, String> getAllImages(String owner) throws DatabaseAccessException;
}
