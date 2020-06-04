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
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;
import org.pesmypetcare.webservice.error.InvalidOperationException;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
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
    @Mock
    private UserToken userToken;

    @InjectMocks
    private ForumService service = spy(new ForumServiceImpl());

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
            doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
            willDoNothing().given(forumDao).createForum(any(UserToken.class), anyString(), any(ForumEntity.class));

            service.createForum(token, groupName, forumEntity);
            verify(forumDao).forumNameInUse(same(groupName), same(forumEntity.getName()));
            verify(forumDao).createForum(same(userToken), same(groupName), same(forumEntity));
        }

        @Test
        public void deleteForumShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
            assertThrows(DocumentException.class, () -> service.deleteForum(token, groupName, forumName));
        }

        @Test
        public void getForumShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            assertThrows(DocumentException.class, () -> service.getForum(groupName, forumName));
        }

        @Test
        public void updateNameShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
            assertThrows(DocumentException.class, () -> service.updateName(token, groupName, forumName, newName));
        }

        @Test
        public void updateTagsShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
            assertThrows(DocumentException.class,
                () -> service.updateTags(token, groupName, forumName, newTags, deletedTags));
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
                () -> service.addUserToLikedByOfMessage(token, groupName, forumName, creator, date));
        }

        @Test
        public void removeUserFromLikedByOfMessageShouldThrowDocumentExceptionWhenTheForumDoesNotExistInTheGroup() {
            assertThrows(DocumentException.class,
                () -> service.removeUserFromLikedByOfMessage(token, groupName, forumName, creator, date));
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
            assertThrows(DocumentException.class, () -> service.createForum(token, groupName, forumEntity));
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
                doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
                willDoNothing().given(forumDao).deleteForum(any(UserToken.class), anyString(), anyString());

                service.deleteForum(token, groupName, forumName);
                verify(forumDao).deleteForum(same(userToken), same(groupName), same(forumName));
            }

            @Test
            public void updateName() throws DatabaseAccessException, DocumentException {
                doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
                willDoNothing().given(forumDao).updateName(any(UserToken.class), anyString(), anyString(), anyString());

                service.updateName(token, groupName, forumName, newName);
                verify(forumDao).updateName(eq(userToken), same(groupName), same(forumName), same(newName));
            }

            @Test
            public void updateTags() throws DatabaseAccessException, DocumentException {
                doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
                willDoNothing().given(forumDao)
                    .updateTags(any(UserToken.class), anyString(), anyString(), anyList(), anyList());

                service.updateTags(token, groupName, forumName, newTags, deletedTags);
                verify(forumDao)
                    .updateTags(eq(userToken), same(groupName), same(forumName), same(newTags), same(deletedTags));
            }

            @Test
            public void postMessage() throws DatabaseAccessException, DocumentException {
                willDoNothing().given(forumDao).postMessage(anyString(), anyString(), any(Message.class));

                service.postMessage(token, groupName, forumName, message);
                verify(forumDao).postMessage(same(groupName), same(forumName), same(message));
            }

            @Test
            public void deleteMessage() throws DatabaseAccessException, DocumentException {
                doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
                given(userToken.getUsername()).willReturn(creator);
                willDoNothing().given(forumDao).deleteMessage(anyString(), anyString(), anyString(), anyString());

                service.deleteMessage(token, groupName, forumName, creator, date);
                verify(forumDao).deleteMessage(same(groupName), same(forumName), same(creator), same(date));
            }

            @Test
            public void reportMessage() throws DatabaseAccessException, DocumentException, InvalidOperationException {
                doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
                given(userToken.getUsername()).willReturn(creator);
                willDoNothing().given(forumDao)
                    .reportMessage(any(String.class), anyString(), anyString(), anyString(), anyString());

                service.reportMessage(token, groupName, forumName, creator, date);
                verify(forumDao)
                    .reportMessage(eq(creator), same(groupName), same(forumName), same(creator), same(date));
            }

            @Test
            public void unbanMessage() throws DatabaseAccessException, DocumentException, InvalidOperationException {
                doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
                willDoNothing().given(forumDao)
                    .unbanMessage(any(UserToken.class), anyString(), anyString(), anyString(), anyString());

                service.unbanMessage(token, groupName, forumName, creator, date);
                verify(forumDao)
                    .unbanMessage(eq(userToken), same(groupName), same(forumName), same(creator), same(date));
            }

            @Test
            public void addUserToLikedByOfMessage() throws DatabaseAccessException, DocumentException {
                doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
                given(userToken.getUsername()).willReturn(creator);
                willDoNothing().given(forumDao)
                    .addUserToLikedByOfMessage(anyString(), anyString(), anyString(), anyString(), anyString());

                service.addUserToLikedByOfMessage(token, groupName, forumName, creator, date);
                verify(forumDao)
                    .addUserToLikedByOfMessage(same(creator), same(groupName), same(forumName), same(creator),
                        same(date));
            }

            @Test
            public void removeUserFromLikedByOfMessage() throws DatabaseAccessException, DocumentException {
                doReturn(userToken).when((ForumServiceImpl) service).makeUserToken(anyString());
                given(userToken.getUsername()).willReturn(creator);
                willDoNothing().given(forumDao)
                    .removeUserFromLikedByOfMessage(anyString(), anyString(), anyString(), anyString(), anyString());

                service.removeUserFromLikedByOfMessage(token, groupName, forumName, creator, date);
                verify(forumDao)
                    .removeUserFromLikedByOfMessage(same(creator), same(groupName), same(forumName), same(creator),
                        same(date));
            }
        }
    }
}
