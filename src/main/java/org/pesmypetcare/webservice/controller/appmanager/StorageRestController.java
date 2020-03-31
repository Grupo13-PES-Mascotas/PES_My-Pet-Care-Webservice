package org.pesmypetcare.webservice.controller.appmanager;

import org.pesmypetcare.webservice.entity.ImageEntity;
import org.pesmypetcare.webservice.form.StorageForm;
import org.pesmypetcare.webservice.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage")
public class StorageRestController {
    @Autowired
    private StorageService storage;

    @PutMapping("/image")
    public void saveImage(@RequestHeader("token") String token, @RequestBody ImageEntity image) {
        storage.saveImage(image);
    }

    @GetMapping(value = "/image/{user}",
    produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] downloadImage(@RequestHeader("token") String token, @PathVariable String user,
                                @RequestParam String name) {
        StorageForm form = new StorageForm(user, name);
        return storage.getImage(form);
    }

    @GetMapping(value = "/image/{user}/pets",
        produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] downloadPetImage(@RequestHeader("token") String token, @PathVariable String user,
                                   @RequestParam String name) {
        String path = user + "/pets";
        StorageForm form = new StorageForm(path, name);
        return storage.getImage(form);
    }

    @DeleteMapping("/image")
    public void deleteImage(@RequestHeader("token") String token, @RequestBody StorageForm storageForm) {
        storage.deleteImage(storageForm);
    }
}
