package org.pesmypetcare.webservice.controller.medalmanager;

import org.pesmypetcare.webservice.entity.UserMedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.service.UserMedalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
@RestController
@RequestMapping("/usermedal")
public class UserMedalRestController {
    private static final String VALUE = "value";

    @Autowired
    private UserMedalService userMedalService;


    /**
     * Gets a pet identified by its name and owner.
     * @param owner Username of the owner of the pet
     * @param name Name of the pet
     * @return The PetEntity corresponding to the owner's pet data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{owner}/{name}")
    @ResponseStatus(HttpStatus.OK)
    public UserMedalEntity getPetData(@PathVariable String owner, @PathVariable String name)
        throws DatabaseAccessException, DocumentException {
        return userMedalService.getUserMedalData(owner, name);
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
        return userMedalService.getAllUserMedalsData(owner);
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
        UserMedalEntity.checkSimpleFieldAndValues(field, valueMap.get(VALUE));
        userMedalService.updateSimpleField(owner, name, field, valueMap.get(VALUE));
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
        UserMedalEntity.checkSimpleField(field);
        return userMedalService.getSimpleField(owner, name, field);
    }
}
