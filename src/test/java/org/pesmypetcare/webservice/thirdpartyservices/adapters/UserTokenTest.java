package org.pesmypetcare.webservice.thirdpartyservices.adapters;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class UserTokenTest {
    @Mock
    private FirebaseToken token;
    @Mock
    private UserRecord userRecord;

    @InjectMocks
    private UserToken userToken = new UserTokenImpl();

    @Test
    public void getUsername() {
        given(userRecord.getDisplayName()).willReturn("John");
        assertEquals("Should return the user's username.", "John", userToken.getUsername());
    }

    @Test
    public void getEmail() {
        given(userRecord.getEmail()).willReturn("some@email.com");
        assertEquals("Should return the user's email.", "some@email.com", userToken.getEmail());
    }

    @Test
    public void getUid() {
        given(userRecord.getUid()).willReturn("11231");
        assertEquals("Should return the user's UID.", "11231", userToken.getUid());
    }

    @Test
    public void shouldThrowFirebaseAuthExceptionWhenTheTokenIsInvalid() {
        assertThrows(BadCredentialsException.class, () -> new UserTokenImpl("invalid-token"),
            "Should throw FirebaseAuthException if the token is invalid.");
    }
}
