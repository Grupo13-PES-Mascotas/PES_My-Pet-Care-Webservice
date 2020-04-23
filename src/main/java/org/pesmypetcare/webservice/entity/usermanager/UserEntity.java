package org.pesmypetcare.webservice.entity.usermanager;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Santiago Del Rey
 */
@Data
public class UserEntity {
    private String username;
    private String password;
    private String email;
    private List<String> groupSubscriptions;

    public UserEntity() { }

    public UserEntity(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        groupSubscriptions = new ArrayList<>();
    }

    /**
     * Creates a user entity with the given username and email.
     * @param username The user's username
     * @param password The user's password
     * @param email The user's email
     * @param groupSubscriptions The user's group subscriptions
     */
    public UserEntity(String username, String password, String email, List<String> groupSubscriptions) {
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setGroupSubscriptions(groupSubscriptions);
    }
}
