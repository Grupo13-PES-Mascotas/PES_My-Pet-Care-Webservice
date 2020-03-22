package org.pesmypetcare.webservice.service;


import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.concurrent.ExecutionException;

public interface UserService {
    /**
     * Creates the user authentication profile.
     * @param user The entity that contains the uid, username and email of the user
     * @param password The password for the new account
     * @throws FirebaseAuthException If a user tries to create an account with an existing username or
     *      * email, or with an invalid email
     */
    void createUserAuth(UserEntity user, String password) throws FirebaseAuthException;

    /**
     * Creates a user on the data base.
     * @param userEntity The entity that contains the uid, username and email of the user
     */
    void createUser(UserEntity userEntity);

    /**
     * Deletes the user with the specified uid from the data base.
     * @param id The uid of the user to delete
     */
    void deleteById(String id) throws DatabaseAccessException, FirebaseAuthException;


    /**
     * Gets the data of the specified user.
     * @param uid The unique identifier of the user
     * @return The UserEntity with the users data
     */
    UserEntity getUserData(String uid) throws ExecutionException, InterruptedException, DatabaseAccessException;

    /**
     * Updates the user's email.
     * @param uid The unique identifier of the user
     * @param newEmail The new email for the account
     */
    void updateEmail(String uid, String newEmail) throws FirebaseAuthException;

    /**
     * Updates the user's username.
     * @param uid The unique identifier of the user
     * @param newPassword The new password for the account
     */
    void updatePassword(String uid, String newPassword) throws FirebaseAuthException;
}
