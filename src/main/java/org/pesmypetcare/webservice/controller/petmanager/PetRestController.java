package org.pesmypetcare.webservice.controller.petmanager;

import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
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

/**
 * @author Marc Simó
 */
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
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PostMapping("/{owner}/{name}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPet(@PathVariable String owner, @PathVariable String name, @RequestBody PetEntity pet)
        throws DatabaseAccessException, DocumentException {
        petService.createPet(owner, name, pet);
    }

    /**
     * Deletes the pet with the specified owner and name from the database.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping("/{owner}/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByOwnerAndName(@PathVariable String owner,
                                     @PathVariable String name) throws DatabaseAccessException, DocumentException {
        petService.deleteByOwnerAndName(owner, name);
    }

    /**
     * Deletes all the pets of the specified owner from database.
     * @param owner Username of the owner of the pets
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping("/{owner}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllPets(@PathVariable String owner) throws DatabaseAccessException, DocumentException {
        petService.deleteAllPets(owner);
    }

    /**
     * Gets a pet identified by its name and owner.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @return The PetEntity of the owner data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{owner}/{name}")
    @ResponseStatus(HttpStatus.OK)
    public PetEntity getPetData(@PathVariable String owner, @PathVariable String name)
        throws DatabaseAccessException, DocumentException {
        return petService.getPetData(owner, name);
    }

    /**
     * Gets the data from all the specified pets from the database.
     * @param owner Username of the owner of the pets
     * @return The List containing all the owner pets data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{owner}")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> getAllPetsData(@PathVariable String owner)
        throws DatabaseAccessException, DocumentException {
        return petService.getAllPetsData(owner);
    }

    /**
     * Gets the value for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{owner}/{name}/simple/{field}")
    @ResponseStatus(HttpStatus.OK)
    public Object getSimpleField(@PathVariable String owner, @PathVariable String name,
                                 @PathVariable String field) throws DatabaseAccessException, DocumentException {
        return petService.getSimpleField(owner, name, field);
    }

    /**
     * Updates the pet's field.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to update
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *                key "value"
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PutMapping("/{owner}/{name}/simple/{field}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSimpleField(@PathVariable String owner, @PathVariable String name, @PathVariable String field,
                                  @RequestBody Map<String, Object> valueMap)
        throws DatabaseAccessException, DocumentException {
        petService.updateSimpleField(owner, name, field, valueMap.get("value"));
    }

    /**
     * Deletes the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to delete
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping("/{owner}/{name}/collection/{field}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFieldCollection(@PathVariable String owner, @PathVariable String name,
                               @PathVariable String field) throws DatabaseAccessException, DocumentException {
        petService.deleteFieldCollection(owner, name, field);
    }

    /**
     * Gets the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to retrieve
     * @return The list from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{owner}/{name}/collection/{field}")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> getFieldCollection(@PathVariable String owner, @PathVariable String name,
                                 @PathVariable String field) throws DatabaseAccessException, DocumentException {
        return petService.getFieldCollection(owner, name, field);
    }

    /**
     * Gets all the elements between the keys from the map for the specified field.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key1 Start key (This one included)
     * @param key2 End Key (This one included)
     * @return The list containing the elements between the keys
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{owner}/{name}/collection/{field}/{key1}/{key2}")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> getFieldCollectionElementsBetweenKeys(@PathVariable String owner,
           @PathVariable String name, @PathVariable String field, @PathVariable String key1, @PathVariable String key2)
        throws DatabaseAccessException, DocumentException {
        return petService.getFieldCollectionElementsBetweenKeys(owner, name, field, key1, key2);
    }

    /**
     * Adds an element to the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the new element to be added
     * @param body Element to be added
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PostMapping("/{owner}/{name}/collection/{field}/{key}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addFieldCollectionElement(@PathVariable String owner, @PathVariable String name,
                                        @PathVariable String field, @PathVariable String key,
                                     @RequestBody Map<String, Object> body)
        throws DatabaseAccessException, DocumentException {
        PetEntity.checkKeyAndBody(field, key, body);
        petService.addFieldCollectionElement(owner, name, field, key, body);
    }

    /**
     * Deletes an element from the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element to delete
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping("/{owner}/{name}/collection/{field}/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFieldCollectionElement(@PathVariable String owner, @PathVariable String name,
                                        @PathVariable String field, @PathVariable String key)
        throws DatabaseAccessException, DocumentException {
        petService.deleteFieldCollectionElement(owner, name, field, key);
    }

    /**
     * Updates an element from the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element to update
     * @param body Update of the element
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PutMapping("/{owner}/{name}/collection/{field}/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
public void updateFieldCollectionElement(@PathVariable String owner, @PathVariable String name,
                                        @PathVariable String field, @PathVariable String key,
                                      @RequestBody Map<String, Object> body)
        throws DatabaseAccessException, DocumentException {
        PetEntity.checkKeyAndBody(field, key, body);
        petService.updateFieldCollectionElement(owner, name, field, key, body);
    }

    /**
     * Gets an element from the map for the specified field of the pet on the database.
     * @param owner Username of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element
     * @return Element assigned to the key
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{owner}/{name}/collection/{field}/{key}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getFieldCollectionElement(@PathVariable String owner, @PathVariable String name,
                                     @PathVariable String field, @PathVariable String key)
        throws DatabaseAccessException, DocumentException {
        return petService.getFieldCollectionElement(owner, name, field, key);
    }
}
