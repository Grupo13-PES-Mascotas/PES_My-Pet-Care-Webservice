package org.pesmypetcare.webservice.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class UserEntityStub {
    private String uid;
    private String username;
    private String email;

    public UserEntityStub() {
        uid = "user";
        username = uid;
        email = "user@mail.com";
    }
}
