package org.pesmypetcare.webservice.controller.usermanager;

import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usermanager")
public class UserRestController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<UserEntity> findAll() {
        return userService.findAll();
    }

    @GetMapping("/users/{userId}")
    public UserEntity getUser(@PathVariable int userId) {
        UserEntity userEntity = userService.findById(userId);

        if (userEntity == null) {
            throw new RuntimeException("UserEntity id not found: " + userId);
        }
        return userEntity;
    }
}
