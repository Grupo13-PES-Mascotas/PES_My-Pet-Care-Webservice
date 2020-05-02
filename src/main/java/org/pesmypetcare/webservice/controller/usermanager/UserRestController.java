package org.pesmypetcare.webservice.controller.usermanager;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.usermanager.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@RestController
@RequestMapping("/users")
public class UserRestController {
    private static final String TOKEN = "token";
    @Autowired
    private UserService userService;

    /**
     * Deletes the user.
     * @param token The personal access token ofDocument the user
     * @param username The user's username
     * @param db If true deletes the user only from the database, otherwise deletes the user entirely
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    @DeleteMapping("/{username}")
    public void deleteAccount(@RequestHeader(TOKEN) String token, @PathVariable String username,
                              @RequestParam(required = false) boolean db)
        throws DatabaseAccessException, FirebaseAuthException {
        if (db) {
            userService.deleteFromDatabase(token, username);
        } else {
            userService.deleteById(token, username);
        }
    }

    /**
     * Retrieves the user data.
     * @param token The personal access token ofDocument the user
     * @param username The user's username
     * @return A user entity that contains the user data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{username}")
    public UserEntity getUserData(@RequestHeader(TOKEN) String token,
                                  @PathVariable String username) throws DatabaseAccessException {
        return userService.getUserData(token, username);
    }

    /**
     * Updates the user email bound to the account.
     * @param token The personal access token ofDocument the user
     * @param username The user's username
     * @param value The new value
     * @throws FirebaseAuthException If an error occurs when updating the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping("/{username}")
    public void updateField(@RequestHeader(TOKEN) String token, @PathVariable String username,
                            @RequestBody Map<String, String> value)
        throws FirebaseAuthException, DatabaseAccessException {
        String field = value.keySet().iterator().next();
        userService.updateField(token, username, field, value.get(field));
    }

    @GetMapping("/subscriptions")
    public List<String> getUserSubscriptions(@RequestHeader String token, @RequestParam String username) throws DatabaseAccessException {
        return userService.getUserSubscriptions(token, username);
    }
}
