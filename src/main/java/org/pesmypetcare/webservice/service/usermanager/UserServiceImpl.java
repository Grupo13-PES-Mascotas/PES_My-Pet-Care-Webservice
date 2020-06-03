package org.pesmypetcare.webservice.service.usermanager;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.dao.usermanager.UserDao;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;
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
    private UserToken userToken;

    public UserToken makeUserToken(String token) {
        return new UserTokenImpl(token);
    }

    @Override
    public void createUser(String token, UserEntity userEntity)
        throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        userToken = makeUserToken(token);
        userDao.createUser(userToken, userEntity);
    }

    @Override
    public void deleteFromDatabase(String token) throws DatabaseAccessException, DocumentException {
        userToken = makeUserToken(token);
        userDao.deleteFromDatabase(userToken);
    }

    @Override
    public void deleteById(String token)
        throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        userToken = makeUserToken(token);
        userDao.deleteById(userToken);
    }

    @Override
    public UserEntity getUserData(String token) throws DatabaseAccessException {
        userToken = makeUserToken(token);
        return userDao.getUserData(userToken);
    }

    @Override
    public void updateField(String token, String field, String newValue)
        throws FirebaseAuthException, DatabaseAccessException {
        userToken = makeUserToken(token);
        userDao.updateField(userToken, field, newValue);
    }

    @Override
    public boolean existsUsername(String username) throws DatabaseAccessException {
        return userDao.existsUsername(username);
    }

    @Override
    public void saveMessagingToken(String token, String fcmToken)
        throws DatabaseAccessException, DocumentException {
        userToken = makeUserToken(token);
        userDao.saveMessagingToken(userToken, fcmToken);
    }

    @Override
    public List<String> getUserSubscriptions(String token) throws DatabaseAccessException {
        userToken = makeUserToken(token);
        return userDao.getUserSubscriptions(userToken);
    }
}
