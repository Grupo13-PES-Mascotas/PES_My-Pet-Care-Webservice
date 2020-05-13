package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.entity.MedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
public interface MedalService {

    /**
     * Gets a medal identified by its name.
     * @param name Name of the medal
     * @return The MedalEntity corresponding to the owner's medal data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    MedalEntity getMedalData(String name) throws DatabaseAccessException, DocumentException;

    /**
     * Gets the data from all the specified medals from the database.
     * @return The List containing all the owner medals data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    List<Map<String, Object>> getAllMedalsData() throws DatabaseAccessException, DocumentException;

    /**
     * Gets the value for the specified field of the medal on the database.
     * @param name Name of the medal
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    Object getSimpleField(String name, String field) throws DatabaseAccessException, DocumentException;
}
