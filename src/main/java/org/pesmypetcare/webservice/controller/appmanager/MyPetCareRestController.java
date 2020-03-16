package org.pesmypetcare.webservice.controller.appmanager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import org.pesmypetcare.webservice.securingservice.FirebaseFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyPetCareRestController {
    private FirebaseFactory firebaseFactory = FirebaseFactory.getInstance();

    @PostMapping("/signup")
    public Object login(@RequestParam String username, @RequestParam String email, @RequestParam String password)
        throws FirebaseAuthException {
        CreateRequest request = new CreateRequest().setDisplayName(username)
            .setEmail(email).setEmailVerified(false).setPassword(password);
        UserRecord userRecord;
        FirebaseAuth myAuth = firebaseFactory.getFirebaseAuth();
        userRecord = myAuth.createUser(request);
        return "Successfully created new user: " + userRecord.getUid();
    }
}
