package org.pesmypetcare.webservice.dao;

import org.pesmypetcare.webservice.entity.User;

import java.util.List;

public interface UserDao {
    List<User> findAll();
    User findById(int id);
    void save(User user);
    void deleteById(int id);
}
