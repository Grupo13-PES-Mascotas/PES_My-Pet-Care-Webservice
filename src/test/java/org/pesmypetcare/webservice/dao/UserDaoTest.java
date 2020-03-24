package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserDaoTest {
    private static UserEntity userEntity;
    private static String username;
    private static String password;
    private static String email;
    private static final String EMAIL_FIELD = "email";

    @Mock
    private FirebaseAuth auth;
    @Mock
    private CollectionReference usersRef;
    @Mock
    private DocumentReference docRef;
    @Mock
    private PetDao petDao;
    @Mock
    private ApiFuture<DocumentSnapshot> future;
    @Mock
    private DocumentSnapshot snapshot;
    @Mock
    private UserRecord userRecord;
    @Mock
    private UserRecord.UpdateRequest updateRequest;

    @InjectMocks
    private UserDao dao = new UserDaoImpl();

    @BeforeAll
    public static void setUp() {
        password = "123456";
        username = "username";
        email = "user@email.com";
        userEntity = new UserEntity(username, email);
    }

    @Test
    public void shouldCreateUserAccount() throws FirebaseAuthException {
        dao.createUserAuth(userEntity, password);
        verify(auth).createUser(isA(UserRecord.CreateRequest.class));
    }

    @Test
    public void shouldThrowFirebaseAuthExceptionOnFail() {
        assertThrows(FirebaseAuthException.class, () -> {
            willThrow(FirebaseAuthException.class).given(auth).createUser(any(UserRecord.CreateRequest.class));
            dao.createUserAuth(userEntity, password);
        }, "Should throw FirebaseAuthException when the creation fails");
    }

    @Test
    public void shouldCreateUserOnDatabase() {
        given(usersRef.document(anyString())).willReturn(docRef);
        given(docRef.set(any(UserEntity.class))).willReturn(null);
        dao.createUser(userEntity);
        verify(usersRef).document(same(userEntity.getUsername()));
        verify(docRef).set(same(userEntity));
    }

    @Test
    public void shouldDeleteUserOnDatabase() throws DatabaseAccessException, FirebaseAuthException {
        given(usersRef.document(anyString())).willReturn(docRef);
        dao.deleteById(username);
        verify(petDao).deleteAllPets(same(username));
        verify(docRef).delete();
        verify(auth).deleteUser(same(username));
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            willThrow(DatabaseAccessException.class).given(petDao).deleteAllPets(anyString());
            dao.deleteById(username);
        }, "Should throw DatabaseAccessException when the deletion from database fails");
    }

    @Test
    public void shouldThrowFirebaseAuthExceptionWhenDeleteFromDatabaseFails() {
        given(usersRef.document(anyString())).willReturn(docRef);
        assertThrows(FirebaseAuthException.class, () -> {
            willThrow(FirebaseAuthException.class).given(auth).deleteUser(anyString());
            dao.deleteById(username);
        }, "Should throw DatabaseAccessException when the deletion from Firebase authentication fails");
    }

    @Test
    public void shouldReturnUserEntity() throws ExecutionException, InterruptedException, DatabaseAccessException {
        given(usersRef.document(anyString())).willReturn(docRef);
        given(docRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(true);
        given(snapshot.toObject(UserEntity.class)).willReturn(userEntity);

        UserEntity actual = dao.getUserData(username);
        assertSame(userEntity, actual, "Should return a user entity");
    }

    @Test
    public void shouldFailIfUserDoesNotExist() throws ExecutionException, InterruptedException {
        given(usersRef.document(anyString())).willReturn(docRef);
        given(docRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(false);
        assertThrows(DatabaseAccessException.class, () -> dao.getUserData(username),
            "Should return DatabaseAccessException if user doesn't exist");
    }

    @Test
    public void shouldUpdateEmail() throws FirebaseAuthException {
        given(auth.getUser(anyString())).willReturn(userRecord);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setEmail(anyString())).willReturn(updateRequest);
        given(auth.updateUserAsync(any(UserRecord.UpdateRequest.class))).willReturn(null);
        given(usersRef.document(username)).willReturn(docRef);
        given(docRef.update(EMAIL_FIELD, email)).willReturn(null);
        dao.updateEmail(username, email);

        verify(updateRequest).setEmail(same(email));
        verify(auth).updateUserAsync(same(updateRequest));
        verify(usersRef).document(same(username));
        verify(docRef).update(same(EMAIL_FIELD), same(email));
    }

    @Test
    public void shouldFailIfAnErrorOccursWhileRetrievingUserDataWhenUpdatingEmail() {
        assertThrows(FirebaseAuthException.class, () -> {
            willThrow(FirebaseAuthException.class).given(auth).getUser(username);
            dao.updateEmail(username, email);
        }, "Should return FirebaseAuthException if an error occurs while retrieving user data");
    }

    @Test
    public void shouldFailIfAnErrorOccursWhenUserIdIsNullWhenUpdatingEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            willThrow(IllegalArgumentException.class).given(auth).getUser(isNull());
            dao.updateEmail(null, email);
        }, "Should return IllegalArgumentException if the user id is null");
    }

    @Test
    public void shouldFailIfAnErrorOccursWhenUserIdIsEmptyWhenUpdatingEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            willThrow(IllegalArgumentException.class).given(auth).getUser(matches("^$"));
            dao.updateEmail("", email);
        }, "Should return IllegalArgumentException if the user id is empty");
    }

    @Test
    public void shouldUpdatePassword() throws FirebaseAuthException {
        given(auth.getUser(anyString())).willReturn(userRecord);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setPassword(anyString())).willReturn(updateRequest);
        given(auth.updateUserAsync(any(UserRecord.UpdateRequest.class))).willReturn(null);

        dao.updatePassword(username, password);
        verify(updateRequest).setPassword(same(password));
        verify(auth).updateUserAsync(same(updateRequest));
    }

    @Test
    public void shouldFailIfAnErrorOccurredWhileRetrievingTheDataWhenUpdatingPassword() {
        assertThrows(FirebaseAuthException.class, () -> {
            willThrow(FirebaseAuthException.class).given(auth).getUser(username);
            dao.updatePassword(username, password);
        }, "Should return FirebaseAuthException if an error occurs while retrieving user data");
    }

    @Test
    public void shouldFailIfAnErrorOccursWhenUserIdIsNullWhenUpdatingPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            willThrow(IllegalArgumentException.class).given(auth).getUser(isNull());
            dao.updatePassword(null, password);
        }, "Should return IllegalArgumentException if the user id is null");
    }

    @Test
    public void shouldFailIfAnErrorOccursWhenUserIdIsEmptyWhenUpdatingPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            willThrow(IllegalArgumentException.class).given(auth).getUser(matches("^$"));
            dao.updatePassword("", password);
        }, "Should return IllegalArgumentException if the user id is empty");
    }
}
