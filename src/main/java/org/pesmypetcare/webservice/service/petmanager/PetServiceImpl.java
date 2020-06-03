package org.pesmypetcare.webservice.service.petmanager;

import org.pesmypetcare.webservice.dao.petmanager.PetDao;
import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserTokenImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Marc Simó
 */
@Service
public class PetServiceImpl implements PetService {
    @Autowired
    private PetDao petDao;

    public UserToken makeUserToken(String token) {
        return new UserTokenImpl(token);
    }

    @Override
    public void createPet(String token, String name, PetEntity petEntity)
        throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        petDao.createPet(userToken.getUid(), name, petEntity);
    }

    @Override
    public void deleteByOwnerAndName(String token, String name) throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        petDao.deleteByOwnerAndName(userToken.getUid(), name);
    }

    @Override
    public void deleteAllPets(String token) throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        petDao.deleteAllPets(userToken.getUid());
    }

    @Override
    public PetEntity getPetData(String token, String name) throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        return petDao.getPetData(userToken.getUid(), name);
    }

    @Override
    public List<Map<String, Object>> getAllPetsData(String token) throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        return petDao.getAllPetsData(userToken.getUid());
    }

    @Override
    public Object getSimpleField(String token, String name, String field)
        throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        return petDao.getSimpleField(userToken.getUid(), name, field);
    }

    @Override
    public void updateSimpleField(String token, String name, String field, Object value)
        throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        petDao.updateSimpleField(userToken.getUid(), name, field, value);
    }

    @Override
    public void deleteFieldCollection(String token, String name, String field)
        throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        petDao.deleteFieldCollection(userToken.getUid(), name, field);
    }

    @Override
    public void deleteFieldCollectionElementsPreviousToKey(String token, String name, String field, String key)
        throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        petDao.deleteFieldCollectionElementsPreviousToKey(userToken.getUid(), name, field, key);
    }

    @Override
    public List<Map<String, Object>> getFieldCollection(String token, String name, String field)
        throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        return petDao.getFieldCollection(userToken.getUid(), name, field);
    }

    @Override
    public List<Map<String, Object>> getFieldCollectionElementsBetweenKeys(String token, String name, String field,
                                                                           String key1, String key2)
        throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        return petDao.getFieldCollectionElementsBetweenKeys(userToken.getUid(), name, field, key1, key2);
    }

    @Override
    public void addFieldCollectionElement(String token, String name, String field, String key, Map<String, Object> body)
        throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        petDao.addFieldCollectionElement(userToken.getUid(), name, field, key, body);
    }

    @Override
    public void deleteFieldCollectionElement(String token, String name, String field, String key)
        throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        petDao.deleteFieldCollectionElement(userToken.getUid(), name, field, key);
    }

    @Override
    public void updateFieldCollectionElement(String token, String name, String field, String key,
                                             Map<String, Object> body)
        throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        petDao.updateFieldCollectionElement(userToken.getUid(), name, field, key, body);
    }

    @Override
    public Map<String, Object> getFieldCollectionElement(String token, String name, String field, String key)
        throws DatabaseAccessException, DocumentException {
        UserToken userToken = makeUserToken(token);
        return petDao.getFieldCollectionElement(userToken.getUid(), name, field, key);
    }
}
