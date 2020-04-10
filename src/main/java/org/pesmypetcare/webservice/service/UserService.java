package org.pesmypetcare.webservice.service;


import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

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
     * @param userEntity The entity that contains the username, password and email for the new user
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void createUser(UserEntity userEntity) throws DatabaseAccessException;

    /**
     * Deletes the user with the specified uid from the data base.
     * @param id The uid of the user to delete
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    void deleteById(String id) throws DatabaseAccessException, FirebaseAuthException;


    /**
     * Gets the data of the specified user.
     * @param uid The unique identifier of the user
     * @return The UserEntity with the users data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    UserEntity getUserData(String uid) throws DatabaseAccessException;

    /**
     * Updates the user's email.
     * @param uid The unique identifier of the user
     * @param newEmail The new email for the account
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    void updateEmail(String uid, String newEmail) throws FirebaseAuthException;

    /**
     * Updates the user's username.
     * @param uid The unique identifier of the user
     * @param newPassword The new password for the account
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    void updatePassword(String uid, String newPassword) throws FirebaseAuthException;
}
