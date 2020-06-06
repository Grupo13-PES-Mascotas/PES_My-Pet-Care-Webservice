package org.pesmypetcare.webservice.dao.medalmanager;

import org.pesmypetcare.webservice.entity.medalmanager.UserMedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.List;

/**
 * @author Oriol Catalán
 */
public interface UserMedalDao {

    /**
     * Creates a medal on the data base.
     * @param owner Username of the owner of the medal
     * @param name Name of the medal
     * @param medal The medal entity that contains the attributes of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void createUserMedal(String owner, String name, UserMedalEntity medal)
        throws DatabaseAccessException, DocumentException;


    /**
     * Gets a medal identified by its name and owner.
     * @param owner Username of the owner of the medal
     * @param name Name of the medal
     * @return The MedalEntity corresponding to the owner's medal data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    UserMedalEntity getUserMedalData(String owner, String name) throws DatabaseAccessException, DocumentException;

    /**
     * Gets the data from all the specified medals from the database.
     * @param owner Username of the owner of the medals
     * @return The List containing all the owner medals data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    List<UserMedalEntity> getAllUserMedalsData(String owner) throws DatabaseAccessException,
        DocumentException;

    /**
     * Updates the medal's field.
     * @param owner Username of the owner of the medal
     * @param name Name of the medal
     * @param field Name of the field to update
     * @param value Value the field will have
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void updateField(String owner, String name, String field, Object value)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets the value for the specified field of the medal on the database.
     * @param owner Username of the owner of the medals
     * @param name Name of the medal
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    Object getField(String owner, String name, String field) throws DatabaseAccessException, DocumentException;

    /**
     * Create all user medals when user is created.
     * @param username Username of the user.
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    void createAllUserMedals(String username) throws DatabaseAccessException, DocumentException;

}
