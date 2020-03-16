package org.pesmypetcare.webservice.service;



import org.pesmypetcare.webservice.entity.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(int id);
    void save(User user);
    void deleteById(int id);
}
