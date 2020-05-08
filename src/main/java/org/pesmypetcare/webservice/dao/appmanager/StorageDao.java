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
     * @throws DocumentException When the document does not exist
     */
    void uploadPetImage(String owner, ImageEntity image) throws DatabaseAccessException, DocumentException;

    /**
     * Downloads an image from the storage.
     * @param form The form with the requested data
     * @return The image as a base64 encoded byte array
     */
    String downloadImage(StorageForm form);

    /**
     * Downloads all the images from the pets folder.
     * @param owner The path with the requested data
     * @return A map with pets names and the their images as a byte array
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    Map<String, String> downloadAllPetImages(String owner) throws DatabaseAccessException, DocumentException;

    /**
     * Deletes an image from the storage.
     * @param form The form with the requested data
     */
    void deleteImage(StorageForm form);

    /**
     * Deletes an image from the storage.
     * @param imageName The name ofDocument the image
     */
    void deleteImageByName(String imageName);
}
