package org.pesmypetcare.webservice.entity;

import lombok.Data;

@Data
public class UserEntity {
    private String username;
    private String email;

    public UserEntity() {
        setUsername(null);
        setEmail(null);
    }

    /**
     * Creates a user entity with the given username and email.
     * @param username The user's username
     * @param email The user's email
     */
    public UserEntity(String username, String email) {
        setUsername(username);
        setEmail(email);
    }
}
