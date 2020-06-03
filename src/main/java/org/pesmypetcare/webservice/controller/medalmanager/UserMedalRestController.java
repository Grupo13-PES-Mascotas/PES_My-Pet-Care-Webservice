package org.pesmypetcare.webservice.controller.medalmanager;

import org.pesmypetcare.webservice.entity.medalmanager.UserMedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.service.medalmanager.UserMedalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
     * Gets a medal identified by its name and owner.
     * @param token The user's personal access token
     * @param name Name of the medal
     * @return The MedalEntity corresponding to the owner's medal data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public UserMedalEntity getMedalData(@RequestHeader String token, @PathVariable String name)
        throws DatabaseAccessException, DocumentException {
        return userMedalService.getUserMedalData(token, name);
    }

    /**
     * Gets the data from all the specified medals from the database.
     * @param token The user's personal access tokens
     * @return The List containing all the owner medals data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, UserMedalEntity>> getAllMedalsData(@RequestHeader String token)
        throws DatabaseAccessException, DocumentException {
        return userMedalService.getAllUserMedalsData(token);
    }

    /**
     * Updates the medal's field.
     * @param token The user's personal access token
     * @param name Name of the medal
     * @param field Name of the field to update
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *                key "value"
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PutMapping("/{name}/{field}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateField(@RequestHeader String token, @PathVariable String name, @PathVariable String field,
                                  @RequestBody Map<String, Object> valueMap)
        throws DatabaseAccessException, DocumentException {
        UserMedalEntity.checkFieldAndValues(field, valueMap.get(VALUE));
        userMedalService.updateField(token, name, field, valueMap.get(VALUE));
    }

    /**
     * Gets the value for the specified field of the medal on the database.
     * @param token The user's personal access tokens
     * @param name Name of the medal
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{name}/{field}")
    @ResponseStatus(HttpStatus.OK)
    public Object getField(@RequestHeader String token, @PathVariable String name,
                                 @PathVariable String field) throws DatabaseAccessException, DocumentException {
        UserMedalEntity.checkField(field);
        return userMedalService.getField(token, name, field);
    }
}
