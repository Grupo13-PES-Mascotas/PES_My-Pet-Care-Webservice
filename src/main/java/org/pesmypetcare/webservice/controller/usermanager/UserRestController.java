package org.pesmypetcare.webservice.controller.usermanager;

import org.pesmypetcare.webservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usermanager")
public class UserRestController {
    @Autowired
    private UserService userService;

    /*@GetMapping("/users/{userId}")
    public UserEntity getUser(@PathVariable int userId) {
        UserEntity userEntity = userService.findById(userId);

        if (userEntity == null) {
            throw new RuntimeException("UserEntity id not found: " + userId);
        }
        return userEntity;
    }*/
}
