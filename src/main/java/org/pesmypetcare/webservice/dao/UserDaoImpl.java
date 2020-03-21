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

import java.util.concurrent.ExecutionException;

@Repository
public class UserDaoImpl implements UserDao {
    private final String USERS_KEY;
    private FirebaseFactory firebaseFactory;
    private CollectionReference users;

    public UserDaoImpl() {
        firebaseFactory = FirebaseFactory.getInstance();
        Firestore db = FirebaseFactory.getInstance().getAdminFirestore();
        USERS_KEY = "users";
        users = db.collection(USERS_KEY);
    }

    @Override
    public void save(UserEntity userEntity) {
        Firestore db = FirebaseFactory.getInstance().getAdminFirestore();
            FirebaseFactory.getInstance().getAdminFirestore();
        users = db.collection(USERS_KEY);
        users.document(userEntity.getUsername()).set(userEntity);
    }

    @Override
    public void saveAuth(UserEntity user, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setDisplayName(user.getUsername())
            .setEmail(user.getEmail()).setEmailVerified(false).setPassword(password).setUid(user.getUsername());
        FirebaseAuth myAuth = firebaseFactory.getFirebaseAuth();
        myAuth.createUser(request);
    }

    @Override
    public void deleteById(String uid) {
        //TODO
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

}
