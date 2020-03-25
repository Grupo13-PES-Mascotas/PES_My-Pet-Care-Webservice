package org.pesmypetcare.webservice.controller.usermanager;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    private UserService userService;

    @DeleteMapping("/{username}/delete")
    public void deleteAccount(@PathVariable String username) throws DatabaseAccessException, FirebaseAuthException {
        userService.deleteById(username);
    }

    @GetMapping("/{username}")
    public UserEntity getUserData(@PathVariable String username) throws DatabaseAccessException {
        return userService.getUserData(username);
    }

    @PutMapping("/{username}/update/email")
    public void updateEmail(@PathVariable String username, @RequestBody String newEmail) throws FirebaseAuthException {
        userService.updateEmail(username, newEmail);
    }

    @PutMapping("/{username}/update/password")
    public void updatePassword(@PathVariable String username, @RequestBody String newPassword)
        throws FirebaseAuthException {
        userService.updatePassword(username, newPassword);
    }
}
