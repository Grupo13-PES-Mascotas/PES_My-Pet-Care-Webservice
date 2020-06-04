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
    private Integer messagesBanned;

    public UserEntity() { }

    public UserEntity(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        groupSubscriptions = new ArrayList<>();
        messagesBanned = 0;
    }

    /**
     * Creates a user entity with the given username and email.
     * @param username The user's username
     * @param password The user's password
     * @param email The user's email
     * @param groupSubscriptions The user's group subscriptions
     * @param messagesBanned Number of messages created by the user that are banned.
     */
    public UserEntity(String username, String password, String email, List<String> groupSubscriptions,
                      Integer messagesBanned) {
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setGroupSubscriptions(groupSubscriptions);
        setMessagesBanned(messagesBanned);
    }

    public UserEntity(User userData) {
        this.username = userData.getUsername();
        this.password = userData.getPassword();
        this.email = userData.getEmail();
        this.groupSubscriptions = new ArrayList<>();
        this.messagesBanned = 0;
    }
}
