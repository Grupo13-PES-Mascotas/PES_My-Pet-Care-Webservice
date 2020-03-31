package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.entity.ImageEntity;
import org.pesmypetcare.webservice.form.StorageForm;

public interface StorageService {
    /**
     * Saves an image to the storage.
     * @param image The image entity to save
     */
    void saveImage(ImageEntity image);

    /**
     * Gets an image from the storage.
     * @param form The form with the request data
     * @return The image as a byte array
     */
    byte[] getImage(StorageForm form);

    /**
     * Deletes an image from the storage.
     * @param form The form with the request data
     */
    void deleteImage(StorageForm form);
}
