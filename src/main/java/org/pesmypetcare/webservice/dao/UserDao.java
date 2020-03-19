package org.pesmypetcare.webservice.dao;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.UserEntity;

public interface UserDao {
    /**
     * Creates a user on the data base.
     * @param userEntity The entity that contains the uid, username and email of the user
     */
    void save(UserEntity userEntity);

    /**
     * Deletes the user with the specified uid from the data base.
     * @param uid The uid of the user to delete
     */
    void deleteById(int uid);

    /**
     * Creates the user authentication profile.
     * @param userEntity The entity that contains the uid, username and email of the user
     * @param password The password for the new account
     * @throws FirebaseAuthException If a user tries to create an account with an existing username or
     *      * email, or with an invalid email
     */
    void saveAuth(UserEntity userEntity, String password) throws FirebaseAuthException;
}
