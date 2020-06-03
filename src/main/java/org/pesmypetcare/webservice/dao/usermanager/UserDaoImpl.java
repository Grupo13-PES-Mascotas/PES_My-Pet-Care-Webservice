package org.pesmypetcare.webservice.dao.usermanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.dao.petmanager.PetDao;
import org.pesmypetcare.webservice.dao.petmanager.PetDaoImpl;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.FirebaseFactory;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * @author Santiago Del Rey
 */
@Repository
public class UserDaoImpl implements UserDao {
    public static final String USERNAME_FIELD = "username";
    public static final String EMAIL_FIELD = "email";
    public static final String PASSWORD_FIELD = "password";
    private static final String USED_USERNAME_MESSAGE = "The username is already in use";
    private static final String USER_DOES_NOT_EXIST_MESSAGE = "The user does not exist";
    private static final String USER_KEY = "user";
    private static final String INVALID_USER = "invalid-user";
    private static final String INVALID_USERNAME = "invalid-username";
    private static final String UPDATE_FAILED_CODE = "update-failed";
    private static final String FIELD_LIKED_BY = "likedBy";
    private static final String FCM = "FCM";
    private static final String WRITE_FAILED_CODE = "write-failed";
    private FirebaseAuth myAuth;
    private CollectionReference users;
    private CollectionReference usedUsernames;
    private Firestore db;
    @Autowired
    private PetDao petDao;
    @Autowired
    private FirestoreCollection collectionAdapter;
    @Autowired
    private FirestoreDocument documentAdapter;

    public UserDaoImpl() {
        FirebaseFactory firebaseFactory = FirebaseFactory.getInstance();
        myAuth = firebaseFactory.getFirebaseAuth();
        db = firebaseFactory.getFirestore();
        users = db.collection("users");
        usedUsernames = db.collection("used_usernames");
    }

    @Override
    public void createUser(UserToken token, UserEntity userEntity)
        throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        String username = userEntity.getUsername();
        if (!existsUsername(username)) {
            WriteBatch batch = documentAdapter.batch();
            saveUsername(token.getUid(), username, batch);
            String encodedPassword = new BCryptPasswordEncoder().encode(userEntity.getPassword());
            userEntity.setPassword(encodedPassword);
            documentAdapter.createDocumentWithId(Path.ofCollection(Collections.users), token.getUid(), userEntity, batch);
            documentAdapter.commitBatch(batch);
            updateDisplayName(token.getUid(), username);
        } else {
            throw new DatabaseAccessException(INVALID_USERNAME, USED_USERNAME_MESSAGE);
        }
    }

    @Override
    public void deleteFromDatabase(UserToken token) throws DatabaseAccessException, DocumentException {
        String uid = token.getUid();
        DocumentSnapshot userDoc = getDocumentSnapshot(users, uid);
        throwExceptionIfUserDoesNotExist(userDoc);
        String username = token.getUsername();
        petDao.deleteAllPets(username);
        deleteUserStorage(uid);
        WriteBatch batch = collectionAdapter.batch();
        deleteUserLikes(username, batch);
        collectionAdapter.commitBatch(batch);
        usedUsernames.document(Objects.requireNonNull(username)).delete();
        users.document(uid).delete();
    }

    @Override
    public void deleteById(UserToken token) throws FirebaseAuthException, DatabaseAccessException, DocumentException {
        deleteFromDatabase(token);
        myAuth.deleteUser(token.getUid());
    }

    @Override
    public UserEntity getUserData(UserToken token) throws DatabaseAccessException {
        DocumentSnapshot userDoc = getDocumentSnapshot(users, token.getUid());
        throwExceptionIfUserDoesNotExist(userDoc);
        return userDoc.toObject(UserEntity.class);
    }

    @Override
    public void updateField(UserToken token, String field, String newValue)
        throws FirebaseAuthException, DatabaseAccessException {
        String uid = token.getUid();
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

    @Override
    public boolean existsUsername(String username) throws DatabaseAccessException {
        return documentAdapter.documentExists(Path.ofDocument(Collections.used_usernames, username));
    }

    @Override
    public String getField(UserToken token, String field) throws DatabaseAccessException {
        DocumentSnapshot userDoc = getDocumentSnapshot(users, token.getUid());
        throwExceptionIfUserDoesNotExist(userDoc);
        return (String) userDoc.get(field);
    }

    @Override
    public String getFcmToken(String uid) throws DatabaseAccessException, DocumentException {
        return documentAdapter.getStringFromDocument(Path.ofDocument(Collections.users, uid), "FCM");
    }

    @Override
    public String getUid(String username) throws DatabaseAccessException {
        DocumentSnapshot usernameDoc = getDocumentSnapshot(usedUsernames, username);
        throwExceptionIfUserDoesNotExist(usernameDoc);
        return (String) usernameDoc.get(USER_KEY);
    }

    @Override
    public void addGroupSubscription(UserToken token, String groupName, WriteBatch batch) {
        DocumentReference user = users.document(token.getUid());
        Map<String, Object> data = new HashMap<>();
        data.put("groupSubscriptions", FieldValue.arrayUnion(groupName));
        batch.update(user, data);
    }

    @Override
    public void deleteGroupSubscription(UserToken token, String groupName, WriteBatch batch) {
        DocumentReference user = users.document(token.getUid());
        Map<String, Object> data = new HashMap<>();
        data.put("groupSubscriptions", FieldValue.arrayRemove(groupName));
        batch.update(user, data);
    }

    @Override
    public List<String> getUserSubscriptions(UserToken token) throws DatabaseAccessException {
        DocumentSnapshot user = getDocumentSnapshot(users, token.getUid());
        return (List<String>) user.get("groupSubscriptions");
    }

    @Override
    public void saveMessagingToken(UserToken token, String fcmToken) throws DatabaseAccessException, DocumentException {
        String currentToken = documentAdapter
            .getStringFromDocument(Path.ofDocument(Collections.users, token.getUid()), FCM);
        WriteBatch batch = documentAdapter.batch();
        if (currentToken != null) {
            updateTokenInSubscribedGroups(fcmToken, currentToken, batch);
        }
        documentAdapter.updateDocumentFields(batch, Path.ofDocument(Collections.users, token.getUid()), FCM, fcmToken);
        documentAdapter.commitBatch(batch);
    }

    /**
     * Gets the document snapshot for the api future given.
     *
     * @param collection The collection from which to get the document
     * @param docName The document name
     * @return The document snapshot for the api future given
     * @throws DatabaseAccessException If the deletion fails or if the user doesn't exist
     */
    private DocumentSnapshot getDocumentSnapshot(CollectionReference collection, String docName)
        throws DatabaseAccessException {
        ApiFuture<DocumentSnapshot> future = collection.document(docName).get();
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
     *
     * @param uid The user unique identifier
     */
    private void deleteUserStorage(String uid) {
        StorageDao storageDao = ((PetDaoImpl) petDao).getStorageDao();
        storageDao.deleteImageByName(uid + "/profile-image.png");
    }

    /**
     * Deletes all the user likes to messages.
     *
     * @param username The user's username
     * @param batch The batch where to write
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void deleteUserLikes(String username, WriteBatch batch) throws DatabaseAccessException {
        ApiFuture<QuerySnapshot> documentSnapshots = collectionAdapter
            .getCollectionGroupDocumentsWhereArrayContains(Collections.messages.name(), FIELD_LIKED_BY, username);
        try {
            for (DocumentSnapshot document : documentSnapshots.get().getDocuments()) {
                batch.update(document.getReference(), FIELD_LIKED_BY, FieldValue.arrayRemove(username));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException(UPDATE_FAILED_CODE, "The deletion of the user likes failed");
        }
    }

    /**
     * Updates the user's username.
     *
     * @param uid The unique identifier of the user
     * @param newUsername The new username for the account
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    private void updateUsername(String uid, String newUsername) throws DatabaseAccessException, FirebaseAuthException {
        DocumentSnapshot usernameDoc = getDocumentSnapshot(usedUsernames, newUsername);
        if (!usernameDoc.exists()) {
            String username = myAuth.getUser(uid).getDisplayName();
            WriteBatch batch = db.batch();
            updateNameOnSubscriptions(username, newUsername, batch);
            updateNameOnCreatedGroups(username, newUsername, batch);
            updateNameOnCreatedForums(username, newUsername, batch);
            updateNameOnCreatedMessages(username, newUsername, batch);
            deleteOldUsername(uid, batch);
            saveUsername(uid, newUsername, batch);
            Map<String, Object> data = new HashMap<>();
            data.put(USERNAME_FIELD, newUsername);
            batch.update(users.document(uid), data);
            try {
                batch.commit().get();
                updateDisplayName(uid, newUsername);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new DatabaseAccessException(UPDATE_FAILED_CODE, "The username update failed");
            }
        } else {
            throw new DatabaseAccessException(INVALID_USERNAME, USED_USERNAME_MESSAGE);
        }
    }

    /**
     * Updates the username on all the groups the user is subscribed to.
     *
     * @param username The current username
     * @param newUsername The new username
     * @param batch The batch of writes
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void updateNameOnSubscriptions(String username, String newUsername, WriteBatch batch)
        throws DatabaseAccessException {
        Query query = db.collectionGroup("members").whereEqualTo(USER_KEY, username);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            Map<String, Object> data = new HashMap<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                DocumentReference ref = document.getReference();
                data.put(USER_KEY, newUsername);
                batch.update(ref, data);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException(UPDATE_FAILED_CODE, "Failure when updating name in subscriptions");
        }
    }

    /**
     * Updates the username on all the groups the user has created.
     *
     * @param username The current username
     * @param newUsername The new username
     * @param batch The batch of writes
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void updateNameOnCreatedGroups(String username, String newUsername, WriteBatch batch)
        throws DatabaseAccessException {
        Query query = db.collection("groups").whereEqualTo("creator", username);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            Map<String, Object> data = new HashMap<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                DocumentReference ref = document.getReference();
                data.put("creator", newUsername);
                batch.update(ref, data);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException(UPDATE_FAILED_CODE, "Failure when updating name in created groups");
        }
    }

    /**
     * Updates the username on all the forums the user has created.
     *
     * @param username The current username
     * @param newUsername The new username
     * @param batch The batch of writes
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void updateNameOnCreatedForums(String username, String newUsername, WriteBatch batch)
        throws DatabaseAccessException {
        Query query = db.collectionGroup("forums").whereEqualTo("creator", username);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            Map<String, Object> data = new HashMap<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                DocumentReference ref = document.getReference();
                data.put("creator", newUsername);
                batch.update(ref, data);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException(UPDATE_FAILED_CODE, "Failure when updating name in created groups");
        }
    }

    /**
     * Updates the username on all the messages the user has created.
     *
     * @param username The current username
     * @param newUsername The new username
     * @param batch The batch of writes
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    private void updateNameOnCreatedMessages(String username, String newUsername, WriteBatch batch)
        throws DatabaseAccessException {
        Query query = db.collectionGroup("messages").whereEqualTo("creator", username);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        try {
            Map<String, Object> data = new HashMap<>();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                DocumentReference ref = document.getReference();
                data.put("creator", newUsername);
                batch.update(ref, data);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new DatabaseAccessException(UPDATE_FAILED_CODE, "Failure when updating name in created messages");
        }
    }

    /**
     * Updates the user's email.
     *
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
     * Updates the user's password.
     *
     * @param uid The unique identifier of the user
     * @param newPassword The new password for the account
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    private void updatePassword(String uid, String newPassword) throws FirebaseAuthException {
        UserRecord.UpdateRequest updateRequest = getUserRecord(uid);
        String encodedPassword = new BCryptPasswordEncoder().encode(newPassword);
        updateRequest.setPassword(encodedPassword);
        myAuth.updateUserAsync(updateRequest);
        users.document(uid).update(PASSWORD_FIELD, encodedPassword);
    }

    /**
     * Saves the username inside the used usernames collection.
     *
     * @param username The username to save
     * @param batch The batch of writes
     */
    private void saveUsername(String uid, String username, WriteBatch batch) {
        Map<String, String> docData = new HashMap<>();
        docData.put(USER_KEY, uid);
        documentAdapter.createDocumentWithId(Path.ofCollection(Collections.used_usernames), username, docData, batch);
    }

    /**
     * Deletes the old username from the database.
     *
     * @param uid The unique identifier of the user
     * @param batch The batch of writes
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    private void deleteOldUsername(String uid, WriteBatch batch) throws FirebaseAuthException {
        String oldUsername = myAuth.getUser(uid).getDisplayName();
        batch.delete(usedUsernames.document(oldUsername));
    }

    /**
     * Updates the display name.
     *
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
     *
     * @param uid The unique identifier of the user
     * @return An update request for the user data
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    private UserRecord.UpdateRequest getUserRecord(String uid) throws FirebaseAuthException {
        return myAuth.getUser(uid).updateRequest();
    }

    /**
     * Throws DatabaseAccessException if the user does not exist.
     *
     * @param userDoc The document snapshot of the user
     * @throws DatabaseAccessException If the user does not exist
     */
    private void throwExceptionIfUserDoesNotExist(DocumentSnapshot userDoc) throws DatabaseAccessException {
        if (!userDoc.exists()) {
            throw new DatabaseAccessException(INVALID_USER, USER_DOES_NOT_EXIST_MESSAGE);
        }
    }

    /**
     * Updates the FCM token in all the groups the user is subscribed to.
     *
     * @param token The new FCM token
     * @param currentToken The current FCM token
     * @param batch The batch of writes
     * @throws DatabaseAccessException When the operation is interrupted
     * @throws DocumentException When the update fails
     */
    private void updateTokenInSubscribedGroups(String token, String currentToken, WriteBatch batch)
        throws DatabaseAccessException, DocumentException {
        ApiFuture<QuerySnapshot> subscribedGroups = collectionAdapter
            .getDocumentsWhereArrayContains(Path.ofCollection(Collections.groups), "notification-tokens", currentToken);
        try {
            for (QueryDocumentSnapshot group : subscribedGroups.get().getDocuments()) {
                batch.update(group.getReference(), FCM, FieldValue.arrayRemove(currentToken), FCM,
                    FieldValue.arrayUnion(token));
            }
        } catch (InterruptedException e) {
            throw new DatabaseAccessException(WRITE_FAILED_CODE, e.getMessage());
        } catch (ExecutionException e) {
            throw new DocumentException(WRITE_FAILED_CODE, e.getMessage());
        }
    }
}
