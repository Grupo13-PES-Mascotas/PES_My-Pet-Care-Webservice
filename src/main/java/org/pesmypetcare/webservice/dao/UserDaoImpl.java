package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Repository
public class UserDaoImpl implements UserDao {
    private static final String USED_USERNAME_MESSAGE = "The username is already in use";
    private static final String USER_DOES_NOT_EXIST_MESSAGE = "The user does not exist";
    private static final String USERNAME_FIELD = "username";
    private static final String USER_KEY = "user";
    private final String EMAIL_FIELD = "email";
    private final String PASSWORD_FIELD = "password";
    private FirebaseAuth myAuth;
    private CollectionReference users;
    private CollectionReference usedUsernames;
    private PetDao petDao;

    public UserDaoImpl() {
        FirebaseFactory firebaseFactory = FirebaseFactory.getInstance();
        myAuth = firebaseFactory.getFirebaseAuth();
        Firestore db = firebaseFactory.getFirestore();
        users = db.collection("users");
        usedUsernames = db.collection("used_usernames");
        petDao = new PetDaoImpl();
    }

    @Override
    public void createUser(String uid, UserEntity userEntity) throws DatabaseAccessException, FirebaseAuthException {
        String username = userEntity.getUsername();
        if (!existsUsername(username)) {
            saveUsername(uid, username);
            users.document(uid).set(userEntity);
            updateDisplayName(uid, username);
        } else {
            throw new DatabaseAccessException("invalid-username", USED_USERNAME_MESSAGE);
        }
    }

    @Override
    public void createUserAuth(UserEntity user, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setDisplayName(user.getUsername())
            .setEmail(user.getEmail()).setEmailVerified(false).setPassword(password).setUid(user.getUsername());
        myAuth.createUser(request);
    }

    @Override
    public void deleteFromDatabase(String uid) throws DatabaseAccessException {
        petDao.deleteAllPets(uid);
        deleteUserStorage(uid);
        ApiFuture<DocumentSnapshot> future = users.document(uid).get();
        DocumentSnapshot userDoc = getDocumentSnapshot(future);
        String username = (String) userDoc.get(USERNAME_FIELD);
        usedUsernames.document(Objects.requireNonNull(username)).delete();
        users.document(uid).delete();
    }

    @Override
    public void deleteById(String uid) throws FirebaseAuthException, DatabaseAccessException {
        deleteFromDatabase(uid);
        myAuth.deleteUser(uid);
    }

    @Override
    public UserEntity getUserData(String uid) throws DatabaseAccessException {
        DocumentReference docRef = users.document(uid);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot userDoc = getDocumentSnapshot(future);
        if (!userDoc.exists()) {
            throw new DatabaseAccessException("invalid-user", USER_DOES_NOT_EXIST_MESSAGE);
        }
        return userDoc.toObject(UserEntity.class);
    }

    @Override
    public void updateField(String username, String field, String newValue)
        throws FirebaseAuthException, DatabaseAccessException {
        String uid = getUid(username);
        switch (field) {
            case USERNAME_FIELD:
                updateUsername(uid, newValue);
                break;
            case EMAIL_FIELD:
                updateEmail(uid, newValue);
                break;
            case PASSWORD_FIELD:
                updatePassword(uid, newValue);
                break;
            default:
                throw new DatabaseAccessException("invalid-field", "The field does not exist");
        }
    }

    /**
     * Gets the user's uid.
     * @param username The user's username
     * @return The user's uid
     * @throws DatabaseAccessException If the user doesn't exist
     */
    private String getUid(String username) throws DatabaseAccessException {
        ApiFuture<DocumentSnapshot> future = usedUsernames.document(username).get();
        DocumentSnapshot usernameDoc = getDocumentSnapshot(future);
        if (!usernameDoc.exists()) {
            throw new DatabaseAccessException("invalid-user", USER_DOES_NOT_EXIST_MESSAGE);
        }
        return (String) usernameDoc.get(USER_KEY);
    }

    @Override
    public boolean existsUsername(String username) throws DatabaseAccessException {
        ApiFuture<DocumentSnapshot> future = usedUsernames.document(username).get();
        DocumentSnapshot usernameDoc = getDocumentSnapshot(future);
        return usernameDoc.exists();
    }

    /**
     * Gets the document snapshot for the api future given.
     * @param future The api future from which to get the document
     * @return The document snapshot for the api future given
     * @throws DatabaseAccessException If the deletion fails or if the user doesn't exist
     */
    private DocumentSnapshot getDocumentSnapshot(ApiFuture<DocumentSnapshot> future) throws DatabaseAccessException {
        DocumentSnapshot userDoc;
        try {
            userDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("retrieval-failed", e.getMessage());
        }
        return userDoc;
    }

    /**
     * Deletes all the user files on the storage.
     * @param uid The user unique identifier
     */
    private void deleteUserStorage(String uid) {
        StorageDao storageDao = ((PetDaoImpl) petDao).getStorageDao();
        storageDao.deleteImageByName(uid + "/profile-image.png");
    }

    /**
     * Updates the user's username.
     * @param uid The unique identifier of the user
     * @param newUsername The new username for the account
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    private void updateUsername(String uid, String newUsername) throws DatabaseAccessException, FirebaseAuthException {
        ApiFuture<DocumentSnapshot> future = usedUsernames.document(newUsername).get();
        DocumentSnapshot usernameDoc = getDocumentSnapshot(future);
        if (!usernameDoc.exists()) {
            saveUsername(uid, newUsername);
            deleteOldUsername(uid);
            updateDisplayName(uid, newUsername);
            users.document(uid).update(USERNAME_FIELD, newUsername);
        } else {
            throw new DatabaseAccessException("invalid-username", USED_USERNAME_MESSAGE);
        }
    }

    /**
     * Updates the user's email.
     * @param uid The unique identifier of the user
     * @param newEmail The new email for the account
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    private void updateEmail(String uid, String newEmail) throws FirebaseAuthException {
        UserRecord.UpdateRequest updateRequest = getUserRecord(uid);
        updateRequest.setEmail(newEmail);
        myAuth.updateUserAsync(updateRequest);
        users.document(uid).update(EMAIL_FIELD, newEmail);
    }

    /**
     * Updates the user's username.
     * @param uid The unique identifier of the user
     * @param newPassword The new password for the account
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    private void updatePassword(String uid, String newPassword) throws FirebaseAuthException {
        //TODO: Apply password encryption
        UserRecord.UpdateRequest updateRequest = getUserRecord(uid);
        updateRequest.setPassword(newPassword);
        myAuth.updateUserAsync(updateRequest);
        users.document(uid).update(PASSWORD_FIELD, newPassword);
    }

    /**
     * Saves the username inside the used usernames collection.
     * @param username The username to save
     */
    private void saveUsername(String uid, String username) {
        Map<String, String> docData = new HashMap<>();
        docData.put(USER_KEY, uid);
        usedUsernames.document(username).set(docData);
    }

    /**
     * Deletes the old username from the database.
     * @param uid The unique identifier of the user
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    private void deleteOldUsername(String uid) throws FirebaseAuthException {
        String oldUsername = myAuth.getUser(uid).getDisplayName();
        usedUsernames.document(Objects.requireNonNull(oldUsername)).delete();
    }

    /**
     * Updates the display name.
     * @param uid The unique identifier of the user
     * @param newUsername The new username for the account
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    private void updateDisplayName(String uid, String newUsername) throws FirebaseAuthException {
        UserRecord.UpdateRequest updateRequest = getUserRecord(uid);
        updateRequest.setDisplayName(newUsername);
        myAuth.updateUserAsync(updateRequest);
    }

    /**
     * Gets the update request for the user.
     * @param uid The unique identifier of the user
     * @return An update request for the user data
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    private UserRecord.UpdateRequest getUserRecord(String uid) throws FirebaseAuthException {
        return myAuth.getUser(uid).updateRequest();
    }
}
