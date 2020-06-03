package org.pesmypetcare.webservice.service.medalmanager;

import org.pesmypetcare.webservice.entity.medalmanager.UserMedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
public interface UserMedalService {

    /**
     * Creates a medal on the data base.
     * @param token The user's personal access token
     * @param name Name of the medal
     * @param medal The medal entity that contains the attributes of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void createUserMedal(String token, String name, UserMedalEntity medal)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets a medal identified by its name and owner.
     * @param token The user's personal access token
     * @param name Name of the medal
     * @return The MedalEntity corresponding to the owner's medal data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    UserMedalEntity getUserMedalData(String token, String name) throws DatabaseAccessException, DocumentException;

    /**
     * Gets the data from all the specified medals from the database.
     * @param token The user's personal access tokens
     * @return The List containing all the owner medals data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    List<Map<String, UserMedalEntity>> getAllUserMedalsData(String token) throws DatabaseAccessException,
        DocumentException;

    /**
     * Updates the medal's field.
     * @param token The user's personal access token
     * @param name Name of the medal
     * @param field Name of the field to update
     * @param value Value the field will have
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void updateField(String token, String name, String field, Object value)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets the value for the specified field of the medal on the database.
     * @param token The user's personal access tokens
     * @param name Name of the medal
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    Object getField(String token, String name, String field) throws DatabaseAccessException, DocumentException;
}
