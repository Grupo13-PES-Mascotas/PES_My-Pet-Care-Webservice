package org.pesmypetcare.webservice.controller.appmanager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import org.pesmypetcare.webservice.entity.User;
import org.pesmypetcare.webservice.securingService.FirebaseFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class MyPetCareRestController {
    FirebaseFactory firebaseFactory = FirebaseFactory.getInstance();
    User u;

    @PostMapping("/signup")
    public Object login(@RequestParam String username, @RequestParam String email, @RequestParam String password)
        throws FirebaseAuthException {
        CreateRequest request = new CreateRequest().setDisplayName(username)
            .setEmail(email).setEmailVerified(false).setPassword(password);
        UserRecord userRecord = null;
        FirebaseAuth myAuth = firebaseFactory.getFirebaseAuth();
        userRecord = myAuth.createUser(request);
        return "Successfully created new user: " + userRecord.getUid();
    }

    @GetMapping("/users")
    @ResponseBody
    public User users() {
        return u;
    }
}
