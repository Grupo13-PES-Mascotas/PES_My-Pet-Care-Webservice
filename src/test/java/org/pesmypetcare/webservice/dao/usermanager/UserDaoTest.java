package org.pesmypetcare.webservice.dao.usermanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.dao.petmanager.PetDaoImpl;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class UserDaoTest {
    private static final String USERNAME_FIELD = "username";
    private static final String EMAIL_FIELD = "email";
    private static final String PASSWORD_FIELD = "password";
    private static final String USER_FIELD = "user";
    private static UserEntity userEntity;
    private static String uid;
    private static String username;
    private static String newUsername;
    private static String password;
    private static String email;
    private static Map<String, String> docData;

    @Mock
    private FirebaseAuth myAuth;
    @Mock
    private CollectionReference users;
    @Mock
    private CollectionReference usedUsernames;
    @Mock
    private CollectionReference groups;
    @Mock
    private DocumentReference userRef;
    @Mock
    private DocumentReference usernameRef;
    @Mock
    private DocumentReference newUsernameRef;
    @Mock
    private DocumentReference groupRef;
    @Mock
    private PetDaoImpl petDao;
    @Mock
    private StorageDao storageDao;
    @Mock
    private ApiFuture<DocumentSnapshot> future;
    @Mock
    private ApiFuture<DocumentSnapshot> newFuture;
    @Mock
    private ApiFuture<QuerySnapshot> queryFuture;
    @Mock
    private DocumentSnapshot snapshot;
    @Mock
    private DocumentSnapshot oldSnapshot;
    @Mock
    private QuerySnapshot querySnapshot;
    @Mock
    private UserRecord userRecord;
    @Mock
    private UserRecord.UpdateRequest updateRequest;
    @Mock
    private Firestore db;
    @Mock
    private Query query;
    @Mock
    private List<QueryDocumentSnapshot> docList;

    @InjectMocks
    private final UserDao dao = new UserDaoImpl();

    @BeforeAll
    public static void setUp() {
        password = "123456";
        uid = "123eA2eA4";
        username = "John";
        newUsername = "Michael";
        email = "user@email.com";
        userEntity = new UserEntity(username, password, email);
        docData = new HashMap<>();
        docData.put(USER_FIELD, uid);
    }

    @Test
    public void shouldCreateUserOnDatabase() throws DatabaseAccessException, FirebaseAuthException,
        ExecutionException, InterruptedException {
        given(usedUsernames.document(anyString())).willReturn(usernameRef);
        given(users.document(anyString())).willReturn(userRef);
        given(usernameRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(false);
        given(usernameRef.set(docData)).willReturn(null);
        given(userRef.set(any(UserEntity.class))).willReturn(null);
        given(myAuth.getUser(uid)).willReturn(userRecord);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setDisplayName(anyString())).willReturn(updateRequest);

        dao.createUser(uid, userEntity);
        verify(usedUsernames, times(2)).document(same(username));
        verify(usernameRef).set(docData);
        verify(users).document(same(uid));
        verify(userRef).set(same(userEntity));
        verify(myAuth).getUser(same(uid));
        verify(updateRequest).setDisplayName(same(username));
    }

    @Test
    public void shouldDeleteUser() throws DatabaseAccessException, FirebaseAuthException,
        ExecutionException, InterruptedException {
        given(petDao.getStorageDao()).willReturn(storageDao);
        willDoNothing().given(storageDao).deleteImageByName(anyString());
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(true);
        given(snapshot.get(USERNAME_FIELD)).willReturn(username);
        given(usedUsernames.document(anyString())).willReturn(usernameRef);
        given(usernameRef.delete()).willReturn(null);
        dao.deleteById(username);
        verify(petDao).deleteAllPets(same(username));
        verify(userRef).delete();
        verify(usernameRef).delete();
        verify(myAuth).deleteUser(same(username));
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseOfNonExistentUser()
        throws ExecutionException, InterruptedException {
            given(petDao.getStorageDao()).willReturn(storageDao);
            willDoNothing().given(storageDao).deleteImageByName(anyString());
            given(users.document(anyString())).willReturn(userRef);
            given(userRef.get()).willReturn(future);
            given(future.get()).willReturn(snapshot);
            given(snapshot.exists()).willReturn(false);
        assertThrows(DatabaseAccessException.class, () -> dao.deleteById(username),
            "Should throw DatabaseAccessException when the deleting a non existent user");
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
        given(snapshot.exists()).willReturn(true);
        given(snapshot.get(USERNAME_FIELD)).willReturn(username);
        given(usedUsernames.document(anyString())).willReturn(usernameRef);
        given(usernameRef.delete()).willReturn(null);
        assertThrows(FirebaseAuthException.class, () -> {
            willThrow(FirebaseAuthException.class).given(myAuth).deleteUser(anyString());
            dao.deleteById(username);
        }, "Should throw FirebaseAuthException when the deletion from Firebase authentication fails");
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
    public void shouldUpdateUsername() throws FirebaseAuthException, DatabaseAccessException,
        ExecutionException, InterruptedException {
        given(usedUsernames.document(username)).willReturn(usernameRef);
        given(usernameRef.get()).willReturn(future);
        given(future.get()).willReturn(oldSnapshot);
        given(oldSnapshot.exists()).willReturn(true);
        given(oldSnapshot.get(anyString())).willReturn(uid);
        given(usedUsernames.document(newUsername)).willReturn(newUsernameRef);
        given(newUsernameRef.get()).willReturn(newFuture);
        given(newFuture.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(false);
        given(myAuth.getUser(anyString())).willReturn(userRecord);
        given(userRecord.getDisplayName()).willReturn(username);
        given(usernameRef.delete()).willReturn(null);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setDisplayName(anyString())).willReturn(updateRequest);
        given(myAuth.updateUserAsync(any(UserRecord.UpdateRequest.class))).willReturn(null);
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.update(anyString(), anyString())).willReturn(null);
        given(db.collectionGroup(anyString())).willReturn(query);
        given(db.collection(anyString())).willReturn(groups);
        given(query.whereEqualTo(anyString(), isA(String.class))).willReturn(query);
        given(groups.whereEqualTo(anyString(), isA(String.class))).willReturn(query);
        given(query.get()).willReturn(queryFuture);
        given(queryFuture.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(docList);
        Iterator mockIterator = mock(Iterator.class);
        given(docList.iterator()).willReturn(mockIterator);
        given(mockIterator.hasNext()).willReturn(true, false);
        given(mockIterator.next()).willReturn(snapshot);
        given(snapshot.getReference()).willReturn(groupRef);
        given(groupRef.update(anyString(), anyString())).willReturn(null);


        dao.updateField(username, USERNAME_FIELD, newUsername);
        verify(usedUsernames, times(2)).document(same(username));
        verify(oldSnapshot).get(same(USER_FIELD));
        verify(newUsernameRef).set(docData);
        verify(myAuth, times(3)).getUser(same(uid));
        verify(userRecord, times(2)).getDisplayName();
        verify(usedUsernames, times(2)).document(same(newUsername));
        verify(usernameRef).delete();
        verify(userRecord).updateRequest();
        verify(updateRequest).setDisplayName(same(newUsername));
        verify(myAuth).updateUserAsync(same(updateRequest));
        verify(users).document(same(uid));
        verify(userRef).update(same(USERNAME_FIELD), same(newUsername));
        verify(groupRef).update("user", newUsername);
    }

    @Test
    public void shouldUpdateEmail() throws FirebaseAuthException, DatabaseAccessException,
        ExecutionException, InterruptedException {
        given(usedUsernames.document(username)).willReturn(usernameRef);
        given(usernameRef.get()).willReturn(future);
        given(future.get()).willReturn(oldSnapshot);
        given(oldSnapshot.exists()).willReturn(true);
        given(oldSnapshot.get(anyString())).willReturn(uid);
        given(myAuth.getUser(anyString())).willReturn(userRecord);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setEmail(anyString())).willReturn(updateRequest);
        given(myAuth.updateUserAsync(any(UserRecord.UpdateRequest.class))).willReturn(null);
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.update(anyString(), anyString())).willReturn(null);

        dao.updateField(username, EMAIL_FIELD, email);
        verify(usedUsernames).document(same(username));
        verify(oldSnapshot).get(same(USER_FIELD));
        verify(updateRequest).setEmail(same(email));
        verify(myAuth).updateUserAsync(same(updateRequest));
        verify(users).document(same(uid));
        verify(userRef).update(same(EMAIL_FIELD), same(email));
    }

    @Test
    public void shouldUpdatePassword() throws FirebaseAuthException, DatabaseAccessException,
        ExecutionException, InterruptedException {
        given(usedUsernames.document(username)).willReturn(usernameRef);
        given(usernameRef.get()).willReturn(future);
        given(future.get()).willReturn(oldSnapshot);
        given(oldSnapshot.exists()).willReturn(true);
        given(oldSnapshot.get(anyString())).willReturn(uid);
        given(myAuth.getUser(anyString())).willReturn(userRecord);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setPassword(anyString())).willReturn(updateRequest);
        given(myAuth.updateUserAsync(any(UserRecord.UpdateRequest.class))).willReturn(null);
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.update(anyString(), anyString())).willReturn(null);

        dao.updateField(username, PASSWORD_FIELD, password);
        verify(usedUsernames).document(same(username));
        verify(oldSnapshot).get(same(USER_FIELD));
        verify(updateRequest).setPassword(same(password));
        verify(myAuth).updateUserAsync(same(updateRequest));
        verify(users).document(same(uid));
        verify(userRef).update(same(PASSWORD_FIELD), same(password));
    }

    @Test
    public void shouldFailIfAnErrorOccursWhileRetrievingUserDataWhenUpdatingField()
        throws ExecutionException, InterruptedException {
        given(usedUsernames.document(anyString())).willReturn(usernameRef);
        given(usernameRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(true);
        given(snapshot.get(anyString())).willReturn(uid);
        assertThrows(FirebaseAuthException.class, () -> {
            willThrow(FirebaseAuthException.class).given(myAuth).getUser(uid);
            dao.updateField(username, EMAIL_FIELD, email);
        }, "Should return FirebaseAuthException if an error occurs while retrieving user data");
    }

    @Test
    public void shouldFailWhenUpdatingFieldIfTheUserDoesNotExist() throws ExecutionException, InterruptedException {
        given(usedUsernames.document(anyString())).willReturn(usernameRef);
        given(usernameRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(false);
        assertThrows(DatabaseAccessException.class, () -> dao.updateField(username, EMAIL_FIELD, email),
            "Should return DatabaseAccessException if the user does not exist");
    }
}
