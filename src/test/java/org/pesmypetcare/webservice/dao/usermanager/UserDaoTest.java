package org.pesmypetcare.webservice.dao.usermanager;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
import org.pesmypetcare.webservice.dao.appmanager.StorageDao;
import org.pesmypetcare.webservice.dao.petmanager.PetDaoImpl;
import org.pesmypetcare.webservice.entity.usermanager.UserEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class UserDaoTest {
    private final String USERNAME_FIELD = "username";
    private final String EMAIL_FIELD = "email";
    private final String PASSWORD_FIELD = "password";
    private final String USER_FIELD = "user";
    private UserEntity userEntity;
    private String uid;
    private String username;
    private String newUsername;
    private String password;
    private String email;
    private Map<String, String> docData;
    private List<QueryDocumentSnapshot> docList;


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
    private WriteBatch batch;
    @Mock
    private ApiFuture<List<WriteResult>> batchResult;
    @Mock
    private FirestoreDocument documentAdapter;
    @Mock
    private FirestoreCollection collectionAdapter;
    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshot;
    @Mock
    private UserToken userToken;

    @InjectMocks
    private final UserDao dao = new UserDaoImpl();
    private String encodedPassword;

    @BeforeEach
    public void setUp() {
        password = "123456";
        uid = "123eA2eA4";
        username = "John";
        newUsername = "Michael";
        email = "user@email.com";
        encodedPassword = new BCryptPasswordEncoder().encode(password);
        userEntity = new UserEntity(username, password, email);
        docData = new HashMap<>();
        docData.put(USER_FIELD, uid);
        docList = new ArrayList<>();
        docList.add(queryDocumentSnapshot);
    }

    @Test
    public void shouldCreateUserOnDatabase() throws DatabaseAccessException, FirebaseAuthException, DocumentException {
        given(userToken.getUid()).willReturn(uid);
        given(documentAdapter.documentExists(eq(Path.ofDocument(Collections.used_usernames, username))))
            .willReturn(false);
        given(documentAdapter.batch()).willReturn(batch);
        mockSaveUsername();
        lenient().when(documentAdapter
            .createDocumentWithId(eq(Path.ofCollection(Collections.users)), eq(uid), same(userEntity), same(batch)))
            .thenReturn(userRef);
        willDoNothing().given(documentAdapter).commitBatch(same(batch));
        mockUpdateDisplayName();

        dao.createUser(userToken, userEntity);
        verifySaveUsername();
        userEntity.setPassword(encodedPassword);
        verify(updateRequest).setDisplayName(same(username));
    }

    private void mockSaveUsername() {
        given(documentAdapter
            .createDocumentWithId(eq(Path.ofCollection(Collections.used_usernames)), eq(username), eq(docData),
                same(batch))).willReturn(userRef);
    }

    private void verifySaveUsername() {
        Map<String, String> docData = new HashMap<>();
        docData.put(USER_FIELD, uid);
        verify(documentAdapter)
            .createDocumentWithId(eq(Path.ofCollection(Collections.used_usernames)), eq(username), eq(docData),
                same(batch));
    }

    private void mockUpdateDisplayName() throws FirebaseAuthException {
        given(myAuth.getUser(uid)).willReturn(userRecord);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setDisplayName(anyString())).willReturn(updateRequest);
    }

    @Test
    public void shouldDeleteUser()
        throws DatabaseAccessException, FirebaseAuthException, ExecutionException, InterruptedException,
        DocumentException {
        given(userToken.getUid()).willReturn(uid);
        given(userToken.getUsername()).willReturn(username);
        given(petDao.getStorageDao()).willReturn(storageDao);
        willDoNothing().given(storageDao).deleteImageByName(anyString());
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(true);
        given(usedUsernames.document(anyString())).willReturn(usernameRef);
        given(usernameRef.delete()).willReturn(null);
        given(collectionAdapter.getCollectionGroupDocumentsWhereArrayContains(anyString(), anyString(), any()))
            .willReturn(queryFuture);
        given(queryFuture.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(docList);
        given(queryDocumentSnapshot.getReference()).willReturn(userRef);
        given(collectionAdapter.batch()).willReturn(batch);
        given(batch.update(any(DocumentReference.class), anyString(), any())).willReturn(batch);
        willDoNothing().given(collectionAdapter).commitBatch(any(WriteBatch.class));

        dao.deleteById(userToken);
        verify(petDao).deleteAllPets(same(username));
        verify(userRef).delete();
        verify(usernameRef).delete();
        verify(myAuth).deleteUser(same(uid));
        verify(collectionAdapter)
            .getCollectionGroupDocumentsWhereArrayContains(eq(Collections.messages.name()), eq("likedBy"),
                same(username));
        verify(batch).update(same(userRef), eq("likedBy"), eq(FieldValue.arrayRemove(username)));
        verify(collectionAdapter).commitBatch(same(batch));
    }

    @Test
    public void shouldThrowDatabaseAccessExceptionWhenDeleteFromDatabaseOfNonExistentUser()
        throws ExecutionException, InterruptedException {
        given(userToken.getUid()).willReturn(uid);
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(false);
        assertThrows(DatabaseAccessException.class, () -> dao.deleteById(userToken),
            "Should throw DatabaseAccessException when the deleting a non existent user");
    }

    @Test
    public void shouldReturnUserEntity() throws ExecutionException, InterruptedException, DatabaseAccessException {
        given(userToken.getUid()).willReturn(uid);
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(true);
        given(snapshot.toObject(UserEntity.class)).willReturn(userEntity);

        UserEntity actual = dao.getUserData(userToken);
        assertSame(userEntity, actual, "Should return a user entity");
    }

    @Test
    public void shouldFailIfUserDoesNotExist() throws ExecutionException, InterruptedException {
        given(userToken.getUid()).willReturn(uid);
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.get()).willReturn(future);
        given(future.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(false);
        assertThrows(DatabaseAccessException.class, () -> dao.getUserData(userToken),
            "Should return DatabaseAccessException if user doesn't exist");
    }

    @Test
    public void shouldUpdateUsername()
        throws FirebaseAuthException, DatabaseAccessException, ExecutionException, InterruptedException {
        given(userToken.getUid()).willReturn(uid);
        given(usedUsernames.document(username)).willReturn(usernameRef);
        given(usedUsernames.document(newUsername)).willReturn(newUsernameRef);
        given(newUsernameRef.get()).willReturn(newFuture);
        given(newFuture.get()).willReturn(snapshot);
        given(snapshot.exists()).willReturn(false);
        given(myAuth.getUser(anyString())).willReturn(userRecord);
        given(userRecord.getDisplayName()).willReturn(username);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setDisplayName(anyString())).willReturn(updateRequest);
        given(myAuth.updateUserAsync(any(UserRecord.UpdateRequest.class))).willReturn(null);
        given(users.document(anyString())).willReturn(userRef);
        given(db.collectionGroup(anyString())).willReturn(query);
        given(db.collection(anyString())).willReturn(groups);
        given(query.whereEqualTo(anyString(), isA(String.class))).willReturn(query);
        given(groups.whereEqualTo(anyString(), isA(String.class))).willReturn(query);
        given(query.get()).willReturn(queryFuture);
        given(queryFuture.get()).willReturn(querySnapshot);
        given(querySnapshot.getDocuments()).willReturn(docList);
        given(queryDocumentSnapshot.getReference()).willReturn(groupRef);
        given(db.batch()).willReturn(batch);
        given(batch.update(any(DocumentReference.class), any())).willReturn(batch);
        given(batch.commit()).willReturn(batchResult);
        given(batchResult.get()).willReturn(null);
        given(documentAdapter
            .createDocumentWithId(eq(Path.ofCollection(Collections.used_usernames)), eq(newUsername), eq(docData),
                same(batch))).willReturn(userRef);


        dao.updateField(userToken, USERNAME_FIELD, newUsername);
        verify(usedUsernames).document(same(username));
        verify(myAuth, times(3)).getUser(same(uid));
        verify(userRecord, times(2)).getDisplayName();
        verify(usedUsernames).document(same(newUsername));
        verify(userRecord).updateRequest();
        verify(updateRequest).setDisplayName(same(newUsername));
        verify(myAuth).updateUserAsync(same(updateRequest));
        verify(users).document(same(uid));
        Map<String, Object> data = new HashMap<>();
        data.put(USER_FIELD, newUsername);
        verify(batch).update(groupRef, data);
        verify(batch).delete(same(usernameRef));
        data = new HashMap<>();
        data.put(USERNAME_FIELD, newUsername);
        verify(batch).update(userRef, data);
        verify(batch).commit();
    }

    @Test
    public void shouldUpdateEmail()
        throws FirebaseAuthException, DatabaseAccessException {
        given(userToken.getUid()).willReturn(uid);
        given(myAuth.getUser(anyString())).willReturn(userRecord);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setEmail(anyString())).willReturn(updateRequest);
        given(myAuth.updateUserAsync(any(UserRecord.UpdateRequest.class))).willReturn(null);
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.update(anyString(), anyString())).willReturn(null);

        dao.updateField(userToken, EMAIL_FIELD, email);
        verify(updateRequest).setEmail(same(email));
        verify(myAuth).updateUserAsync(same(updateRequest));
        verify(users).document(same(uid));
        verify(userRef).update(same(EMAIL_FIELD), same(email));
    }

    @Test
    public void shouldUpdatePassword()
        throws FirebaseAuthException, DatabaseAccessException, ExecutionException, InterruptedException {
        given(userToken.getUid()).willReturn(uid);
        given(myAuth.getUser(anyString())).willReturn(userRecord);
        given(userRecord.updateRequest()).willReturn(updateRequest);
        given(updateRequest.setPassword(anyString())).willReturn(updateRequest);
        given(myAuth.updateUserAsync(any(UserRecord.UpdateRequest.class))).willReturn(null);
        given(users.document(anyString())).willReturn(userRef);
        given(userRef.update(anyString(), anyString())).willReturn(null);

        dao.updateField(userToken, PASSWORD_FIELD, password);
        verify(updateRequest).setPassword(not(eq(password)));
        verify(myAuth).updateUserAsync(same(updateRequest));
        verify(users).document(same(uid));
        verify(userRef).update(same(PASSWORD_FIELD), not(eq(password)));
    }

    @Test
    public void shouldFailIfAnErrorOccursWhileRetrievingUserDataWhenUpdatingField() {
        given(userToken.getUid()).willReturn(uid);
        assertThrows(FirebaseAuthException.class, () -> {
            willThrow(FirebaseAuthException.class).given(myAuth).getUser(uid);
            dao.updateField(userToken, EMAIL_FIELD, email);
        }, "Should return FirebaseAuthException if an error occurs while retrieving user data");
    }
}
