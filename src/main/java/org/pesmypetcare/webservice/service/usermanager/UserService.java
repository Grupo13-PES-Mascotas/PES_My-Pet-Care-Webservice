package org.pesmypetcare.webservice.service.usermanager;


import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
public interface UserService {
    /**
     * Creates a user on the data base.
     *
     * @param uid The user's UID
     * @param userEntity The entity that contains the username, password and email for the new user
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     * @throws DocumentException If the creation of the user fails
     */
    void createUser(String uid, UserEntity userEntity)
        throws DatabaseAccessException, FirebaseAuthException, DocumentException;

    /**
     * Deletes a user from database.
     *
     * @param token The user's personal access token
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    void deleteFromDatabase(String token) throws DatabaseAccessException, DocumentException;

    /**
     * Deletes the user with the specified uid from the data base.
     *
     * @param token The user's personal access token
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     * @throws DocumentException When the document does not exist
     */
    void deleteById(String token) throws DatabaseAccessException, FirebaseAuthException, DocumentException;


    /**
     * Gets the data of the specified user.
     *
     * @param token The user's personal access token
     * @return The UserEntity with the users data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    UserEntity getUserData(String token) throws DatabaseAccessException;

    /**
     * Updates a user field.
     *
     * @param token The user's personal access token
     * @param field The field to update
     * @param newValue The new field value
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateField(String token, String field, String newValue) throws FirebaseAuthException, DatabaseAccessException;

    /**
     * Checks if a username is already in use.
     *
     * @param username The username to check
     * @return True if the username is already in use
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    boolean existsUsername(String username) throws DatabaseAccessException;

    /**
     * Saves the FCM token of the user.
     *
     * @param token The user's personal access token
     * @param fcmToken The FCM token to save
     * @throws DatabaseAccessException If an error occurs when saving the token
     * @throws DocumentException When the user does not exist
     * @throws BadCredentialsException When the access token is not valid
     */
    void saveMessagingToken(String token, String fcmToken)
        throws DatabaseAccessException, DocumentException, BadCredentialsException;

    /**
     * Lists all the user subscriptions to groups.
     *
     * @param token The user's personal access token
     * @return The list of groups to which the user is subscribed
     * @throws DatabaseAccessException If an error occurs when saving the token
     */
    List<String> getUserSubscriptions(String token) throws DatabaseAccessException;
}
