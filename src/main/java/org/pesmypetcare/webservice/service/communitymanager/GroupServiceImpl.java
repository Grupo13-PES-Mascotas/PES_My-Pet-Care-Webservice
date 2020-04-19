package org.pesmypetcare.webservice.service.communitymanager;

import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.entity.communitymanager.TagEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupDao groupDao;

    @Override
    public void createGroup(GroupEntity entity) throws DatabaseAccessException {
        if (groupDao.groupNameInUse(entity.getName())) {
            throw new DatabaseAccessException("invalid-group-name", "The name is already in use");
        } else {
            groupDao.createGroup(entity);
        }
    }

    @Override
    public void deleteGroup(String name) throws DatabaseAccessException {
        if (!groupDao.groupNameInUse(name)) {
            throw new DatabaseAccessException("invalid-group-name", "The name does not exist");
        } else {
            groupDao.deleteGroup(name);
        }
    }

    @Override
    public GroupEntity getGroup(String name) throws DatabaseAccessException {
        if (!groupDao.groupNameInUse(name)) {
            throw new DatabaseAccessException("invalid-group-name", "The name does not exist");
        } else {
            return groupDao.getGroup(name);
        }
    }

    @Override
    public List<GroupEntity> getAllGroups() throws DatabaseAccessException {
        return groupDao.getAllGroups();
    }

    @Override
    public void updateField(String name, String field, String newValue) throws DatabaseAccessException {
        if (!groupDao.groupNameInUse(name)) {
            throw new DatabaseAccessException("invalid-group-name", "The name does not exist");
        } else {
            groupDao.updateField(name, field, newValue);
        }
    }

    @Override
    public void subscribe(String token, String group, String username) throws DatabaseAccessException {
        if (!groupDao.groupNameInUse(group)) {
            throw new DatabaseAccessException("invalid-group-name", "The name does not exist");
        } else {
            groupDao.subscribe(group, username);
        }
    }

    @Override
    public void updateTags(String group, List<String> newTags, List<String> deletedTags) throws DatabaseAccessException {
        if (!groupDao.groupNameInUse(group)) {
            throw new DatabaseAccessException("invalid-group-name", "The name does not exist");
        } else {
            groupDao.updateTags(group, newTags, deletedTags);
        }
    }

    @Override
    public Map<String, TagEntity> getAllTags() throws DatabaseAccessException {
        return groupDao.getAllTags();
    }
}
