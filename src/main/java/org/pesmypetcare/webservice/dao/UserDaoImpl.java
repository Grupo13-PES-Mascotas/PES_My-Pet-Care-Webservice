package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class UserDaoImpl implements UserDao {
    private FirebaseAuth myAuth;
    private CollectionReference users;

    public UserDaoImpl() {
        FirebaseFactory firebaseFactory = FirebaseFactory.getInstance();
        myAuth = firebaseFactory.getFirebaseAuth();
        Firestore db = firebaseFactory.getFirestore();
        users = db.collection("users");
    }

    @Override
    public void createUser(UserEntity userEntity) {
        users.document(userEntity.getUsername()).set(userEntity);
    }

    @Override
    public void createUserAuth(UserEntity user, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setDisplayName(user.getUsername())
            .setEmail(user.getEmail()).setEmailVerified(false).setPassword(password).setUid(user.getUsername());
        myAuth.createUser(request);
    }

    @Override
    public void deleteById(String uid) throws DatabaseAccessException, FirebaseAuthException {
        DocumentReference userRef = users.document(uid);
        try {
            ApiFuture<QuerySnapshot> future = userRef.collection("pets").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("deletion-failed", e.getMessage());
        }
        userRef.delete();
        myAuth.deleteUser(uid);
    }

    @Override
    public UserEntity getUserData(String uid) throws DatabaseAccessException {
        DocumentReference docRef = users.document(uid);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot userDoc;
        try {
            userDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException("deletion-failed", e.getMessage());
        }
        if (!userDoc.exists()) {
            throw new DatabaseAccessException("invalid-user", "The user does not exist");
        }
        return userDoc.toObject(UserEntity.class);
    }

    @Override
    public void updateEmail(String uid, String newEmail) throws FirebaseAuthException {
        UserRecord.UpdateRequest updateRequest = getUserRecord(uid);
        updateRequest.setEmail(newEmail);
        myAuth.updateUserAsync(updateRequest);
        users.document(uid).update("email", newEmail);
    }

    @Override
    public void updatePassword(String uid, String newPassword) throws FirebaseAuthException {
        //TODO: Apply password encryption
        UserRecord.UpdateRequest updateRequest = getUserRecord(uid);
        updateRequest.setPassword(newPassword);
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
