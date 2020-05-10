package org.pesmypetcare.webservice.dao.communitymanager;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.WriteBatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.builders.Collections;
import org.pesmypetcare.webservice.builders.Path;
import org.pesmypetcare.webservice.dao.usermanager.UserDao;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreCollection;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.firestore.FirestoreDocument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class GroupDaoTest {
    private String groupName;
    private String groupId;
    private String username;
    private String userId;

    @Mock
    private UserDao userDao;
    @Mock
    private FirestoreDocument documentAdapter;
    @Mock
    private FirestoreCollection collectionAdapter;
    @Mock
    private WriteBatch batch;
    @Mock
    private DocumentReference docRef;

    @InjectMocks
    private GroupDao dao = new GroupDaoImpl();
    private String tag;

    @Nested
    class UsesWriteBatch {
        private String groupNamesPath;
        private String groupsPath;

        @BeforeEach
        public void setUp() throws DatabaseAccessException, DocumentException {
            groupName = "Dogs";
            groupId = "12daw3e23d";
            username = "John";
            userId = "sda23e8823nda";
            groupsPath = Path.ofCollection(Collections.groups);
            groupNamesPath = Path.ofCollection(Collections.groupsNames);
            given(documentAdapter.batch()).willReturn(batch);
            willDoNothing().given(documentAdapter).commitBatch(any(WriteBatch.class));
        }

        @Test
        public void createGroup() throws DatabaseAccessException, DocumentException {
            given(documentAdapter.createDocument(anyString(), any(GroupEntity.class), any(WriteBatch.class)))
                .willReturn(docRef);
            willDoNothing().given(userDao).addGroupSubscription(anyString(), anyString(), any(WriteBatch.class));
            lenient().when(documentAdapter.createDocumentWithId(anyString(), anyString(), any(), any(WriteBatch.class)))
                .thenReturn(docRef);
            given(documentAdapter.documentExists(anyString())).willReturn(true);
            willDoNothing().given(documentAdapter).updateDocumentFields(anyString(), anyString(), any(),
                any(WriteBatch.class));
            given(docRef.getId()).willReturn(groupId);
            given(userDao.getUid(anyString())).willReturn(userId);

            List<String> tags = new ArrayList<>();
            tags.add(tag);
            GroupEntity entity = new GroupEntity(groupName, username, "", tags);
            dao.createGroup(entity);
            verify(documentAdapter).createDocument(eq(groupsPath), same(entity), same(batch));
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss",
                new Locale("es", "ES"));
            Map<String, Object> data = new HashMap<>();
            data.put("group", groupId);
            String membersPath = Path.ofCollection(Collections.members, groupId);
            Map<String, Object> data2 = new HashMap<>();
            data2.put("user", username);
            data2.put("date", timeFormatter.format(LocalDateTime.now()));
            verify(documentAdapter).createDocumentWithId(AdditionalMatchers.or(eq(groupNamesPath), eq(membersPath)),
                AdditionalMatchers.or(eq(groupName), eq(userId)), AdditionalMatchers.or(eq(data), eq(data2)),
                same(batch));
            verify(userDao).addGroupSubscription(eq(username), eq(groupName), same(batch));
            String tagPath = Path.ofDocument(Collections.tags, tag);
            verify(documentAdapter).documentExists(eq(tagPath));
            verify(documentAdapter).updateDocumentFields(eq(tagPath), eq("groups"), any(FieldValue.class),
                same(batch));
        }
    }

    @BeforeEach
    public void setUp() {
        tag = "dogs";
    }

    @Test
    public void deleteGroup() {
    }

    @Test
    public void getGroup() {
    }

    @Test
    public void getAllGroups() {
    }

    @Test
    public void updateField() {
    }

    @Test
    public void groupNameInUse() {
    }

    @Test
    public void subscribe() {
    }

    @Test
    public void updateTags() {
    }

    @Test
    public void getAllTags() {
    }

    @Test
    public void unsubscribe() {
    }

    @Test
    public void getGroupId() {
    }
}
