package org.pesmypetcare.webservice.controller.usermanager;

import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserRestController {
    //TODO: Implement methods
    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public UserEntity getUserData(@PathVariable String username) throws DatabaseAccessException {
        return userService.getUserData(username);
    }

    @PostMapping("/{username}/update/email")
    public String updateEmail(@PathVariable String username, @RequestBody String newEmail) {
        return "Not implemented yet";
    }

    @PostMapping("/{username}/update/password")
    public String updatePassword(@PathVariable String username, @RequestBody String newPassword) {
        return "Not implemented yet";
    }

    @DeleteMapping("/{username}/delete")
    public String deleteAccount(@PathVariable String username) {
        return "Not implemented yet";
    }
}
