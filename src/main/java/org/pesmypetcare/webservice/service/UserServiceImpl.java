package org.pesmypetcare.webservice.service;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.dao.UserDao;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public void save(UserEntity userEntity) {
        userDao.save(userEntity);
    }

    @Override
    public void deleteById(int id) {
        userDao.deleteById(id);
    }

    @Override
    public void saveAuth(UserEntity user, String password) throws FirebaseAuthException {
        userDao.saveAuth(user, password);
    }
}