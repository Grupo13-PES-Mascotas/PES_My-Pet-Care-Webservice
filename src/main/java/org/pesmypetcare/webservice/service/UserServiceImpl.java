package org.pesmypetcare.webservice.service;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.dao.StorageDao;
import org.pesmypetcare.webservice.dao.UserDao;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private StorageDao storageDao;

    @Override
    public void createUserAuth(UserEntity user, String password) throws FirebaseAuthException {
        userDao.createUserAuth(user, password);
    }

    @Override
    public void createUser(UserEntity userEntity) {
        userDao.createUser(userEntity);
    }

    @Override
    public void deleteById(String id) throws DatabaseAccessException, FirebaseAuthException {
        userDao.deleteById(id);
    }

    @Override
    public UserEntity getUserData(String uid) throws DatabaseAccessException {
        return userDao.getUserData(uid);
    }

    @Override
    public void updateEmail(String uid, String newEmail) throws FirebaseAuthException {
        userDao.updateEmail(uid, newEmail);
    }

    @Override
    public void updatePassword(String uid, String newPassword) throws FirebaseAuthException {
        userDao.updatePassword(uid, newPassword);
    }

    @Override
    public void saveProfileImage(String uid, String imgName, byte[] img) {
        storageDao.uploadImage(uid + "-" + imgName, img);
    }

    @Override
    public byte[] getProfileImage(String uid, String imgName) {
        return storageDao.downloadImage(uid + "-" + imgName);
    }
}
