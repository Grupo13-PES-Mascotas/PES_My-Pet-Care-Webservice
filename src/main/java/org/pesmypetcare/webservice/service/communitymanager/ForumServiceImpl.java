package org.pesmypetcare.webservice.service.communitymanager;

import org.pesmypetcare.webservice.dao.communitymanager.ForumDao;
import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.Message;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
@Service
public class ForumServiceImpl implements ForumService {
    private final String DOCUMENT_ALREADY_EXISTS = "document-already-exists";
    private final String DOCUMENT_NOT_EXISTS = "document-not-exists";
    private final String FORUM_DOES_NOT_EXISTS = "The forum does not exist";
    @Autowired
    private ForumDao forumDao;
    @Autowired
    private GroupDao groupDao;

    @Override
    public void createForum(String parentGroup, ForumEntity forumEntity)
        throws DatabaseAccessException, DocumentException {
        if (forumDao.forumNameInUse(parentGroup, forumEntity.getName())) {
            throw new DocumentException(DOCUMENT_ALREADY_EXISTS, "The name is already in use");
        } else {
            forumDao.createForum(parentGroup, forumEntity);
        }
    }

    @Override
    public void deleteForum(String parentGroup, String forumName) throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DocumentException(DOCUMENT_NOT_EXISTS, FORUM_DOES_NOT_EXISTS);
        } else {
            forumDao.deleteForum(parentGroup, forumName);
        }
    }

    @Override
    public ForumEntity getForum(String parentGroup, String forumName)
        throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DocumentException(DOCUMENT_NOT_EXISTS, FORUM_DOES_NOT_EXISTS);
        } else {
            return forumDao.getForum(parentGroup, forumName);
        }
    }

    @Override
    public List<ForumEntity> getAllForumsFromGroup(String groupName) throws DatabaseAccessException, DocumentException {
        if (!groupDao.groupNameInUse(groupName)) {
            throw new DocumentException(DOCUMENT_NOT_EXISTS, "The group does not exist");
        } else {
            return forumDao.getAllForumsFromGroup(groupName);
        }
    }

    @Override
    public void updateName(String parentGroup, String currentName, String newName)
        throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, currentName)) {
            throw new DocumentException(DOCUMENT_NOT_EXISTS, FORUM_DOES_NOT_EXISTS);
        } else {
            forumDao.updateName(parentGroup, currentName, newName);
        }
    }

    @Override
    public void updateTags(String parentGroup, String forumName, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DocumentException(DOCUMENT_NOT_EXISTS, FORUM_DOES_NOT_EXISTS);
        } else {
            forumDao.updateTags(parentGroup, forumName, newTags, deletedTags);
        }
    }

    @Override
    public void postMessage(String token, String parentGroup, String forumName, Message message)
        throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DocumentException(DOCUMENT_NOT_EXISTS, FORUM_DOES_NOT_EXISTS);
        } else {
            forumDao.postMessage(parentGroup, forumName, message);
        }
    }

    @Override
    public void deleteMessage(String token, String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DocumentException(DOCUMENT_NOT_EXISTS, FORUM_DOES_NOT_EXISTS);
        } else {
            forumDao.deleteMessage(parentGroup, forumName, creator, date);
        }
    }

    @Override
    public void addUserToLikedByOfMessage(String token, String username, String parentGroup, String forumName,
                                          String creator, String date)
        throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DocumentException(DOCUMENT_NOT_EXISTS, FORUM_DOES_NOT_EXISTS);
        } else {
            forumDao.addUserToLikedByOfMessage(username, parentGroup, forumName, creator, date);
        }
    }

    @Override
    public void removeUserFromLikedByOfMessage(String token, String username, String parentGroup, String forumName,
                                               String creator, String date)
        throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DocumentException(DOCUMENT_NOT_EXISTS, FORUM_DOES_NOT_EXISTS);
        } else {
            forumDao.removeUserFromLikedByOfMessage(username, parentGroup, forumName, creator, date);
        }
    }
}
