package org.pesmypetcare.webservice.controller.usermanager;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.service.usermanager.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
     *
     * @param token The personal access token of the user
     * @param db If true deletes the user only from the database, otherwise deletes the user entirely
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws FirebaseAuthException If an error occurs when retrieving the data
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@RequestHeader(TOKEN) String token, @RequestParam(required = false) boolean db)
        throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        if (db) {
            userService.deleteFromDatabase(token);
        } else {
            userService.deleteById(token);
        }
    }

    /**
     * Retrieves the user data.
     *
     * @param token The personal access token of the user
     * @return A user entity that contains the user data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping
    public UserEntity getUserData(@RequestHeader(TOKEN) String token)
        throws DatabaseAccessException {
        return userService.getUserData(token);
    }

    /**
     * Updates the user email bound to the account.
     *
     * @param token The personal access token of the user
     * @param value The new value
     * @throws FirebaseAuthException If an error occurs when updating the data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateField(@RequestHeader(TOKEN) String token, @RequestBody Map<String, String> value)
        throws FirebaseAuthException, DatabaseAccessException {
        String field = value.keySet().iterator().next();
        userService.updateField(token, field, value.get(field));
    }

    /**
     * Gets all the group subscriptions of the user.
     *
     * @param token The personal access token of the user
     * @return A list with all the groups the user is subscribed to
     * @throws DatabaseAccessException If an error occurs when updating the database
     */
    @GetMapping("/subscriptions")
    public List<String> getUserSubscriptions(@RequestHeader String token)
        throws DatabaseAccessException {
        return userService.getUserSubscriptions(token);
    }

    /**
     * Saves a token for the application messaging.
     *
     * @param token The personal access token of the user
     * @param fcmToken The FCM token to save
     * @throws DatabaseAccessException If an error occurs when updating the database
     * @throws DocumentException If the user of the token does not exist
     */
    @PutMapping("/fcm-token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveMessagingToken(@RequestHeader String token, @RequestHeader String fcmToken)
        throws DatabaseAccessException, DocumentException {
        userService.saveMessagingToken(token, fcmToken);
    }
}
