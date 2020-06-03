package org.pesmypetcare.webservice.service.communitymanager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.communitymanager.ForumDao;
import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.Message;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.error.InvalidOperationException;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

/**
 * @author Santiago Del Rey
 */
@ExtendWith(MockitoExtension.class)
class ForumServiceTest {
    private String groupName;
    private String forumName;
    private String newName;
    private String token;
    private String creator;
    private String date;
    private List<String> newTags;
    private List<String> deletedTags;
    private ForumEntity forumEntity;
    private Message message;

    @Mock
    private GroupDao groupDao;
    @Mock
    private ForumDao forumDao;

    @InjectMocks
    private ForumService service = new ForumServiceImpl();

    @BeforeEach
    public void setUp() {
        groupName = "Dogs";
        forumName = "Huskies";
        forumEntity = new ForumEntity();
        forumEntity.setName(forumName);
        token = "my-token";
        creator = "John Doe";
        date = "2020-05-01T17:48:15";
        newTags = new LinkedList<>();
        deletedTags = new LinkedList<>();
        message = new Message();
        newName = "German Shepherds";
    }

    @Test
    public void getAllForumsFromGroup() throws DatabaseAccessException, DocumentException {
        List<ForumEntity> forumEntityList = new LinkedList<>();
        forumEntityList.add(forumEntity);
        given(groupDao.groupNameInUse(anyString())).willReturn(true);
        given(service.getAllForumsFromGroup(groupName)).willReturn(forumEntityList);

        List<ForumEntity> result = service.getAllForumsFromGroup(groupName);
        assertEquals(forumEntityList, result,
            "Should return a list with all the forum entities in the requested " + "collection.");
    }

    @Test
    public void getAllForumsFromGroupShouldThrowDocumentExceptionWhenTheGroupDoesNotExist() {
        assertThrows(DocumentException.class, () -> {
            lenient().when(groupDao.groupNameInUse(anyString())).thenReturn(false);
            service.getAllForumsFromGroup(groupName);
        });
    }

    @Nested
    class ForumNameInUseReturnsFalse {
        @BeforeEach
        public void setUp() throws DatabaseAccessException {
            given(forumDao.forumNameInUse(anyString(), anyString())).willReturn(false);
        }

        @Test
        public void createForum() throws DatabaseAccessException, DocumentException {
            willDoNothing().given(forumDao).createForum(anyString(), any(ForumEntity.class));

            service.createForum(groupName, forumEntity);
            verify(forumDao).forumNameInUse(same(groupName), same(forumEntity.getName()));
            verify(forumDao).createForum(same(groupName), same(forumEntity));
        }

        @Test
        public void deleteForumShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            assertThrows(DocumentException.class, () -> service.deleteForum(groupName, forumName));
        }

        @Test
        public void getForumShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            assertThrows(DocumentException.class, () -> service.getForum(groupName, forumName));
        }

        @Test
        public void updateNameShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            assertThrows(DocumentException.class, () -> service.updateName(groupName, forumName, newName));
        }

        @Test
        public void updateTagsShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            assertThrows(DocumentException.class, () -> service.updateTags(groupName, forumName, newTags, deletedTags));
        }

        @Test
        public void postMessageShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            assertThrows(DocumentException.class, () -> service.postMessage(token, groupName, forumName, message));
        }

        @Test
        public void deleteMessageShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            assertThrows(DocumentException.class,
                () -> service.deleteMessage(token, groupName, forumName, creator, date));
        }

        @Test
        public void addUserToLikedByOfMessageShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            assertThrows(DocumentException.class,
                () -> service.addUserToLikedByOfMessage(token, creator, groupName, forumName, creator, date));
        }

        @Test
        public void removeUserFromLikedByOfMessageShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            assertThrows(DocumentException.class,
                () -> service.removeUserFromLikedByOfMessage(token, creator, groupName, forumName, creator, date));
        }
    }

    @Nested
    class ForumNameInUseReturnsTrue {
        @BeforeEach
        public void setUp() throws DatabaseAccessException {
            given(forumDao.forumNameInUse(anyString(), anyString())).willReturn(true);
        }

        @Test
        public void createForumShouldThrowDocumentExceptionWhenTheForumNameAlreadyExistsInTheGroup() {
            assertThrows(DocumentException.class, () -> service.createForum(groupName, forumEntity));
        }

        @Test
        public void getForum() throws DatabaseAccessException, DocumentException {
            given(service.getForum(groupName, forumName)).willReturn(forumEntity);

            ForumEntity result = service.getForum(groupName, forumName);
            assertEquals(forumEntity, result, "Should return the forum entity requested.");
        }

        @Nested
        class VerifiesForumNameInUse {
            @AfterEach
            public void verifyForumNameInUse() throws DatabaseAccessException {
                verify(forumDao).forumNameInUse(same(groupName), same(forumName));
            }

            @Test
            public void deleteForum() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(forumDao).deleteForum(anyString(), anyString());

                service.deleteForum(groupName, forumName);
                verify(forumDao).deleteForum(same(groupName), same(forumName));
            }

            @Test
            public void updateName() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(forumDao).updateName(anyString(), anyString(), anyString());

                service.updateName(groupName, forumName, newName);
                verify(forumDao).updateName(same(groupName), same(forumName), same(newName));
            }

            @Test
            public void updateTags() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(forumDao).updateTags(anyString(), anyString(), anyList(), anyList());

                service.updateTags(groupName, forumName, newTags, deletedTags);
                verify(forumDao).updateTags(same(groupName), same(forumName), same(newTags), same(deletedTags));
            }

            @Test
            public void postMessage() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(forumDao).postMessage(anyString(), anyString(), any(Message.class));

                service.postMessage(token, groupName, forumName, message);
                verify(forumDao).postMessage(same(groupName), same(forumName), same(message));
            }

            @Test
            public void deleteMessage() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(forumDao).deleteMessage(anyString(), anyString(), anyString(), anyString());

                service.deleteMessage(token, groupName, forumName, creator, date);
                verify(forumDao).deleteMessage(same(groupName), same(forumName), same(creator), same(date));
            }

            @Test
            public void reportMessage() throws DatabaseAccessException, DocumentException, InvalidOperationException {
                willDoNothing().given(forumDao).reportMessage(anyString(), anyString(), anyString(), anyString(),
                    anyString());

                service.reportMessage(token, groupName, forumName, creator, newName, date);
                verify(forumDao).reportMessage(same(groupName), same(forumName), same(creator),
                    same(newName), same(date));
            }

            @Test
            public void unbanMessage() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(forumDao).unbanMessage(anyString(), anyString(), anyString(), anyString());

                service.unbanMessage(token, groupName, forumName, creator, date);
                verify(forumDao).unbanMessage(same(groupName), same(forumName), same(creator), same(date));
            }

            @Test
            public void addUserToLikedByOfMessage() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(forumDao)
                    .addUserToLikedByOfMessage(anyString(), anyString(), anyString(), anyString(), anyString());

                service.addUserToLikedByOfMessage(token, creator, groupName, forumName, creator, date);
                verify(forumDao)
                    .addUserToLikedByOfMessage(same(creator), same(groupName), same(forumName), same(creator),
                        same(date));
            }

            @Test
            public void removeUserFromLikedByOfMessage() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(forumDao)
                    .removeUserFromLikedByOfMessage(anyString(), anyString(), anyString(), anyString(), anyString());

                service.removeUserFromLikedByOfMessage(token, creator, groupName, forumName, creator, date);
                verify(forumDao)
                    .removeUserFromLikedByOfMessage(same(creator), same(groupName), same(forumName), same(creator),
                        same(date));
            }
        }
    }
}
