package org.pesmypetcare.webservice.service.appmanager;

import org.pesmypetcare.webservice.entity.appmanager.ImageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
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
    void saveUserImage(ImageEntity image);

    /**
     * Saves a pet image to the storage.
     * @param token The pet's owner personal access token
     * @param image The image to save
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the pet does not exist
     */
    void savePetImage(String token, ImageEntity image) throws DatabaseAccessException, DocumentException;

    /**
     * Saves a group image to the storage.
     * @param token The user personal access token
     * @param group The group name
     * @param image The image to save
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document group not exist
     */
    void saveGroupImage(String token, String group, ImageEntity image) throws DatabaseAccessException,
        DocumentException;

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
     * @return A map with pets names and the their images as a base64 encoded byte array
     * @throws DatabaseAccessException When an error occurs when accessing the database
     * @throws DocumentException When the pet does not exist
     */
    Map<String, String> getAllPetImages(String owner) throws DatabaseAccessException, DocumentException;

    /**
     * Downloads all the images from a forum folder.
     * @param group The group name
     * @param forum The forum name
     * @return A map with the images paths and their images as a base64 encoded byte array
     * @throws DatabaseAccessException When an error occurs when accessing the database
     * @throws DocumentException When the either the group or forum do not exist
     */
    Map<String, String> getAllPostsImagesFromForum(String group, String forum)
        throws DatabaseAccessException, DocumentException;
}
