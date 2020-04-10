package org.pesmypetcare.webservice.service;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.dao.UserDao;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public void createUserAuth(UserEntity user, String password) throws FirebaseAuthException {
        userDao.createUserAuth(user, password);
    }

    @Override
    public void createUser(String uid, UserEntity userEntity) throws DatabaseAccessException {
        userDao.createUser(uid, userEntity);
    }

    @Override
    public void deleteFromDatabase(String uid) throws DatabaseAccessException {
        userDao.deleteFromDatabase(uid);
    }

    @Override
    public void deleteById(String uid) throws DatabaseAccessException, FirebaseAuthException {
        userDao.deleteById(uid);
    }

    @Override
    public UserEntity getUserData(String uid) throws DatabaseAccessException {
        return userDao.getUserData(uid);
    }

    @Override
    public void updateField(String uid, String field, String newValue) throws FirebaseAuthException, DatabaseAccessException {
        userDao.updateField(uid, field, newValue);
    }
}
