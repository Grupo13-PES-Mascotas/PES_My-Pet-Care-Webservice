package org.pesmypetcare.webservice.service.medalmanager;

import org.pesmypetcare.webservice.dao.medalmanager.UserMedalDao;
import org.pesmypetcare.webservice.entity.medalmanager.UserMedalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserTokenImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
@Service
public class UserMedalServiceImpl implements UserMedalService {
    @Autowired
    private UserMedalDao userMedalDao;

    public UserToken makeUserToken(String token) {
        return new UserTokenImpl(token);
    }

    @Override
    public void createUserMedal(String token, String name, UserMedalEntity medal)
        throws DatabaseAccessException, DocumentException {
        userMedalDao.createUserMedal(makeUserToken(token).getUid(), name, medal);
    }

    @Override
    public UserMedalEntity getUserMedalData(String token, String name) throws DatabaseAccessException,
        DocumentException {
        return userMedalDao.getUserMedalData(makeUserToken(token).getUid(), name);
    }

    @Override
    public List<Map<String, UserMedalEntity>> getAllUserMedalsData(String token) throws DatabaseAccessException,
        DocumentException {
        return userMedalDao.getAllUserMedalsData(makeUserToken(token).getUid());
    }

    @Override
    public void updateField(String token, String name, String field, Object value) throws DatabaseAccessException,
        DocumentException {
        userMedalDao.updateField(makeUserToken(token).getUid(), name, field, value);
    }

    @Override
    public Object getField(String token, String name, String field) throws DatabaseAccessException,
        DocumentException {
        return userMedalDao.getField(makeUserToken(token).getUid(), name, field);
    }


}
