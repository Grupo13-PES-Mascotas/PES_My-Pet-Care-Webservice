package org.pesmypetcare.webservice.entity.usermanager;

import lombok.Data;

@Data
public class UserEntity {
    private String username;
    private String password;
    private String email;

    public UserEntity() { }

    /**
     * Creates a user entity with the given username and email.
     * @param username The user's username
     * @param password The user's password
     * @param email The user's email
     */
    public UserEntity(String username, String password, String email) {
        setUsername(username);
        setPassword(password);
        setEmail(email);
    }
}
