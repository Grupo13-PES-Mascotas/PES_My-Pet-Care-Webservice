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

import java.util.HashMap;
import java.util.Map;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserDaoTest {
    private static final String EMAIL_FIELD = "email";
    private static final String PASSWORD_FIELD = "password";
    private static UserEntity userEntity;
    private static String username;
    private static String password;
    private static String email;

    @Mock
    private FirebaseAuth myAuth;
    @Mock
    private CollectionReference users;
    @Mock
    private CollectionReference used_usernames;
    @Mock
    private DocumentReference userRef;
    @Mock
    private DocumentReference usernameRef;
    @Mock
    private PetDaoImpl petDao;
    @Mock
    private StorageDao storageDao;
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
        userEntity = new UserEntity(username, password, email);
    }

    @Test
    public void shouldCreateUserAccount() throws FirebaseAuthException {
        dao.createUserAuth(userEntity, password);
        verify(myAuth).createUser(isA(UserRecord.CreateRequest.class));
    }

    @Test
    public void shouldThrowFirebaseAuthExceptionOnFail() {
        assertThrows(FirebaseAuthException.class, () -> {
            willThrow(FirebaseAuthException.class).given(myAuth).createUser(any(UserRecord.CreateRequest.class));
            dao.createUserAuth(userEntity, password);
        }, "Should throw FirebaseAuthException when the creation fails");
    }

    @Test
    public void shouldCreateUserOnDatabase() throws DatabaseAccessException, ExecutionException, InterruptedException {
        Map<String, Boolean> docData = new HashMap<>();
        docData.put("exists", true);
        given(used_usernames.document(anyString())).willReturn(usernameRef);
        given(users.document(anyString())).willReturn(userRef);
        given(usernameRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(false);
        given(usernameRef.set(docData)).willReturn(null);
        given(userRef.set(any(UserEntity.class))).willReturn(null);
        dao.createUser("uid", userEntity);
        verify(used_usernames, times(2)).document(same(username));
        verify(usernameRef).set(docData);
        verify(users).document(same("uid"));
        verify(userRef).set(same(userEntity));
    }

    @Test
    public void shouldDeleteUser() throws DatabaseAccessException, FirebaseAuthException, ExecutionException, InterruptedException {
        given(petDao.getStorageDao()).willReturn(storageDao);
        willDoNothing().given(storageDao).deleteImageByName(anyString());
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.get("username")).willReturn(username);
        given(used_usernames.document(anyString())).willReturn(usernameRef);
        given(usernameRef.delete()).willReturn(null);
        dao.deleteById(username);
        verify(petDao).deleteAllPets(same(username));
        verify(userRef).delete();
        verify(usernameRef).delete();
        verify(myAuth).deleteUser(same(username));
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseFails() {
        assertThrows(DatabaseAccessException.class, () -> {
            willThrow(DatabaseAccessException.class).given(petDao).deleteAllPets(anyString());
            dao.deleteById(username);
        }, "Should throw DatabaseAccessException when the deletion from database fails");
    }

    @Test
    public void shouldThrowFirebaseAuthExceptionWhenDeleteFromDatabaseFails()
        throws ExecutionException, InterruptedException {
        given(petDao.getStorageDao()).willReturn(storageDao);
        willDoNothing().given(storageDao).deleteImageByName(anyString());
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.get("username")).willReturn(username);
        given(used_usernames.document(anyString())).willReturn(usernameRef);
        given(usernameRef.delete()).willReturn(null);
        assertThrows(FirebaseAuthException.class, () -> {
            willThrow(FirebaseAuthException.class).given(myAuth).deleteUser(anyString());
            dao.deleteById(username);
        }, "Should throw DatabaseAccessException when the deletion from Firebase authentication fails");
    }

    @Test
    public void shouldReturnUserEntity() throws ExecutionException, InterruptedException, DatabaseAccessException {
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(true);
        given(snapshot.toObject(UserEntity.class)).willReturn(userEntity);

        UserEntity actual = dao.getUserData(username);
        assertSame(userEntity, actual, "Should return a user entity");
    }

    @Test
    public void shouldFailIfUserDoesNotExist() throws ExecutionException, InterruptedException {
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(false);
        assertThrows(DatabaseAccessException.class, () -> dao.getUserData(username),
            "Should return DatabaseAccessException if user doesn't exist");
    }

    @Test
    public void shouldUpdateEmail() throws FirebaseAuthException, DatabaseAccessException {
        given(myAuth.getUser(anyString())).willReturn(userRecord);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setEmail(anyString())).willReturn(updateRequest);
        given(myAuth.updateUserAsync(any(UserRecord.UpdateRequest.class))).willReturn(null);
        given(users.document(username)).willReturn(userRef);
        given(userRef.update(EMAIL_FIELD, email)).willReturn(null);
        dao.updateField(username, EMAIL_FIELD, email);

        verify(updateRequest).setEmail(same(email));
        verify(myAuth).updateUserAsync(same(updateRequest));
        verify(users).document(same(username));
        verify(userRef).update(same(EMAIL_FIELD), same(email));
    }

    @Test
    public void shouldFailIfAnErrorOccursWhileRetrievingUserDataWhenUpdatingEmail() {
        assertThrows(FirebaseAuthException.class, () -> {
            willThrow(FirebaseAuthException.class).given(myAuth).getUser(username);
            dao.updateField(username, EMAIL_FIELD, email);
        }, "Should return FirebaseAuthException if an error occurs while retrieving user data");
    }

    @Test
    public void shouldFailIfAnErrorOccursWhenUserIdIsNullWhenUpdatingEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            willThrow(IllegalArgumentException.class).given(myAuth).getUser(isNull());
            dao.updateField(null, EMAIL_FIELD, email);
        }, "Should return IllegalArgumentException if the user id is null");
    }

    @Test
    public void shouldFailIfAnErrorOccursWhenUserIdIsEmptyWhenUpdatingEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            willThrow(IllegalArgumentException.class).given(myAuth).getUser(matches("^$"));
            dao.updateField("", EMAIL_FIELD, email);
        }, "Should return IllegalArgumentException if the user id is empty");
    }

    @Test
    public void shouldUpdatePassword() throws FirebaseAuthException, DatabaseAccessException {
        given(myAuth.getUser(anyString())).willReturn(userRecord);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setPassword(anyString())).willReturn(updateRequest);
        given(myAuth.updateUserAsync(any(UserRecord.UpdateRequest.class))).willReturn(null);

        dao.updateField(username, PASSWORD_FIELD, password);
        verify(updateRequest).setPassword(same(password));
        verify(myAuth).updateUserAsync(same(updateRequest));
    }

    @Test
    public void shouldFailIfAnErrorOccurredWhileRetrievingTheDataWhenUpdatingPassword() {
        assertThrows(FirebaseAuthException.class, () -> {
            willThrow(FirebaseAuthException.class).given(myAuth).getUser(username);
            dao.updateField(username, PASSWORD_FIELD, password);
        }, "Should return FirebaseAuthException if an error occurs while retrieving user data");
    }

    @Test
    public void shouldFailIfAnErrorOccursWhenUserIdIsNullWhenUpdatingPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            willThrow(IllegalArgumentException.class).given(myAuth).getUser(isNull());
            dao.updateField(null, PASSWORD_FIELD, password);
        }, "Should return IllegalArgumentException if the user id is null");
    }

    @Test
    public void shouldFailIfAnErrorOccursWhenUserIdIsEmptyWhenUpdatingPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            willThrow(IllegalArgumentException.class).given(myAuth).getUser(matches("^$"));
            dao.updateField("", PASSWORD_FIELD, password);
        }, "Should return IllegalArgumentException if the user id is empty");
    }
}
