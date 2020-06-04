package org.pesmypetcare.webservice.service.usermanager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.pesmypetcare.webservice.dao.usermanager.UserDao;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.FirebaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
@Service
public class UserServiceImpl implements UserService {
    private FirebaseAuth auth;

    @Autowired
    private UserDao userDao;

    public UserServiceImpl() {
        this.auth = FirebaseFactory.getInstance().getFirebaseAuth();
    }

    @Override
    public void createUser(String uid, UserEntity userEntity) throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        userDao.createUser(uid, userEntity);
    }

    @Override
    public void deleteFromDatabase(String token, String uid) throws DatabaseAccessException, DocumentException {
        userDao.deleteFromDatabase(uid);
    }

    @Override
    public void deleteById(String token, String uid)
        throws DatabaseAccessException, FirebaseAuthException, DocumentException {
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

    @Override
    public void saveMessagingToken(String token, String fcmToken)
        throws DatabaseAccessException, DocumentException, BadCredentialsException {
        try {
            FirebaseToken firebaseToken = auth.verifyIdToken(token);
            userDao.saveMessagingToken(firebaseToken.getUid(), fcmToken);
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            throw new BadCredentialsException("The token provided is not correct.");
        }
    }

    @Override
    public List<String> getUserSubscriptions(String token, String username) throws DatabaseAccessException {
        return userDao.getUserSubscriptions(username);
    }
}
