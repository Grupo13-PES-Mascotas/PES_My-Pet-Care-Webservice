package org.pesmypetcare.webservice.dao.appmanager;

import org.pesmypetcare.webservice.entity.appmanager.ImageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.form.StorageForm;

import java.util.Map;

/**
 * @author Santiago Del Rey
 */
public interface StorageDao {
    /**
     * Uploads an image to the storage.
     * @param image The image to upload
     */
    void uploadImage(ImageEntity image);

    /**
     * Uploads a pet image to the storage.
     * @param owner The pet's owner
     * @param image The image to upload
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the pet does not exist
     */
    void uploadPetImage(String owner, ImageEntity image) throws DatabaseAccessException, DocumentException;

    /**
     * Uploads a group image to the storage.
     * @param image The image to upload
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the group does not exist
     */
    void uploadGroupImage(ImageEntity image) throws DatabaseAccessException, DocumentException;

    /**
     * Uploads a post image to the storage.
     * @param group The group name where the post is published
     * @param forum The forum name where the post is published
     * @param image The image to upload
     * @return The path to the image
     */
    String uploadPostImage(String group, String forum, ImageEntity image);

    /**
     * Downloads an image from the storage.
     * @param form The form with the requested data
     * @return The image as a base64 encoded byte array
     */
    String downloadImage(StorageForm form);

    /**
     * Downloads all the images from the pets folder.
     * @param owner The path with the requested data
     * @return A map with pets names and the their images as a base64 encoded byte array
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document pet does not exist
     */
    Map<String, String> downloadAllPetImages(String owner) throws DatabaseAccessException, DocumentException;

    /**
     * Downloads all the images from a forum folder.
     * @param group The group name
     * @param forum The forum name
     * @return A map with images paths and their images as a base64 encoded byte array
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the group or forum does not exist
     */
    Map<String, String> downloadAllPostsImagesFromForum(String group, String forum)
        throws DatabaseAccessException, DocumentException;

    /**
     * Deletes an image from the storage.
     * @param form The form with the requested data
     */
    void deleteImage(StorageForm form);

    /**
     * Deletes an image from the storage.
     * @param imageName The name of the image
     */
    void deleteImageByName(String imageName);
}
