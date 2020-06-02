package org.pesmypetcare.webservice.dao.usermanager;

import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
public interface UserDao {
    /**
     * Creates a user on the database.
     *
     * @param token The user's Firebase token
     * @param userEntity The entity that contains the username, password and email for the new user
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    void createUser(UserToken token, UserEntity userEntity) throws DatabaseAccessException, FirebaseAuthException;

    /**
     * Deletes a user from database.
     *
     * @param token The user's Firebase token
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void deleteFromDatabase(UserToken token) throws DatabaseAccessException, DocumentException;

    /**
     * Deletes the user with the specified uid from the database.
     *
     * @param token The user's Firebase token
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     * @throws DocumentException When the document does not exist
     */
    void deleteById(UserToken token) throws DatabaseAccessException, FirebaseAuthException, DocumentException;

    /**
     * Gets the data of the specified user.
     *
     * @param token The user's Firebase token
     * @return The UserEntity with the users data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    UserEntity getUserData(UserToken token) throws DatabaseAccessException;

    /**
     * Updates a user field.
     *
     * @param token The user's Firebase token
     * @param field The field to update
     * @param newValue The new field value
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateField(UserToken token, String field, String newValue)
        throws FirebaseAuthException, DatabaseAccessException;

    /**
     * Checks if a username is already in use.
     *
     * @param username The username to check
     * @return True if the username is already in use
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    boolean existsUsername(String username) throws DatabaseAccessException;

    /**
     * Gets a user field.
     *
     * @param token The user's Firebase token
     * @param field The field to retrieve
     * @return The field requested
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    String getField(UserToken token, String field) throws DatabaseAccessException;

    /**
     * Returns the user FCM token.
     * @param uid The user's UID
     * @return The user's FCM token
     * @throws DatabaseAccessException If an error occurs when retrieving the data
     * @throws DocumentException If the user does not exist
     */
    String getFcmToken(String uid) throws DatabaseAccessException, DocumentException;

    /**
     * Gets the user's uid.
     *
     * @param username The user's username
     * @return The user's uid
     * @throws DatabaseAccessException If an error occurs when retrieving the data
     */
    String getUid(String username) throws DatabaseAccessException;

    /**
     * Creates an entry of the group in the subscription collection.
     *
     * @param token The user's Firebase token
     * @param groupName The group's name
     * @param batch The batch of writes to which it belongs
     */
    void addGroupSubscription(UserToken token, String groupName, WriteBatch batch);

    /**
     * Deletes an entry of the group in the subscription collection.
     *
     * @param token The user's Firebase token
     * @param groupName The group's name
     * @param batch The batch of writes to which it belongs
     */
    void deleteGroupSubscription(UserToken token, String groupName, WriteBatch batch);

    /**
     * Gets all the user's group subscriptions.
     *
     * @param token The user's Firebase token
     * @return A list with the group subscriptions
     * @throws DatabaseAccessException If an error occurs when retrieving the data
     */
    List<String> getUserSubscriptions(UserToken token) throws DatabaseAccessException;

    /**
     * Saves the FCM token of the user.
     *
     * @param token The user's Firebase token
     * @param fcmToken The user's FCM token
     * @throws DatabaseAccessException If an error occurs when saving the token
     * @throws DocumentException When the user does not exist
     */
    void saveMessagingToken(UserToken token, String fcmToken) throws DatabaseAccessException, DocumentException;
}
