package org.pesmypetcare.webservice.controller.appmanager;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyPetCareRestController {
    @Autowired
    private UserService userService;

    /**
     * Given a username, an email and a password creates the user on the data base.
     * @param user The entity that contains the username and the email for the new account
     * @param password The password for the new account
     * @throws FirebaseAuthException If a user tries to create an account with an existing username or
     * email, or with an invalid email
     */
    /*@PostMapping("/signup")
    public void signUp(@RequestBody UserEntity user, @RequestParam String password)
        throws FirebaseAuthException {
        userService.saveAuth(user, password);
        userService.save(user);
    }*/
}
