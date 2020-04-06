package org.pesmypetcare.webservice.controller.usermanager;

import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/users")
public class UserRestController {
    //TODO: Document class
    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public UserEntity user(@PathVariable String username) throws DatabaseAccessException {
        UserDao dao = new UserDaoImpl();
        UserEntity user = null;
        try {
            user = dao.getUserData(username);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return user;
    }
}
