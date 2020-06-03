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
     * @param owner Username of the owner of the medal
     * @param name Name of the medal
     * @return The MedalEntity corresponding to the owner's medal data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{owner}/{name}")
    @ResponseStatus(HttpStatus.OK)
    public UserMedalEntity getMedalData(@PathVariable String owner, @PathVariable String name)
        throws DatabaseAccessException, DocumentException {
        return userMedalService.getUserMedalData(owner, name);
    }

    /**
     * Gets the data from all the specified medals from the database.
     * @param owner Username of the owner of the medals
     * @return The List containing all the owner medals data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{owner}")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, UserMedalEntity>> getAllMedalsData(@PathVariable String owner)
        throws DatabaseAccessException, DocumentException {
        return userMedalService.getAllUserMedalsData(owner);
    }

    /**
     * Updates the medal's field.
     * @param owner Username of the owner of the medal
     * @param name Name of the medal
     * @param field Name of the field to update
     * @param valueMap Entity that contains the value that the field will have. The new field value needs to have the
     *                key "value"
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PutMapping("/{owner}/{name}/{field}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateField(@PathVariable String owner, @PathVariable String name, @PathVariable String field,
                                  @RequestBody Map<String, Object> valueMap)
        throws DatabaseAccessException, DocumentException {
        UserMedalEntity.checkFieldAndValues(field, valueMap.get(VALUE));
        userMedalService.updateField(owner, name, field, valueMap.get(VALUE));
    }

    /**
     * Gets the value for the specified field of the medal on the database.
     * @param owner Username of the owner of the medals
     * @param name Name of the medal
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{owner}/{name}/{field}")
    @ResponseStatus(HttpStatus.OK)
    public Object getField(@PathVariable String owner, @PathVariable String name,
                                 @PathVariable String field) throws DatabaseAccessException, DocumentException {
        UserMedalEntity.checkField(field);
        return userMedalService.getField(owner, name, field);
    }
}
