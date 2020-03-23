package org.pesmypetcare.webservice.service;

import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.UserDao;
import org.pesmypetcare.webservice.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService service = new UserServiceImpl();

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
    public void createUser() {
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
