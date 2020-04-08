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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    private UserService userService;

    /**
     * Deletes the user account.
     * @param token The personal access token of the user
     * @param username The user's username
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    @DeleteMapping("/{username}/delete")
    public void deleteAccount(@RequestHeader("token") String token,
                              @PathVariable String username) throws DatabaseAccessException, FirebaseAuthException {
        userService.deleteById(username);
    }

    /**
     * Retrieves the user data.
     * @param token The personal access token of the user
     * @param username The user's username
     * @return A user entity that contains the user data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{username}")
    public UserEntity getUserData(@RequestHeader("token") String token,
                                  @PathVariable String username) throws DatabaseAccessException {
        return userService.getUserData(username);
    }

    /**
     * Updates the user email bound to the account.
     * @param token The personal access token of the user
     * @param username The user's username
     * @param valueMap Entity that contains the value that the field will have.
     * @throws FirebaseAuthException If an error occurs when updating the data
     */
    @PutMapping("/{username}/update/email")
    public void updateEmail(@RequestHeader("token") String token,
                            @PathVariable String username, @RequestBody Map<String, String> valueMap) 
        throws FirebaseAuthException {
        userService.updateEmail(username, valueMap.get("password"));
    }

    /**
     * Updates the user password for the account.
     * @param token The personal access token of the user
     * @param username The user's username
     * @param valueMap Entity that contains the value that the field will have.
     * @throws FirebaseAuthException If an error occurs when updating the data
     */
    @PutMapping("/{username}/update/password")
    public void updatePassword(@RequestHeader("token") String token,
                               @PathVariable String username, @RequestBody Map<String, String> valueMap)
        throws FirebaseAuthException {
        userService.updatePassword(username, valueMap.get("password"));
    }
}
