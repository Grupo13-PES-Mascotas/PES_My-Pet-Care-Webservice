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
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private GroupDao dao;

    @InjectMocks
    private GroupService service = new GroupServiceImpl();

    @BeforeEach
    public void setUp() {
        groupName = "Dogs";
    }

    @Test
    public void getAllGroups() {
    }

    @Test
    public void getAllTags() {
    }

    @Test
    public void groupNameInUse() {
    }

    @Nested
    class UsesGroupNameInUse {

        @AfterEach
        public void verifyGroupNameInUse() throws DatabaseAccessException {
            verify(dao).groupNameInUse(same(groupName));
        }
        @Nested
        class GroupExists {

            @BeforeEach
            public void setUp() throws DatabaseAccessException {
                given(dao.groupNameInUse(anyString())).willReturn(true);
            }
            @Test
            public void createGroupShouldThrowDocumentExceptionWhenGroupAlreadyExists() throws DatabaseAccessException {
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
                Group group = new Group();
                given(dao.getGroup(anyString())).willReturn(group);

                Group result = service.getGroup(groupName);
                assertEquals(group, result, "Should return the requested group.");
            }

            @Test
            public void updateField() {
            }

            @Test
            public void subscribe() {
            }

            @Test
            public void updateTags() {
            }

            @Test
            public void unsubscribe() {
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
        }
    }
}
