package org.pesmypetcare.webservice.dao;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.securingservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class UserDaoImpl implements UserDao {
    private FirebaseFactory firebaseFactory;

    UserDaoImpl() {
        firebaseFactory = FirebaseFactory.getInstance();
    }

    @Override
    public void save(UserEntity userEntity) {
        Firestore db = FirebaseFactory.getInstance().getFirestore();
        DocumentReference docRef = db.collection("users").document(userEntity.getUsername());
        HashMap<String, Object> data = new HashMap<>();
        data.put("email", userEntity.getEmail());
        docRef.set(data);
    }

    @Override
    public void deleteById(int uid) {
        //TODO
    }

    @Override
    public void saveAuth(UserEntity user, String password) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setDisplayName(user.getUsername())
            .setEmail(user.getEmail()).setEmailVerified(false).setPassword(password).setUid(user.getUsername());
        FirebaseAuth myAuth = firebaseFactory.getFirebaseAuth();
        myAuth.createUser(request);
    }
}
