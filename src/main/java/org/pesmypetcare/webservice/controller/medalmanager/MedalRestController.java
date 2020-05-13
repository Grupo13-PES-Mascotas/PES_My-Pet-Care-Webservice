package org.pesmypetcare.webservice.controller.medalmanager;

import org.pesmypetcare.webservice.entity.MedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.service.MedalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
/**
 * @author Oriol Catalán
 */
@RestController
@RequestMapping("/medal")
public class MedalRestController {
    @Autowired
    private MedalService medalService;

    /**
     * Gets a medal identified by its name and owner.
     * @param name Name of the pet
     * @return The MedalEntity corresponding to the medal data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public MedalEntity getPetData(@PathVariable String name)
        throws DatabaseAccessException, DocumentException {
        return medalService.getMedalData(name);
    }

    /**
     * Gets the data from all the specified medals from the database.
     * @return The List containing all the owner medals data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> getAllPetsData()
        throws DatabaseAccessException, DocumentException {
        return medalService.getAllMedalsData();
    }

    /**
     * Gets the value for the specified field of the medal on the database.
     * @param name Name of the medal
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("{name}/simple/{field}")
    @ResponseStatus(HttpStatus.OK)
    public Object getSimpleField(@PathVariable String name,
                                 @PathVariable String field) throws DatabaseAccessException, DocumentException {
        MedalEntity.checkSimpleField(field);
        return medalService.getSimpleField(name, field);
    }
}
