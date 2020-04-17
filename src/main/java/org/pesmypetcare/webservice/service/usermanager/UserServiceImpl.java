package org.pesmypetcare.webservice.service.usermanager;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.dao.usermanager.UserDao;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Santiago Del Rey
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public void createUserAuth(UserEntity user, String password) throws FirebaseAuthException {
        userDao.createUserAuth(user, password);
    }

    @Override
    public void createUser(String uid, UserEntity userEntity) throws DatabaseAccessException, FirebaseAuthException {
        userDao.createUser(uid, userEntity);
    }

    @Override
    public void deleteFromDatabase(String token, String uid) throws DatabaseAccessException {
        userDao.deleteFromDatabase(uid);
    }

    @Override
    public void deleteById(String token, String uid) throws DatabaseAccessException, FirebaseAuthException {
        userDao.deleteById(uid);
    }

    @Override
    public UserEntity getUserData(String token, String uid) throws DatabaseAccessException {
        return userDao.getUserData(uid);
    }

    @Override
    public void updateField(String token, String uid, String field, String newValue)
        throws FirebaseAuthException, DatabaseAccessException {
        userDao.updateField(uid, field, newValue);
    }

    @Override
    public boolean existsUsername(String username) throws DatabaseAccessException {
        return userDao.existsUsername(username);
    }
}
