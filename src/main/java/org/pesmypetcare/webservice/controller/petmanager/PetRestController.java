package org.pesmypetcare.webservice.controller.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.petmanager.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pet")
public class PetRestController {
    @Autowired
    private PetService petService;

    /**
     * Creates a pet on the data base.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param name Name ofDocument the pet
     * @param pet The pet entity that contains the attributes ofDocument the pet
     */
    @PostMapping("/{owner}/{name}")
    public void createPet(@PathVariable String owner, @PathVariable String name, @RequestBody PetEntity pet) {
        petService.createPet(owner, name, pet);
    }

    /**
     * Deletes the pet with the specified owner and name from the database.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param name Name ofDocument the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{name}")
    public void deleteByOwnerAndName(@PathVariable String owner,
                                     @PathVariable String name) throws DatabaseAccessException {
        petService.deleteByOwnerAndName(owner, name);
    }

    /**
     * Deletes all the pets ofDocument the specified owner from database.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}")
    public void deleteAllPets(@PathVariable String owner) throws DatabaseAccessException {
        petService.deleteAllPets(owner);
    }

    /**
     * Gets a pet identified by its name and owner.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param name Name ofDocument the pet
     * @return The PetEntity ofDocument the owner pet data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{name}")
    public PetEntity getPetData(@PathVariable String owner, @PathVariable String name) throws DatabaseAccessException {
        return petService.getPetData(owner, name);
    }

    /**
     * Gets the data from all the specified pets from the database.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @return The List containing all the owner pets data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}")
    public List<Map<String, Object>> getAllPetsData(@PathVariable String owner) throws DatabaseAccessException {
        return petService.getAllPetsData(owner);
    }

    /**
     * Gets the value for the specified field ofDocument the pet on the database.
     * @param owner Username ofDocument the owner ofDocument the pets
     * @param name Name ofDocument the pet
     * @param field Name ofDocument the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{name}/{field}")
    public Object getField(@PathVariable String owner, @PathVariable String name,
                           @PathVariable String field) throws DatabaseAccessException {
        return petService.getField(owner, name, field);
    }

    /**
     * Updates the pet's field.
     * @param owner Username ofDocument the owner ofDocument the pet
     * @param name Name ofDocument the pet
     * @param field Name ofDocument the field to update
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *                key "value"
     */
    @PutMapping("/{owner}/{name}/{field}")
    public void updateField(@PathVariable String owner, @PathVariable String name, @PathVariable String field,
                            @RequestBody Map<String, Object> valueMap) {
        petService.updateField(owner, name, field, valueMap.get("value"));
    }
}
