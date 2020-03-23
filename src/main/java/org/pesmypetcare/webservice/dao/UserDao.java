package org.pesmypetcare.webservice.dao;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.concurrent.ExecutionException;

public interface UserDao {
    /**
     * Creates the user authentication profile.
     * @param userEntity The entity that contains the uid, username and email of the user
     * @param password The password for the new account
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    void createUserAuth(UserEntity userEntity, String password) throws FirebaseAuthException;

    /**
     * Creates a user on the data base.
     * @param userEntity The entity that contains the uid, username and email of the user
     */
    void createUser(UserEntity userEntity);

    /**
     * Deletes the user with the specified uid from the data base.
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
