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
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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
    private String fcmToken;

    @Mock
    private UserDao userDao;
    @Mock
    private UserToken userToken;

    @InjectMocks
    private final UserService service = spy(new UserServiceImpl());

    @BeforeEach
    public void setUp() {
        username = "user";
        newEmail = "new-user@email.com";
        user = new UserEntity(username, "123456", "user@email");
        fcmToken = "fmcToken";
    }

    @Test
    public void createUser() throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        willDoNothing().given(userDao).createUser(anyString(), any(UserEntity.class));

        String uid = "some-uid";
        service.createUser(uid, user);
        verify(userDao).createUser(same(uid), same(user));
    }

    @Test
    public void deleteById() throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        doReturn(userToken).when((UserServiceImpl) service).makeUserToken(anyString());
        willDoNothing().given(userDao).deleteById(any(UserToken.class));

        service.deleteById(TOKEN);
        verify(userDao).deleteById(same(userToken));
    }

    @Test
    public void deleteFromDatabase() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((UserServiceImpl) service).makeUserToken(anyString());
        willDoNothing().given(userDao).deleteFromDatabase(any(UserToken.class));

        service.deleteFromDatabase(TOKEN);
        verify(userDao).deleteFromDatabase(same(userToken));
    }

    @Test
    public void getUserData() throws DatabaseAccessException {
        doReturn(userToken).when((UserServiceImpl) service).makeUserToken(anyString());
        given(userDao.getUserData(eq(userToken))).willReturn(user);

        UserEntity entity = service.getUserData(TOKEN);
        assertSame(user, entity, "Should return a user entity");
    }

    @Test
    public void updateField() throws FirebaseAuthException, DatabaseAccessException {
        doReturn(userToken).when((UserServiceImpl) service).makeUserToken(anyString());
        willDoNothing().given(userDao).updateField(any(UserToken.class), anyString(), anyString());
        service.updateField(TOKEN, EMAIL_FIELD, newEmail);
        verify(userDao).updateField(same(userToken), same(EMAIL_FIELD), same(newEmail));
    }

    @Test
    public void existsUsername() throws DatabaseAccessException {
        given(userDao.existsUsername(username)).willReturn(true);

        assertTrue(service.existsUsername(username), "Should return true if the username is already in use.");
    }

    @Test
    public void saveMessagingToken() throws DatabaseAccessException, DocumentException {
        doReturn(userToken).when((UserServiceImpl) service).makeUserToken(anyString());
        willDoNothing().given(userDao).saveMessagingToken(any(UserToken.class), anyString());

        service.saveMessagingToken(TOKEN, fcmToken);
        verify(userDao).saveMessagingToken(same(userToken), same(fcmToken));
    }

    @Test
    public void saveMessagingTokenShouldThrowBadCredentialExceptionWhenTheTokenIsNotValid() {
        assertThrows(BadCredentialsException.class, () -> service.saveMessagingToken(TOKEN, fcmToken),
            "Should throw BadCredentialsException when the authentication is invalid.");
    }

    @Test
    public void getUserSubscriptions() throws DatabaseAccessException {
        doReturn(userToken).when((UserServiceImpl) service).makeUserToken(anyString());
        List<String> subscriptions = new ArrayList<>();
        subscriptions.add("Dogs");
        given(userDao.getUserSubscriptions(any(UserToken.class))).willReturn(subscriptions);

        List<String> result = service.getUserSubscriptions(TOKEN);
        assertEquals(subscriptions, result, "Should return all the user subscriptions to groups.");
    }
}
