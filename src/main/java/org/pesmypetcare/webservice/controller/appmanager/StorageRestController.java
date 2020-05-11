package org.pesmypetcare.webservice.controller.appmanager;

import org.pesmypetcare.webservice.entity.appmanager.ImageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.form.StorageForm;
import org.pesmypetcare.webservice.service.appmanager.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@RestController
@RequestMapping("/storage")
public class StorageRestController {
    private final String TOKEN = "token";
    @Autowired
    private StorageService storage;

    /**
     * Saves an image in user storage.
     * @param token The personal access token of the user
     * @param image The image entity containing the image, its path and name
     */
    @PutMapping("/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveUserImage(@RequestHeader(TOKEN) String token, @RequestBody ImageEntity image) {
        storage.saveUserImage(image);
    }

    /**
     * Saves a pet image in user storage.
     * @param token The personal access token of the user
     * @param user The user's username
     * @param image The image entity containing the image, its path and name
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the pet does not exist
     */
    @PutMapping("/image/{user}/pets")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void savePetImage(@RequestHeader(TOKEN) String token, @PathVariable String user,
                             @RequestBody ImageEntity image) throws DatabaseAccessException, DocumentException {
        storage.savePetImage(user, image);
    }

    /**
     * Saves a group image to the storage.
     * @param token The user personal access token
     * @param group The group name
     * @param image The image to save
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document group not exist
     */
    @PutMapping("/image/{group}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveGroupImage(@RequestHeader(TOKEN) String token, @PathVariable String group,
                               @RequestBody ImageEntity image) throws DatabaseAccessException, DocumentException {
        storage.saveGroupImage(token, group, image);
    }

    /**
     * Downloads an image from user storage.
     * @param token The personal access token of the user
     * @param user The user's username
     * @param name The image name
     * @return The image as a base64 encoded byte array
     */
    @GetMapping(value = "/image/{user}")
    @ResponseBody
    public String downloadUserImage(@RequestHeader(TOKEN) String token, @PathVariable String user,
                                    @RequestParam String name) {
        StorageForm form = new StorageForm(user, name);
        return storage.getImage(form);
    }

    /**
     * Downloads a pet image from user storage.
     * @param token The personal access token of the user
     * @param user The user's username
     * @param name The image name
     * @return The image as a base64 encoded byte array
     */
    @GetMapping(value = "/image/{user}/pets/{name}")
    @ResponseBody
    public String downloadPetImage(@RequestHeader(TOKEN) String token, @PathVariable String user,
                                   @PathVariable String name) {
        String path = user + "/pets";
        StorageForm form = new StorageForm(path, name);
        return storage.getImage(form);
    }

    /**
     * Downloads a group image from storage.
     * @param group The group name
     * @param name The image name
     * @return The image as a base64 encoded byte array
     */
    @GetMapping(value = "/image/groups/{group}")
    @ResponseBody
    public String downloadGroupImage(@PathVariable String group,
                                   @RequestParam String name) {
        String path = "Groups/" + group;
        StorageForm form = new StorageForm(path, name);
        return storage.getImage(form);
    }

    /**
     * Downloads all pet profile pictures from user storage.
     * @param token The personal access token of the user
     * @param user The user's username
     * @return A map with the pets names and the their profile pictures as a byte array
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping(value = "/image/{user}/pets")
    @ResponseBody
    public Map<String, String> downloadAllPetsImages(@RequestHeader(TOKEN) String token, @PathVariable String user)
        throws DatabaseAccessException, DocumentException {
        return storage.getAllImages(user);
    }

    /**
     * Deletes an image from user storage.
     * @param token The personal access token of the user
     * @param storageForm A form with the image name and its path
     */
    @DeleteMapping("/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@RequestHeader(TOKEN) String token, @RequestBody StorageForm storageForm) {
        storage.deleteImage(storageForm);
    }
}
