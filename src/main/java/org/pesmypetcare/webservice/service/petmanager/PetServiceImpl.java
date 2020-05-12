package org.pesmypetcare.webservice.service.petmanager;

import org.pesmypetcare.webservice.dao.petmanager.PetDao;
import org.pesmypetcare.webservice.entity.petmanager.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
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

    @Override
    public void createPet(String owner, String name, PetEntity petEntity)
        throws DatabaseAccessException, DocumentException {
        petDao.createPet(owner, name, petEntity);
    }

    @Override
    public void deleteByOwnerAndName(String owner, String name)
        throws DatabaseAccessException, DocumentException {
        petDao.deleteByOwnerAndName(owner, name);
    }

    @Override
    public void deleteAllPets(String owner) throws DatabaseAccessException, DocumentException {
        petDao.deleteAllPets(owner);
    }

    @Override
    public PetEntity getPetData(String owner, String name) throws DatabaseAccessException, DocumentException {
        return petDao.getPetData(owner, name);
    }

    @Override
    public List<Map<String, Object>> getAllPetsData(String owner) throws DatabaseAccessException, DocumentException {
        return petDao.getAllPetsData(owner);
    }

    @Override
    public Object getSimpleField(String owner, String name, String field)
        throws DatabaseAccessException, DocumentException {
        return petDao.getSimpleField(owner, name, field);
    }

    @Override
    public void updateSimpleField(String owner, String name, String field, Object value)
        throws DatabaseAccessException, DocumentException {
        petDao.updateSimpleField(owner, name, field, value);
    }

    @Override
    public void deleteFieldCollection(String owner, String name, String field)
        throws DatabaseAccessException, DocumentException {
        petDao.deleteFieldCollection(owner, name, field);
    }

    @Override
    public void deleteFieldCollectionElementsPreviousToKey(String owner, String name, String field, String key)
        throws DatabaseAccessException, DocumentException {
        petDao.deleteFieldCollectionElementsPreviousToKey(owner, name, field, key);
    }

    @Override
    public List<Map<String, Object>> getFieldCollection(String owner, String name, String field)
        throws DatabaseAccessException, DocumentException {
        return petDao.getFieldCollection(owner, name, field);
    }

    @Override
    public List<Map<String, Object>> getFieldCollectionElementsBetweenKeys(String owner, String name, String field,
                                                                           String key1, String key2)
        throws DatabaseAccessException, DocumentException {
        return petDao.getFieldCollectionElementsBetweenKeys(owner, name, field, key1, key2);
    }

    @Override
    public void addFieldCollectionElement(String owner, String name, String field, String key, Map<String, Object> body)
        throws DatabaseAccessException, DocumentException {
        petDao.addFieldCollectionElement(owner, name, field, key, body);
    }

    @Override
    public void deleteFieldCollectionElement(String owner, String name, String field, String key)
        throws DatabaseAccessException, DocumentException {
        petDao.deleteFieldCollectionElement(owner, name, field, key);
    }

    @Override
    public void updateFieldCollectionElement(String owner, String name, String field, String key, Map<String,
        Object> body) throws DatabaseAccessException, DocumentException {
        petDao.updateFieldCollectionElement(owner, name, field, key, body);
    }

    @Override
    public Map<String, Object> getFieldCollectionElement(String owner, String name, String field, String key)
        throws DatabaseAccessException, DocumentException {
        return petDao.getFieldCollectionElement(owner, name, field, key);
    }
}
