package org.pesmypetcare.webservice.controller.appmanager;

import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MyPetCareRestController {
    @Autowired
    private UserService userService;

    /**
     * Given a username, an email and a password creates the user on the data base.
     * @param request The request body that contains the uid, username, password and email for the new account
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PostMapping("/signup")
    public void signUp(@RequestBody Map<String, Object> request) throws DatabaseAccessException {
            userService.createUser((String) request.get("uid"), (UserEntity) request.get("user"));
    }
}
