package org.pesmypetcare.webservice.service.medalmanager;

import org.pesmypetcare.webservice.dao.medalmanager.UserMedalDao;
import org.pesmypetcare.webservice.entity.medalmanager.UserMedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Oriol Catalán
 */
@Service
public class UserMedalServiceImpl implements UserMedalService {
    @Autowired
    private UserMedalDao userMedalDao;

    @Override
    public void createUserMedal(String owner, String name, UserMedalEntity medal)
        throws DatabaseAccessException, DocumentException {
        userMedalDao.createUserMedal(owner, name, medal);
    }

    @Override
    public UserMedalEntity getUserMedalData(String owner, String name) throws DatabaseAccessException,
        DocumentException {
        return userMedalDao.getUserMedalData(owner, name);
    }

    @Override
    public List<UserMedalEntity> getAllUserMedalsData(String owner) throws DatabaseAccessException,
        DocumentException {
        return userMedalDao.getAllUserMedalsData(owner);
    }

    @Override
    public void updateField(String owner, String name, String field, Object value) throws DatabaseAccessException,
        DocumentException {
        userMedalDao.updateField(owner, name, field, value);
    }

    @Override
    public Object getField(String owner, String name, String field) throws DatabaseAccessException,
        DocumentException {
        return userMedalDao.getField(owner, name, field);
    }


}
