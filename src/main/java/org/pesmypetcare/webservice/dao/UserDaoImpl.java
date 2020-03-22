package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
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
    private final String USERS_KEY;
    private FirebaseFactory firebaseFactory;
    private FirebaseAuth myAuth;
    private Firestore db;
    private CollectionReference users;

    public UserDaoImpl() {
        firebaseFactory = FirebaseFactory.getInstance();
        myAuth = firebaseFactory.getFirebaseAuth();
        db = FirebaseFactory.getInstance().getFirestore();
        USERS_KEY = "users";
        users = db.collection(USERS_KEY);
    }

    @Override
    public void save(UserEntity userEntity) {
        users.document(userEntity.getUsername()).set(userEntity);
    }

    @Override
    public void saveAuth(UserEntity user, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setDisplayName(user.getUsername())
            .setEmail(user.getEmail()).setEmailVerified(false).setPassword(password).setUid(user.getUsername());
        myAuth.createUser(request);
    }

    @Override
    public void deleteById(String uid) throws DatabaseAccessException {
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
    }

    @Override
    public UserEntity getUserData(String uid) throws ExecutionException, InterruptedException, DatabaseAccessException {
        DocumentReference docRef = users.document(uid);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot userDoc = future.get();
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

    private UserRecord.UpdateRequest getUserRecord(String uid) throws FirebaseAuthException {
        return myAuth.getUser(uid).updateRequest();
    }
}
