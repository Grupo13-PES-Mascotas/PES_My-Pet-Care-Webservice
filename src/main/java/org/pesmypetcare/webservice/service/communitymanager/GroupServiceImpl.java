package org.pesmypetcare.webservice.service.communitymanager;

import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
import org.pesmypetcare.webservice.entity.communitymanager.Group;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.entity.communitymanager.TagEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserTokenImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@Service
public class GroupServiceImpl implements GroupService {
    private static final String NAME_DOES_NOT_EXISTS = "The name does not exist";
    private static final String INVALID_NAME_CODE = "document-not-exists";
    @Autowired
    private GroupDao groupDao;

    public UserToken makeUserToken(String token) {
        return new UserTokenImpl(token);
    }

    @Override
    public void createGroup(String token, GroupEntity entity) throws DatabaseAccessException, DocumentException {
        if (groupDao.groupNameInUse(entity.getName())) {
            throw new DocumentException("document-already-exists", "The name is already in use");
        } else {
            groupDao.createGroup(makeUserToken(token), entity);
        }
    }

    @Override
    public void deleteGroup(String token, String name) throws DatabaseAccessException, DocumentException {
        if (!groupDao.groupNameInUse(name)) {
            throw new DocumentException(INVALID_NAME_CODE, NAME_DOES_NOT_EXISTS);
        } else {
            groupDao.deleteGroup(makeUserToken(token), name);
        }
    }

    @Override
    public Group getGroup(String name) throws DatabaseAccessException, DocumentException {
        if (!groupDao.groupNameInUse(name)) {
            throw new DocumentException(INVALID_NAME_CODE, NAME_DOES_NOT_EXISTS);
        } else {
            return groupDao.getGroup(name);
        }
    }

    @Override
    public List<Group> getAllGroups() throws DatabaseAccessException {
        return groupDao.getAllGroups();
    }

    @Override
    public void updateField(String name, String field, String newValue)
        throws DatabaseAccessException, DocumentException {
        if (!groupDao.groupNameInUse(name)) {
            throw new DocumentException(INVALID_NAME_CODE, NAME_DOES_NOT_EXISTS);
        } else {
            groupDao.updateField(name, field, newValue);
        }
    }

    @Override
    public void updateTags(String group, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException, DocumentException {
        if (!groupDao.groupNameInUse(group)) {
            throw new DocumentException(INVALID_NAME_CODE, NAME_DOES_NOT_EXISTS);
        } else {
            groupDao.updateTags(group, newTags, deletedTags);
        }
    }

    @Override
    public Map<String, TagEntity> getAllTags() throws DatabaseAccessException {
        return groupDao.getAllTags();
    }

    @Override
    public void subscribe(String token, String group, String username) throws DatabaseAccessException,
        DocumentException {
        if (!groupDao.groupNameInUse(group)) {
            throw new DocumentException(INVALID_NAME_CODE, NAME_DOES_NOT_EXISTS);
        } else {
            groupDao.subscribe(group, makeUserToken(token));
        }
    }

    @Override
    public void unsubscribe(String token, String group, String username)
        throws DatabaseAccessException, DocumentException {
        if (!groupDao.groupNameInUse(group)) {
            throw new DocumentException(INVALID_NAME_CODE, NAME_DOES_NOT_EXISTS);
        } else {
            groupDao.unsubscribe(group, makeUserToken(token));
        }
    }

    @Override
    public boolean groupNameInUse(String name) throws DatabaseAccessException {
        return groupDao.groupNameInUse(name);
    }
}
