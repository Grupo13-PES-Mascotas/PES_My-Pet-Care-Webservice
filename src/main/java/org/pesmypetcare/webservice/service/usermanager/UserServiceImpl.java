package org.pesmypetcare.webservice.service.usermanager;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.dao.usermanager.UserDao;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserTokenImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public void createUser(String token, UserEntity userEntity) throws DatabaseAccessException, FirebaseAuthException {
        userDao.createUser(new UserTokenImpl(token), userEntity);
    }

    @Override
    public void deleteFromDatabase(String token) throws DatabaseAccessException, DocumentException {
        userDao.deleteFromDatabase(new UserTokenImpl(token));
    }

    @Override
    public void deleteById(String token)
        throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        userDao.deleteById(new UserTokenImpl(token));
    }

    @Override
    public UserEntity getUserData(String token) throws DatabaseAccessException {
        return userDao.getUserData(new UserTokenImpl(token));
    }

    @Override
    public void updateField(String token, String field, String newValue)
        throws FirebaseAuthException, DatabaseAccessException {
        userDao.updateField(new UserTokenImpl(token), field, newValue);
    }

    @Override
    public boolean existsUsername(String username) throws DatabaseAccessException {
        return userDao.existsUsername(username);
    }

    @Override
    public void saveMessagingToken(String token, String fcmToken)
        throws DatabaseAccessException, DocumentException {
        userDao.saveMessagingToken(new UserTokenImpl(token), fcmToken);
    }

    @Override
    public List<String> getUserSubscriptions(String token) throws DatabaseAccessException {
        return userDao.getUserSubscriptions(new UserTokenImpl(token));
    }
}
