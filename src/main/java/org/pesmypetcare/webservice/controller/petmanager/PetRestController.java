package org.pesmypetcare.webservice.controller.petmanager;

import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param pet The pet entity that contains the attributes of the pet
     */
    @PostMapping("/{owner}/{name}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPet(@PathVariable String owner, @PathVariable String name, @RequestBody PetEntity pet) {
        pet.initializeMaps();
        petService.createPet(owner, name, pet);
    }

    /**
     * Deletes the pet with the specified owner and name from the database.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByOwnerAndName(@PathVariable String owner,
                                     @PathVariable String name) throws DatabaseAccessException {
        petService.deleteByOwnerAndName(owner, name);
    }

    /**
     * Deletes all the pets of the specified owner from database.
     * @param owner Username of the owner of the pets
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllPets(@PathVariable String owner) throws DatabaseAccessException {
        petService.deleteAllPets(owner);
    }

    /**
     * Gets a pet identified by its name and owner.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @return The PetEntity of the owner pet data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{name}")
    @ResponseStatus(HttpStatus.OK)
    public PetEntity getPetData(@PathVariable String owner, @PathVariable String name) throws DatabaseAccessException {
        return petService.getPetData(owner, name);
    }

    /**
     * Gets the data from all the specified pets from the database.
     * @param owner Username of the owner of the pets
     * @return The List containing all the owner pets data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> getAllPetsData(@PathVariable String owner) throws DatabaseAccessException {
        return petService.getAllPetsData(owner);
    }

    /**
     * Gets the value for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{name}/simple/{field}")
    @ResponseStatus(HttpStatus.OK)
    public Object getSimpleField(@PathVariable String owner, @PathVariable String name,
                                 @PathVariable String field) throws DatabaseAccessException {
        return petService.getField(owner, name, field);
    }

    /**
     * Updates the pet's field.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to update
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *                key "value"
     */
    @PutMapping("/{owner}/{name}/simple/{field}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSimpleField(@PathVariable String owner, @PathVariable String name, @PathVariable String field,
                                  @RequestBody Map<String, Object> valueMap) {
        petService.updateField(owner, name, field, valueMap.get("value"));
    }

    /**
     * Deletes the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to delete
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{name}/map/{field}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMapField(@PathVariable String owner, @PathVariable String name,
                               @PathVariable String field) throws DatabaseAccessException {
        petService.deleteMapField(owner, name, field);
    }

    /**
     * Gets the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to retrieve
     * @return The map from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{name}/map/{field}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getMapField(@PathVariable String owner, @PathVariable String name,
                                 @PathVariable String field) throws DatabaseAccessException {
        return petService.getMapField(owner, name, field);
    }

    /**
     * Adds an element to the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the new element to be added
     * @param body Element to be added
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PostMapping("/{owner}/{name}/map/{field}/{key}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addMapFieldElement(@PathVariable String owner, @PathVariable String name,
                                        @PathVariable String field, @PathVariable String key,
                                     @RequestBody Object body) throws DatabaseAccessException {
        PetEntity.checkKeyAndBody(field,key,body);
        petService.addMapFieldElement(owner, name, field, key, body);
    }

    /**
     * Deletes an element from the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element to delete
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{name}/map/{field}/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMapFieldElement(@PathVariable String owner, @PathVariable String name,
                                        @PathVariable String field, @PathVariable String key)
        throws DatabaseAccessException {
        return petService.deleteMapFieldElement(owner, name, field, key);
    }

    /**
     * Updates an element from the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element to update
     * @param body Update of the element
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping("/{owner}/{name}/map/{field}/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMapFieldElement(@PathVariable String owner, @PathVariable String name,
                                        @PathVariable String field, @PathVariable String key,
                                      @RequestBody Object body) throws DatabaseAccessException {
        PetEntity.checkKeyAndBody(field,key,body);
        return petService.updateMapFieldElement(owner, name, field, key, body);
    }

    /**
     * Gets an element from the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element
     * @return Element assigned to the key
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{name}/map/{field}/{key}")
    @ResponseStatus(HttpStatus.OK)
    public Object getMapFieldElement(@PathVariable String owner, @PathVariable String name,
                                     @PathVariable String field, @PathVariable String key)
        throws DatabaseAccessException {
        return petService.getMapFieldElement(owner, name, field, key);
    }

    /**
     * Gets all the elements between the keys from the map for the specified field.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key1 Start key (This one included)
     * @param key2 End Key (This one included)
     * @return The map containing the elements between the keys
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{name}/map/{field}/{key1}/{key2}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getMapFieldElementsBetweenKeys(@PathVariable String owner, @PathVariable String name,
                                     @PathVariable String field, @PathVariable String key1, @PathVariable String key2)
        throws DatabaseAccessException {
        return petService.getMapFieldElementsBetweenKeys(owner, name, field, key1, key2);
    }
}
