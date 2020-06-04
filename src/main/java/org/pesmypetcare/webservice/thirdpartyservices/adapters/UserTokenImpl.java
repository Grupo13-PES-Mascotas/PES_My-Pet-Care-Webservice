package org.pesmypetcare.webservice.thirdpartyservices.adapters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.pesmypetcare.webservice.thirdpartyservices.FirebaseFactory;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * @author Santiago Del Rey
 */
public class UserTokenImpl implements UserToken {
    private FirebaseAuth auth;
    private FirebaseToken token;
    private UserRecord userRecord;

    public UserTokenImpl() {
        auth = FirebaseFactory.getInstance().getFirebaseAuth();
    }

    /**
     * Creates a UserToken instance with the given token.
     * @param token The token to use
     * @throws BadCredentialsException If an error occurs while parsing or validating the token.
     */
    public UserTokenImpl(String token) {
        auth = FirebaseFactory.getInstance().getFirebaseAuth();
        try {
            this.token = auth.verifyIdToken(token);
            this.userRecord = auth.getUser(this.token.getUid());
        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException("The token provided is not correct.");
        }
    }

    public void setToken(String token) throws FirebaseAuthException {
        this.token = auth.verifyIdToken(token);
    }

    @Override
    public String getUsername() {
        return userRecord.getDisplayName();
    }

    @Override
    public String getEmail() {
        return userRecord.getEmail();
    }

    @Override
    public String getUid() {
        return userRecord.getUid();
    }
}
