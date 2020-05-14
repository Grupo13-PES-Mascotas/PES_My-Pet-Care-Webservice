package org.pesmypetcare.webservice.service.communitymanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
import org.pesmypetcare.webservice.entity.communitymanager.Group;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.entity.communitymanager.TagEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    private String groupName;
    private Group group;

    @Mock
    private GroupDao dao;

    @InjectMocks
    private GroupService service = new GroupServiceImpl();

    @BeforeEach
    public void setUp() {
        groupName = "Dogs";
        group = new Group();
    }

    @Test
    public void getAllGroups() throws DatabaseAccessException {
        List<Group> groups = new ArrayList<>();
        groups.add(group);
        given(dao.getAllGroups()).willReturn(groups);

        List<Group> result = service.getAllGroups();
        assertEquals(groups, result, "Should return all the existing groups.");
    }

    @Test
    public void getAllTags() throws DatabaseAccessException {
        Map<String, TagEntity> tags = new HashMap<>();
        tags.put("dogs", new TagEntity());
        given(dao.getAllTags()).willReturn(tags);

        Map<String, TagEntity> result = service.getAllTags();
        assertEquals(tags, result, "Should return all the existing tags.");
    }

    @Nested
    class UsesGroupNameInUse {
        private String field;
        private String newName;
        private String username;
        private String token;
        private List<String> newTags;
        private List<String> deletedTags;


        @AfterEach
        public void verifyGroupNameInUse() throws DatabaseAccessException {
            verify(dao).groupNameInUse(same(groupName));
        }

        @BeforeEach
        public void setUp() {
            field = "name";
            newName = "Cats and Dogs";
            username = "John";
            token = "my-token";
            newTags = new ArrayList<>();
            deletedTags = new ArrayList<>();
        }
        @Nested
        class GroupExists {


            @BeforeEach
            public void setUp() throws DatabaseAccessException {
                given(dao.groupNameInUse(anyString())).willReturn(true);
            }

            @Test
            public void createGroupShouldThrowDocumentExceptionWhenGroupAlreadyExists() {
                GroupEntity entity = new GroupEntity();
                entity.setName(groupName);
                assertThrows(DocumentException.class, () -> service.createGroup(entity),
                    "Should throw an exception when the group already exists.");
            }

            @Test
            public void deleteGroup() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(dao).deleteGroup(anyString());

                service.deleteGroup(groupName);
                verify(dao).deleteGroup(same(groupName));
            }

            @Test
            public void getGroup() throws DatabaseAccessException, DocumentException {
                given(dao.getGroup(anyString())).willReturn(group);

                Group result = service.getGroup(groupName);
                assertEquals(group, result, "Should return the requested group.");
            }

            @Test
            public void updateField() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(dao).updateField(anyString(), anyString(), anyString());

                service.updateField(groupName, field, newName);
                verify(dao).updateField(same(groupName), same(field), same(newName));
            }

            @Test
            public void updateTags() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(dao).updateTags(anyString(), anyList(), anyList());

                service.updateTags(groupName, newTags, deletedTags);
                verify(dao).updateTags(same(groupName), same(newTags), same(deletedTags));
            }

            @Test
            public void subscribe() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(dao).subscribe(anyString(), anyString());

                service.subscribe(token, groupName, username);
                verify(dao).subscribe(same(groupName), same(username));
            }

            @Test
            public void unsubscribe() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(dao).unsubscribe(anyString(), anyString());

                service.unsubscribe(token, groupName, username);
                verify(dao).unsubscribe(same(groupName), same(username));
            }

            @Test
            public void groupNameInUse() throws DatabaseAccessException {
                assertTrue(service.groupNameInUse(groupName), "Should return true if the group exists.");
            }
        }

        @Nested
        class GroupNotExists {
            @BeforeEach
            public void setUp() throws DatabaseAccessException {
                given(dao.groupNameInUse(anyString())).willReturn(false);
            }

            @Test
            public void createGroup() throws DatabaseAccessException, DocumentException {
                GroupEntity entity = new GroupEntity();
                entity.setName(groupName);
                willDoNothing().given(dao).createGroup(entity);

                service.createGroup(entity);
                verify(dao).createGroup(same(entity));
            }

            @Test
            public void deleteGroupShouldThrowDocumentExceptionWhenGroupAlreadyExists() {
                assertThrows(DocumentException.class, () -> service.deleteGroup(groupName),
                    "Should throw an exception when the group already exists.");
            }

            @Test
            public void getGroupShouldThrowDocumentExceptionWhenGroupAlreadyExists() {
                assertThrows(DocumentException.class, () -> service.getGroup(groupName),
                    "Should throw an exception when the group already exists.");
            }

            @Test
            public void updateFieldShouldThrowDocumentExceptionWhenGroupAlreadyExists() {
                assertThrows(DocumentException.class, () -> service.updateField(groupName, field, newName),
                    "Should throw an exception when the group already exists.");
            }

            @Test
            public void updateTagsShouldThrowDocumentExceptionWhenGroupAlreadyExists() {
                assertThrows(DocumentException.class, () -> service.updateTags(groupName, newTags, deletedTags),
                    "Should throw an exception when the group already exists.");
            }

            @Test
            public void subscribeShouldThrowDocumentExceptionWhenGroupAlreadyExists() {
                assertThrows(DocumentException.class, () -> service.subscribe(token, groupName, username),
                    "Should throw an exception when the group already exists.");
            }

            @Test
            public void unsubscribeShouldThrowDocumentExceptionWhenGroupAlreadyExists() {
                assertThrows(DocumentException.class, () -> service.unsubscribe(token, groupName, username),
                    "Should throw an exception when the group already exists.");
            }
        }
    }
}
