package org.pesmypetcare.webservice.dao;

import org.pesmypetcare.webservice.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public User findById(int id) {
        return null;
    }

    @Override
    public void save(User user) {
        //TO-DO
    }

    @Override
    public void deleteById(int id) {
        //TO-DO
    }
}
