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
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService service = new UserServiceImpl();
    private UserEntity user;
    private String username;
    private String newEmail;

    @BeforeEach
    void setUp() {
        user = new UserEntity(username, "user@email");
        username = "user";
        newEmail = "new-user@email.com";
    }

    @Test
    public void shouldReturnNothingWhenUserAccountCreated() throws FirebaseAuthException {
        service.createUserAuth(user, "123456");
        verify(userDao).createUserAuth(isA(UserEntity.class), isA(String.class));
    }

    @Test
    public void shouldReturnAnExceptionWhenUserDaoFails() {
        assertThrows(FirebaseAuthException.class, () -> {
            doThrow(FirebaseAuthException.class).when(userDao).createUserAuth(any(), any());
            service.createUserAuth(user, "1234");
        }, "Should return an exception when a user creation fails");
    }

    @Test
    public void shouldReturnNothingWhenUserCreated() {
        service.createUser(user);
        verify(userDao).createUser(isA(UserEntity.class));
    }

    @Test
    public void shouldReturnNothingWhenUserDeleted() throws DatabaseAccessException, FirebaseAuthException {
        service.deleteById(username);
        verify(userDao).deleteById(any(String.class));
    }

    @Test
    public void shouldReturnFirebaseAuthExceptionWhenDaoFails() {
        assertThrows(FirebaseAuthException.class, () -> {
            doThrow(FirebaseAuthException.class).when(userDao).deleteById(any(String.class));
            service.deleteById(username);
        }, "Should return a firebase authentication exception when a user deletion fails");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenDaoFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(userDao).deleteById(any(String.class));
            service.deleteById(username);
        }, "Should return a database access exception when a user deletion fails");
    }

    @Test
    public void shouldReturnUserEntity() throws DatabaseAccessException {
        when(userDao.getUserData(username)).thenReturn(user);
        UserEntity entity = service.getUserData(username);
        assertSame(user, entity, "Should return a user entity");
    }

    @Test
    public void shouldReturnDatabaseAccessExceptionWhenRequestFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            doThrow(DatabaseAccessException.class).when(userDao).getUserData(any(String.class));
            service.getUserData(username);
        }, "Should return an exception when retrieving a user fails");
    }

    @Test
    public void updateEmail() throws FirebaseAuthException {
        service.updateEmail(username, newEmail);
        verify(userDao).updateEmail(any(String.class), any(String.class));
    }

    @Test
    public void shouldReturnFirebaseAuthExceptionWhenUpdateEmailFails() {
        assertThrows(FirebaseAuthException.class, () -> {
            doThrow(FirebaseAuthException.class).when(userDao).updateEmail(any(String.class), any(String.class));
            service.updateEmail(username, newEmail);
        }, "Should return an exception when email update fails");
    }

    @Test
    public void updatePassword() throws FirebaseAuthException {
        service.updatePassword(username, "newPassword");
        verify(userDao).updatePassword(any(String.class), any(String.class));
    }

    @Test
    public void shouldReturnFirebaseAuthExceptionWhenUpdatePasswordFails() {
        assertThrows(FirebaseAuthException.class, () -> {
            doThrow(FirebaseAuthException.class).when(userDao).updatePassword(any(String.class), any(String.class));
            service.updatePassword(username, "newPassword");
        }, "Should return an exception when password update fails");
    }
}
