package org.pesmypetcare.webservice.entity;

import lombok.Data;

@Data
public class User {
    private String id;
    private String username;
    private String email;
    private String password;
}
