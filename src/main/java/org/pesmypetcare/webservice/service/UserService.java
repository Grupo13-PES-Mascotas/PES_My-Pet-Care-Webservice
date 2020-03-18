package org.pesmypetcare.webservice.service;



import com.google.firebase.auth.FirebaseAuthException;
import org.pesmypetcare.webservice.entity.UserEntity;

import java.util.List;

public interface UserService {
    List<UserEntity> findAll();
    UserEntity findById(int id);
    void save(UserEntity userEntity);
    void deleteById(int id);
    void saveAuth(UserEntity user, String password) throws FirebaseAuthException;
}
