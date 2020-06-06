package org.pesmypetcare.webservice.dao.medalmanager;

import org.pesmypetcare.webservice.entity.medalmanager.MedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
public interface MedalDao {

    /**
     * Creates a medal on the data base.
     * @param name Name of the medal
     * @param medal The medal entity that contains the attributes of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void createMedal(String name, MedalEntity medal) throws DatabaseAccessException, DocumentException;

    /**
     * Gets a medal identified by its name.
     * @param name Name of the medal
     * @return The MedalEntity corresponding to the medal data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    MedalEntity getMedalData(String name) throws DatabaseAccessException, DocumentException;

    /**
     * Gets the data from all the specified medals from the database.
     * @return The List containing all the medals data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    List<Map<String, MedalEntity>> getAllMedalsData() throws DatabaseAccessException, DocumentException;

    /**
     * Gets the value for the specified field of the medal on the database.
     * @param name Name of the medal
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    Object getField(String name, String field) throws DatabaseAccessException, DocumentException;
}
