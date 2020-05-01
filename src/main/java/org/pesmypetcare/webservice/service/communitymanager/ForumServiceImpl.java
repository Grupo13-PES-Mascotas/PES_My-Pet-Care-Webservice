package org.pesmypetcare.webservice.service.communitymanager;

import org.pesmypetcare.webservice.dao.communitymanager.ForumDao;
import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.MessageEntity;
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
    @Autowired
    private ForumDao forumDao;
    @Autowired
    private GroupDao groupDao;

    @Override
    public void createForum(String parentGroup, ForumEntity forumEntity)
        throws DatabaseAccessException, DocumentException {
        if (forumDao.forumNameInUse(parentGroup, forumEntity.getName())) {
            throw new DatabaseAccessException("invalid-forum", "The name is already in use");
        } else {
            forumDao.createForum(parentGroup, forumEntity);
        }
    }

    @Override
    public void deleteForum(String parentGroup, String forumName) throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DatabaseAccessException("invalid-forum", "The forum does not exist");
        } else {
            forumDao.deleteForum(parentGroup, forumName);
        }
    }

    @Override
    public ForumEntity getForum(String parentGroup, String forumName) throws DatabaseAccessException,
        DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DatabaseAccessException("invalid-forum", "The forum does not exist");
        } else {
            return forumDao.getForum(parentGroup, forumName);
        }
    }

    @Override
    public List<ForumEntity> getAllForumsFromGroup(String parentGroup) throws DatabaseAccessException {
        if (!groupDao.groupNameInUse(parentGroup)) {
            throw new DatabaseAccessException("invalid-group-name", "The group does not exist");
        } else {
            return forumDao.getAllForumsFromGroup(parentGroup);
        }
    }

    @Override
    public void updateName(String parentGroup, String currentName, String newName)
        throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, currentName)) {
            throw new DatabaseAccessException("invalid-forum", "The forum does not exist");
        } else {
            forumDao.updateName(parentGroup, currentName, newName);
        }
    }

    @Override
    public void updateTags(String parentGroup, String forumName, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DatabaseAccessException("invalid-forum-name", "The forum does not exist");
        } else {
            forumDao.updateTags(parentGroup, forumName, newTags, deletedTags);
        }
    }

    @Override
    public void postMessage(String token, String parentGroup, String forumName, MessageEntity post)
        throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DatabaseAccessException("invalid-forum", "The forum does not exist");
        } else {
            forumDao.postMessage(parentGroup, forumName, post);
        }
    }

    @Override
    public void deleteMessage(String token, String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DatabaseAccessException("invalid-forum", "The forum does not exist");
        } else {
            forumDao.deleteMessage(parentGroup, forumName, creator, date);
        }
    }

    /*@Override
    public void subscribe(String token, String parentGroup, String forumName, String username) throws DatabaseAccessException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DatabaseAccessException("invalid-forum-name", "The forum does not exist");
        } else {
            forumDao.subscribe(parentGroup, forumName, username);
        }
    }

    @Override
    public void unsubscribe(String token, String parentGroup, String forumName, String username) throws DatabaseAccessException {
        if (!forumDao.forumNameInUse(parentGroup, forumName)) {
            throw new DatabaseAccessException("invalid-forum-name", "The forum does not exist");
        } else {
            forumDao.unsubscribe(parentGroup, forumName, username);
        }
    }*/


}
