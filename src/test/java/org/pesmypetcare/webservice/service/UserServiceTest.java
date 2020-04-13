package org.pesmypetcare.webservice.service;

import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.UserDao;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final String EMAIL_FIELD = "email";
    private String username;
    private String newEmail;
    private UserEntity user;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService service = new UserServiceImpl();
    private String uid;
    private String password;

    @BeforeEach
    public void setUp() {
        uid = "uid";
        username = "user";
        password = "123456";
        newEmail = "new-user@email.com";
        user = new UserEntity(username, password, "user@email");
    }

    @Test
    public void shouldReturnNothingWhenUserAccountCreated() throws FirebaseAuthException {
        service.createUserAuth(user, password);
        verify(userDao).createUserAuth(same(user), same(password));
    }

    @Test
    public void shouldReturnNothingWhenUserCreated() throws DatabaseAccessException, FirebaseAuthException {
        service.createUser(uid, user);
        verify(userDao).createUser(same(uid), same(user));
    }

    @Test
    public void shouldReturnNothingWhenUserDeleted() throws DatabaseAccessException, FirebaseAuthException {
        service.deleteById(username);
        verify(userDao).deleteById(same(username));
    }

    @Test
    public void shouldReturnFirebaseAuthExceptionWhenDaoFails() {
        assertThrows(FirebaseAuthException.class, () -> {
            doThrow(FirebaseAuthException.class).when(userDao).deleteById(any(String.class));
            service.deleteById(username);
        }, "Should return a firebase authentication exception when a user deletion fails");
    }

    @Test
    public void shouldReturnUserEntity() throws DatabaseAccessException {
        when(userDao.getUserData(username)).thenReturn(user);
        UserEntity entity = service.getUserData(username);
        assertSame(user, entity, "Should return a user entity");
    }

    @Test
    public void shouldUpdateField() throws FirebaseAuthException, DatabaseAccessException {
        service.updateField(username, EMAIL_FIELD, newEmail);
        verify(userDao).updateField(same(username), same(EMAIL_FIELD), same(newEmail));
    }
}
