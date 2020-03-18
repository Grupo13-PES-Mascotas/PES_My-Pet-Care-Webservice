package org.pesmypetcare.webservice.controller.appmanager;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord.CreateRequest;
import org.pesmypetcare.webservice.securingservice.FirebaseFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class MyPetCareRestController {
    private FirebaseFactory firebaseFactory = FirebaseFactory.getInstance();

    @PostMapping("/signup")
    public void signUp(@RequestParam String username, @RequestParam String email, @RequestParam String password)
        throws FirebaseAuthException {
        CreateRequest request = new CreateRequest().setDisplayName(username)
            .setEmail(email).setEmailVerified(false).setPassword(password).setUid(username);
        FirebaseAuth myAuth = firebaseFactory.getFirebaseAuth();
        myAuth.createUser(request);

        storeUser(username, email);
    }

    private void storeUser(@RequestParam String username, @RequestParam String email) {
        Firestore db = firebaseFactory.getFirestore();
        DocumentReference docRef = db.collection("users").document(username);
        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email);
        docRef.set(data);
    }
}
