package org.pesmypetcare.webservice.controller.petmanager;

import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pet")
public class PetRestController {
    @Autowired
    private PetService petService;

    /**
     * Creates a pet on the data base.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param pet The pet entity that contains the attributes of the pet
     */
    @PostMapping("/{owner}/{name}")
    public void createPet(@PathVariable String owner, @PathVariable String name, @RequestBody PetEntity pet) {
        //petService.createPet(owner, name, pet);
    }
}
