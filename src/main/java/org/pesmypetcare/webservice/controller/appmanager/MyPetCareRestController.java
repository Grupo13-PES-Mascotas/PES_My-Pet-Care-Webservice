package org.pesmypetcare.webservice.controller.appmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.usermanager.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@RestController
public class MyPetCareRestController {
    @Autowired
    private UserService userService;

    /**
     * Given a username, an email and a password creates the user on the data base.
     * @param user The request body that contains the username, password and email for the new account
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody Map<String, Object> user) throws DatabaseAccessException, FirebaseAuthException {
        ObjectMapper mapper = new ObjectMapper();
        UserEntity userEntity = mapper.convertValue(user.get("user"), UserEntity.class);
        userService.createUser((String) user.get("uid"), userEntity);
    }

    /**
     * Checks if a username is already in use.
     * @param username The username to check
     * @return True if the username is in use
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/usernames")
    public Map<String, Boolean> usernameAlreadyInUse(@RequestParam String username) throws DatabaseAccessException {
        boolean exists = userService.existsUsername(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }
}
