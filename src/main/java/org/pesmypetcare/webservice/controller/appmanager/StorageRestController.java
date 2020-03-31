package org.pesmypetcare.webservice.controller.appmanager;

import org.pesmypetcare.webservice.entity.ImageEntity;
import org.pesmypetcare.webservice.form.StorageForm;
import org.pesmypetcare.webservice.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage")
public class StorageRestController {
    @Autowired
    private StorageService storage;

    @PutMapping("/image")
    public void saveImage(@RequestBody ImageEntity image) {
        storage.saveImage(image);
    }

    @GetMapping(value = "/image",
    produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] downloadImage(@RequestBody StorageForm storageForm) {
        return storage.getImage(storageForm);
    }

    @DeleteMapping("/image")
    public void deleteImage(@RequestBody StorageForm storageForm) {
        storage.deleteImage(storageForm);
    }
}
