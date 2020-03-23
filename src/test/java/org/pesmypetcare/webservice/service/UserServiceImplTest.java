package org.pesmypetcare.webservice.service;

import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pesmypetcare.webservice.dao.UserDao;
import org.pesmypetcare.webservice.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService service = new UserServiceImpl();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createUserAuth() throws FirebaseAuthException {
        UserEntity user = new UserEntity();
        service.createUserAuth(user, "123456");

        verify(userDao).createUserAuth(isA(UserEntity.class), isA(String.class));
    }

    @Test
    public void shouldReturnAnExceptionWhenUserDaoFails() {
        UserEntity user = new UserEntity();
        assertThrows(FirebaseAuthException.class, () -> {
            doThrow(FirebaseAuthException.class).when(userDao).createUserAuth(any(), any());
            service.createUserAuth(user, "1234");
        }, "Should return an exception when createUserAuth fails");
    }

    @Test
    public void createUser() throws FirebaseAuthException {
        UserEntity user = new UserEntity();
        service.createUser(user);
        verify(userDao).createUser(isA(UserEntity.class));
    }

    @Test
    public void deleteById() {
    }

    @Test
    public void getUserData() {
    }

    @Test
    public void updateEmail() {
    }

    @Test
    public void updatePassword() {
    }
}
