package org.pesmypetcare.webservice.controller.usermanager;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    private UserService userService;

    /**
     * Deletes the user.
     * @param token The personal access token of the user
     * @param username The user's username
     * @param db If true deletes the user only from the database, otherwise deletes the user entirely
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     */
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@RequestHeader("token") String token, @PathVariable String username,
                              @RequestParam(required = false) boolean db)
        throws DatabaseAccessException, FirebaseAuthException {
        if (db) {
            userService.deleteFromDatabase(username);
        } else {
            userService.deleteById(username);
        }
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
     * @param value The new value
     * @throws FirebaseAuthException If an error occurs when updating the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateField(@RequestHeader("token") String token, @PathVariable String username,
                            @RequestBody Map<String, String> value)
        throws FirebaseAuthException, DatabaseAccessException {
        String field = value.keySet().iterator().next();
        userService.updateField(username, field, value.get(field));
    }
}
