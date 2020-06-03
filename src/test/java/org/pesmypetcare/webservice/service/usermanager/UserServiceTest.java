package org.pesmypetcare.webservice.service.usermanager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
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
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
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
    private String token;
    private String fcmToken;

    @Mock
    private UserDao userDao;
    @Mock
    private UserToken userToken;
    @Mock
    private FirebaseAuth auth;

    @InjectMocks
    private final UserService service = new UserServiceImpl();

    @BeforeEach
    public void setUp() {
        token = "some-token";
        username = "user";
        newEmail = "new-user@email.com";
        user = new UserEntity(username, "123456", "user@email");
        fcmToken = "fmcToken";
    }

    @Test
    public void createUser() throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        willDoNothing().given(userDao).createUser(any(UserToken.class), any(UserEntity.class));

        service.createUser(token, user);
        verify(userDao).createUser(same(userToken), same(user));
    }

    @Test
    public void deleteById() throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        willDoNothing().given(userDao).deleteById(any(UserToken.class));

        service.deleteById(TOKEN);
        verify(userDao).deleteById(same(userToken));
    }

    @Test
    public void deleteFromDatabase() throws DatabaseAccessException, DocumentException {
        willDoNothing().given(userDao).deleteFromDatabase(any(UserToken.class));

        service.deleteFromDatabase(TOKEN);
        verify(userDao).deleteFromDatabase(same(userToken));
    }

    @Test
    public void getUserData() throws DatabaseAccessException {
        given(userDao.getUserData(eq(userToken))).willReturn(user);

        UserEntity entity = service.getUserData(TOKEN);
        assertSame(user, entity, "Should return a user entity");
    }

    @Test
    public void updateField() throws FirebaseAuthException, DatabaseAccessException {
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
    public void saveMessagingToken() throws FirebaseAuthException, DatabaseAccessException, DocumentException {
        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        given(auth.verifyIdToken(anyString())).willReturn(firebaseToken);
        given(firebaseToken.getUid()).willReturn(token);
        willDoNothing().given(userDao).saveMessagingToken(any(UserToken.class), anyString());

        service.saveMessagingToken(TOKEN, fcmToken);
        verify(auth).verifyIdToken(same(TOKEN));
        verify(firebaseToken).getUid();
        verify(userDao).saveMessagingToken(same(userToken), same(fcmToken));
    }

    @Test
    public void saveMessagingTokenShouldThrowBadCredentialExceptionWhenTheTokenIsNotValid() {
        assertThrows(BadCredentialsException.class, () -> {
            willThrow(FirebaseAuthException.class).given(auth).verifyIdToken(same(TOKEN));
            service.saveMessagingToken(TOKEN, fcmToken);
        }, "Should throw BadCredentialsException when the authentication is invalid.");
    }

    @Test
    public void getUserSubscriptions() throws DatabaseAccessException {
        List<String> subscriptions = new ArrayList<>();
        subscriptions.add("Dogs");
        given(userDao.getUserSubscriptions(any(UserToken.class))).willReturn(subscriptions);

        List<String> result = service.getUserSubscriptions(TOKEN);
        assertEquals(subscriptions, result, "Should return all the user subscriptions to groups.");
    }
}
