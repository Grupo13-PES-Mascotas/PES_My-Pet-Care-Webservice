package org.pesmypetcare.webservice.controller.petmanager;

import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        petService.createPet(owner, name, pet);
    }

    /**
     * Deletes the pet with the specified owner and name from the database.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     */
    @DeleteMapping("/{owner}/{name}")
    public void deleteByOwnerAndName(@PathVariable String owner, @PathVariable String name) {
        petService.deleteByOwnerAndName(owner, name);
    }

    /**
     * Deletes all the pets of the specified owner from database.
     * @param owner Username of the owner of the pets
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}")
    public void deleteAllPets(@PathVariable String owner) throws DatabaseAccessException {
        petService.deleteAllPets(owner);
    }

    /**
     * Gets a pet identified by its name and owner.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @return The PetEntity of the users data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{name}")
    public PetEntity getPetData(@PathVariable String owner, @PathVariable String name) throws DatabaseAccessException {
        return petService.getPetData(owner, name);
    }

    /**
     * Gets the data from all the specified pets from the database.
     * @param owner Username of the owner of the pets
     * @return The PetEntity of the users data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}")
    public List<PetEntity> getAllPetsData(@PathVariable String owner) throws DatabaseAccessException {
        return petService.getAllPetsData(owner);
    }
}
