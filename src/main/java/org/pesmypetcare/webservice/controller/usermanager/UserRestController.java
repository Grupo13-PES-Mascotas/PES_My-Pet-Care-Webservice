package org.pesmypetcare.webservice.controller.usermanager;

import org.pesmypetcare.webservice.dao.UserDao;
import org.pesmypetcare.webservice.dao.UserDaoImpl;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/users")
public class UserRestController {
    //TODO
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
