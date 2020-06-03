package org.pesmypetcare.webservice.dao.medalmanager;

import com.google.cloud.firestore.WriteBatch;
import org.pesmypetcare.webservice.entity.medalmanager.UserMedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
public interface UserMedalDao {

    /**
     * Creates a medal on the data base.
     *
     * @param userId The user's ID
     * @param name Name of the medal
     * @param medal The medal entity that contains the attributes of the pet
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void createUserMedal(String userId, String name, UserMedalEntity medal)
        throws DatabaseAccessException, DocumentException;

    /**
     * Creates a medal on the data base.
     *
     * @param userId The user's ID
     * @param name Name of the medal
     * @param medal The medal entity that contains the attributes of the pet
     * @param batch The batch where to write
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void createUserMedal(String userId, String name, UserMedalEntity medal, WriteBatch batch)
        throws DatabaseAccessException, DocumentException;

    /**
     * Create all user medals when user is created.
     *
     * @param username Username of the user
     * @param batch The batch where to write
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the document does not exist
     */
    void createAllUserMedals(String username, WriteBatch batch) throws DatabaseAccessException, DocumentException;

    /**
     * Gets a medal identified by its name and owner.
     *
     * @param userId The user's ID
     * @param name Name of the medal
     * @return The MedalEntity corresponding to the owner's medal data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    UserMedalEntity getUserMedalData(String userId, String name) throws DatabaseAccessException, DocumentException;

    /**
     * Gets the data from all the specified medals from the database.
     *
     * @param userId The user's IDs
     * @return The List containing all the owner medals data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    List<Map<String, UserMedalEntity>> getAllUserMedalsData(String userId)
        throws DatabaseAccessException, DocumentException;

    /**
     * Updates the medal's field.
     *
     * @param userId The user's ID
     * @param name Name of the medal
     * @param field Name of the field to update
     * @param value Value the field will have
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void updateField(String userId, String name, String field, Object value)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets the value for the specified field of the medal on the database.
     *
     * @param userId The user's IDs
     * @param name Name of the medal
     * @param field Name of the field to retrieve the value from
     * @return The value from the field on the database
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    Object getField(String userId, String name, String field) throws DatabaseAccessException, DocumentException;


}
