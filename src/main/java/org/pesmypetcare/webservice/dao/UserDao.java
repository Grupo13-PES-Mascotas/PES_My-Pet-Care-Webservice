package org.pesmypetcare.webservice.dao;

import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface UserDao {
    public List<User> findAll();
    public User findById(int id);
    public void save(User user);
    public void deleteById(int id);
}
