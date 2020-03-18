package org.pesmypetcare.webservice.dao;

import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.UserEntity;

import java.util.List;

public interface UserDao {
    List<UserEntity> findAll();
    UserEntity findById(int id);
    void save(UserEntity userEntity);
    void deleteById(int id);
    void saveAuth(UserEntity userEntity, String password) throws FirebaseAuthException;
}
