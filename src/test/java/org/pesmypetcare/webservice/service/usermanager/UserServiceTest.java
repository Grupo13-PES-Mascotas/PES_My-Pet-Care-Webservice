package org.pesmypetcare.webservice.service.usermanager;

import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.usermanager.UserDao;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final String EMAIL_FIELD = "email";
    private static final String TOKEN = "myToken";
    private String username;
    private String newEmail;
    private UserEntity user;
    private String uid;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private final UserService service = new UserServiceImpl();

    @BeforeEach
    public void setUp() {
        uid = "uid";
        username = "user";
        newEmail = "new-user@email.com";
        user = new UserEntity(username, "123456", "user@email");
    }

    @Test
    public void shouldReturnNothingWhenUserCreated() throws DatabaseAccessException, FirebaseAuthException {
        service.createUser(uid, user);
        verify(userDao).createUser(same(uid), same(user));
    }

    @Test
    public void shouldReturnNothingWhenUserDeleted()
        throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        service.deleteById(TOKEN, username);
        verify(userDao).deleteById(same(username));
    }

    @Test
    public void shouldReturnFirebaseAuthExceptionWhenDaoFails() {
        assertThrows(FirebaseAuthException.class, () -> {
            doThrow(FirebaseAuthException.class).when(userDao).deleteById(any(String.class));
            service.deleteById(TOKEN, username);
        }, "Should return a firebase authentication exception when a user deletion fails");
    }

    @Test
    public void shouldReturnUserEntity() throws DatabaseAccessException {
        when(userDao.getUserData(username)).thenReturn(user);
        UserEntity entity = service.getUserData(TOKEN, username);
        assertSame(user, entity, "Should return a user entity");
    }

    @Test
    public void shouldUpdateField() throws FirebaseAuthException, DatabaseAccessException {
        service.updateField(TOKEN, username, EMAIL_FIELD, newEmail);
        verify(userDao).updateField(same(username), same(EMAIL_FIELD), same(newEmail));
    }
}
