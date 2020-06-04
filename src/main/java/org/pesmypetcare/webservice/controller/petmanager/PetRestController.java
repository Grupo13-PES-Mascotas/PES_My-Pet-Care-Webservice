package org.pesmypetcare.webservice.controller.petmanager;

import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.service.petmanager.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
     *
     * @param token Access token of the owner of the pet
     * @param name Name of the pet
     * @param pet The pet entity that contains the attributes of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PostMapping("/{name}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPet(@RequestHeader String token, @PathVariable String name, @RequestBody PetEntity pet)
        throws DatabaseAccessException, DocumentException {
        petService.createPet(token, name, pet);
    }

    /**
     * Deletes the pet with the specified owner and name from the database.
     *
     * @param token Access token of the owner of the pet
     * @param name Name of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByOwnerAndName(@RequestHeader String token, @PathVariable String name)
        throws DatabaseAccessException, DocumentException {
        petService.deleteByOwnerAndName(token, name);
    }

    /**
     * Deletes all the pets of the specified owner from database.
     *
     * @param token Access token of the owner of the pets
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllPets(@RequestHeader String token) throws DatabaseAccessException, DocumentException {
        petService.deleteAllPets(token);
    }

    /**
     * Gets a pet identified by its name and owner.
     *
     * @param token Access token of the owner of the pet
     * @param name Name of the pet
     * @return The PetEntity corresponding to the owner's pet data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public PetEntity getPetData(@RequestHeader String token, @PathVariable String name)
        throws DatabaseAccessException, DocumentException {
        return petService.getPetData(token, name);
    }

    /**
     * Gets the data from all the specified pets from the database.
     *
     * @param token Access token of the owner of the pets
     * @return The List containing all the owner pets data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> getAllPetsData(@RequestHeader String token)
        throws DatabaseAccessException, DocumentException {
        return petService.getAllPetsData(token);
    }

    /**
     * Gets the value for the specified field of the pet on the database.
     *
     * @param token Access token of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{name}/simple/{field}")
    @ResponseStatus(HttpStatus.OK)
    public Object getSimpleField(@RequestHeader String token, @PathVariable String name, @PathVariable String field)
        throws DatabaseAccessException, DocumentException {
        PetEntity.checkSimpleField(field);
        return petService.getSimpleField(token, name, field);
    }

    /**
     * Updates the pet's field.
     *
     * @param token Access token of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to update
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     * key "value"
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PutMapping("/{name}/simple/{field}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSimpleField(@RequestHeader String token, @PathVariable String name, @PathVariable String field,
                                  @RequestBody Map<String, Object> valueMap)
        throws DatabaseAccessException, DocumentException {
        PetEntity.checkSimpleFieldAndValues(field, valueMap.get("value"));
        petService.updateSimpleField(token, name, field, valueMap.get("value"));
    }

    /**
     * Deletes the map for the specified field of the pet on the database.
     *
     * @param token Access token of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to delete
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping("/{name}/collection/{field}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFieldCollection(@RequestHeader String token, @PathVariable String name,
                                      @PathVariable String field) throws DatabaseAccessException, DocumentException {
        PetEntity.checkCollectionField(field);
        petService.deleteFieldCollection(token, name, field);
    }

    /**
     * Deletes all the field collection elements with a key previous or smaller to the specified one.
     *
     * @param token Access token of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field where the action will be done
     * @param key Specified key (This one not included)
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping("/{name}/fullcollection/{field}/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFieldCollectionElementsPreviousToKey(@RequestHeader String token, @PathVariable String name,
                                                    @PathVariable String field, @PathVariable String key)
        throws DatabaseAccessException, DocumentException {
        PetEntity.checkCollectionKey(field, key);
        petService.deleteFieldCollectionElementsPreviousToKey(token, name, field, key);
    }

    /**
     * Gets the map for the specified field of the pet on the database.
     *
     * @param token Access token of the owner of the pet
     * @param name Name of the pet
     * @param field Name of the field to retrieve
     * @return The list from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{name}/collection/{field}")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> getFieldCollection(@RequestHeader String token, @PathVariable String name,
                                                        @PathVariable String field)
        throws DatabaseAccessException, DocumentException {
        PetEntity.checkCollectionField(field);
        return petService.getFieldCollection(token, name, field);
    }

    /**
     * Gets all the elements between the keys from the map for the specified field.
     *
     * @param token Access token of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key1 Start key (This one included)
     * @param key2 End Key (This one included)
     * @return The list containing the elements between the keys
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{name}/collection/{field}/{key1}/{key2}")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> getFieldCollectionElementsBetweenKeys(@RequestHeader String token,
                                                                           @PathVariable String name,
                                                                           @PathVariable String field,
                                                                           @PathVariable String key1,
                                                                           @PathVariable String key2)
        throws DatabaseAccessException, DocumentException {
        PetEntity.checkCollectionKey(field, key1);
        PetEntity.checkCollectionKey(field, key2);
        return petService.getFieldCollectionElementsBetweenKeys(token, name, field, key1, key2);
    }

    /**
     * Adds an element to the map for the specified field of the pet on the database.
     *
     * @param token Access token of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the new element to be added
     * @param body Element to be added
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PostMapping("/{name}/collection/{field}/{key}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addFieldCollectionElement(@RequestHeader String token, @PathVariable String name,
                                          @PathVariable String field, @PathVariable String key,
                                          @RequestBody Map<String, Object> body)
        throws DatabaseAccessException, DocumentException {
        PetEntity.checkCollectionKeyAndBody(field, key, body);
        petService.addFieldCollectionElement(token, name, field, key, body);
    }

    /**
     * Deletes an element from the map for the specified field of the pet on the database.
     *
     * @param token Access token of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element to delete
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping("/{name}/collection/{field}/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFieldCollectionElement(@RequestHeader String token, @PathVariable String name,
                                             @PathVariable String field, @PathVariable String key)
        throws DatabaseAccessException, DocumentException {
        PetEntity.checkCollectionField(field);
        petService.deleteFieldCollectionElement(token, name, field, key);
    }

    /**
     * Updates an element from the map for the specified field of the pet on the database.
     *
     * @param token Access token of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element to update
     * @param body Update of the element
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PutMapping("/{name}/collection/{field}/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFieldCollectionElement(@RequestHeader String token, @PathVariable String name,
                                             @PathVariable String field, @PathVariable String key,
                                             @RequestBody Map<String, Object> body)
        throws DatabaseAccessException, DocumentException {
        PetEntity.checkCollectionKeyAndBody(field, key, body);
        petService.updateFieldCollectionElement(token, name, field, key, body);
    }

    /**
     * Gets an element from the map for the specified field of the pet on the database.
     *
     * @param token Access token of the owner of the pets
     * @param name Name of the pet
     * @param field Name of the field
     * @param key Key of the element
     * @return Element assigned to the key
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{name}/collection/{field}/{key}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getFieldCollectionElement(@RequestHeader String token, @PathVariable String name,
                                                         @PathVariable String field, @PathVariable String key)
        throws DatabaseAccessException, DocumentException {
        PetEntity.checkCollectionField(field);
        return petService.getFieldCollectionElement(token, name, field, key);
    }
}
