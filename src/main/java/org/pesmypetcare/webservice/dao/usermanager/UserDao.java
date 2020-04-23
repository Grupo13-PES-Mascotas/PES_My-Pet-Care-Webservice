package org.pesmypetcare.webservice.dao.usermanager;

import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
public interface UserDao {
    /**
     * Creates a user on the database.
     * @param uid The user's unique identifier
     * @param userEntity The entity that contains the username, password and email for the new user
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    void createUser(String uid, UserEntity userEntity) throws DatabaseAccessException, FirebaseAuthException;

    /**
     * Deletes a user from database.
     * @param uid The user's unique identifier
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteFromDatabase(String uid) throws DatabaseAccessException;

    /**
     * Deletes the user with the specified uid from the database.
     * @param uid The uid of the user to delete
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    void deleteById(String uid) throws DatabaseAccessException, FirebaseAuthException;

    /**
     * Gets the data of the specified user.
     * @param uid The unique identifier of the user
     * @return The UserEntity with the users data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    UserEntity getUserData(String uid) throws DatabaseAccessException;

    /**
     * Updates a user field.
     * @param username The user's username
     * @param newValue The new field value
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateField(String username, String field, String newValue)
        throws FirebaseAuthException, DatabaseAccessException;

    /**
     * Checks if a username is already in use.
     * @param username The username to check
     * @return True if the username is already in use
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    boolean existsUsername(String username) throws DatabaseAccessException;

    /**
     * Gets a user field.
     *
     * @param uid The user uid
     * @param field The field to retrieve
     * @return The field requested
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    String getField(String uid, String field) throws DatabaseAccessException;

    /**
     * Gets the user's uid.
     *
     * @param username The user's username
     * @return The user's uid
     * @throws DatabaseAccessException If the user doesn't exist
     */
    String getUid(String username) throws DatabaseAccessException;

    /**
     * Creates an entry of the group in the subscription collection.
     * @param username The user's username
     * @param groupName The group's name
     * @param batch The batch of writes to which it belongs
     */
    void addGroupSubscription(String username, String groupName, WriteBatch batch) throws DatabaseAccessException;

    /**
     * Deletes an entry of the group in the subscription collection.
     * @param userUid The user's uid
     * @param groupName The group's name
     * @param batch The batch of writes to which it belongs
     */
    void deleteGroupSubscription(String userUid, String groupName, WriteBatch batch);

    /**
     * Gets all the user's group subscriptions.
     * @param username The user's username
     * @return A list with the group subscriptions
     * @throws DatabaseAccessException If an error occurs when retrieving the data
     */
    List<String> getUserSubscriptions(String username) throws DatabaseAccessException;

    void addForumSubscription(String username, String parentGroup, String forumName, WriteBatch batch) throws DatabaseAccessException;
}
