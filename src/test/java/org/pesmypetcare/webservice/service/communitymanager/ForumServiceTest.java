package org.pesmypetcare.webservice.service.communitymanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.communitymanager.ForumDao;
import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    private ForumEntity forumEntity;

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
    }

    @Test
    public void createForum() throws DatabaseAccessException, DocumentException {
        given(forumDao.forumNameInUse(anyString(), anyString())).willReturn(false);
        willDoNothing().given(forumDao).createForum(anyString(), any(ForumEntity.class));

        service.createForum(groupName, forumEntity);
        verify(forumDao).forumNameInUse(same(groupName),same(forumEntity.getName()));
        verify(forumDao).createForum(same(groupName),same(forumEntity));
    }

    @Test
    public void createForumShouldFailWhenTheForumNameAlreadyExistsInTheGroup() {
        assertThrows(DocumentException.class, () -> {
            lenient().when(forumDao.forumNameInUse(anyString(), anyString())).thenReturn(true);

            service.createForum(groupName, forumEntity);
        });
    }

    @Test
    public void deleteForum() throws DatabaseAccessException, DocumentException {
        given(forumDao.forumNameInUse(anyString(), anyString())).willReturn(true);
        willDoNothing().given(forumDao).deleteForum(anyString(), anyString());

        service.deleteForum(groupName, forumName);
        verify(forumDao).forumNameInUse(same(groupName),same(forumName));
        verify(forumDao).deleteForum(same(groupName),same(forumName));
    }

    @Test
    public void getForum() {
    }

    @Test
    public void getAllForumsFromGroup() {
    }

    @Test
    public void updateName() {
    }

    @Test
    public void updateTags() {
    }

    @Test
    public void postMessage() {
    }

    @Test
    public void deleteMessage() {
    }
}
