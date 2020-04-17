package org.pesmypetcare.webservice.service.communitymanager;

import org.pesmypetcare.webservice.dao.communitymanager.GroupDao;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupDao dao;

    @Override
    public void createGroup(GroupEntity entity) throws DatabaseAccessException {
        if (dao.groupNameInUse(entity.getName())) {
            throw new DatabaseAccessException("invalid-group-name", "The name is already in use");
        } else {
            dao.createGroup(entity);
        }
    }

    @Override
    public void deleteGroup(String name) throws DatabaseAccessException {
        if (!dao.groupNameInUse(name)) {
            throw new DatabaseAccessException("invalid-group-name", "The name does not exist");
        } else {
            dao.deleteGroup(name);
        }
    }

    @Override
    public GroupEntity getGroup(String name) throws DatabaseAccessException {
        if (!dao.groupNameInUse(name)) {
            throw new DatabaseAccessException("invalid-group-name", "The name does not exist");
        } else {
            return dao.getGroup(name);
        }
    }

    @Override
    public List<GroupEntity> getAllGroups() throws DatabaseAccessException {
        return dao.getAllGroups();
    }

    @Override
    public void updateField(String name, String field, Object newValue) throws DatabaseAccessException {
        if (!dao.groupNameInUse(name)) {
            throw new DatabaseAccessException("invalid-group-name", "The name does not exist");
        } else {
            dao.updateField(name, field, newValue);
        }
    }
}
