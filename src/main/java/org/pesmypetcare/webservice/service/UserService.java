package org.pesmypetcare.webservice.service;


import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

/**
 * @author Santiago Del Rey
 */
public interface UserService {
    /**
     * Creates the user authentication profile.
     * @param user The entity that contains the uid, username and email of the user
     * @param password The password for the new account
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    void createUserAuth(UserEntity user, String password) throws FirebaseAuthException;

    /**
     * Creates a user on the data base.
     * @param uid The unique identifier of the user
     * @param userEntity The entity that contains the username, password and email for the new user
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void createUser(String uid, UserEntity userEntity) throws DatabaseAccessException, FirebaseAuthException;

    /**
     * Deletes a user from database.
     * @param uid The unique identifier of the user
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteFromDatabase(String uid) throws DatabaseAccessException;

    /**
     * Deletes the user with the specified uid from the data base.
     * @param uid The unique identifier of the user
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
     * @param uid The unique identifier of the user
     * @param newValue The new field value
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateField(String uid, String field, String newValue) throws FirebaseAuthException, DatabaseAccessException;

    /**
     * Checks if a username is already in use.
     * @param username The username to check
     * @return True if the username is already in use
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    boolean existsUsername(String username) throws DatabaseAccessException;
}
