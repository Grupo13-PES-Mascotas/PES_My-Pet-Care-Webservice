package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.dao.PetDao;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
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
    public void createPet(String owner, String name, PetEntity petEntity) {
        petDao.createPet(owner, name, petEntity);
    }

    @Override
    public void deleteByOwnerAndName(String owner, String name) throws DatabaseAccessException {
        petDao.deleteByOwnerAndName(owner, name);
    }

    @Override
    public void deleteAllPets(String owner) throws DatabaseAccessException {
        petDao.deleteAllPets(owner);
    }

    @Override
    public PetEntity getPetData(String owner, String name) throws DatabaseAccessException {
        return petDao.getPetData(owner, name);
    }

    @Override
    public List<Map<String, Object>> getAllPetsData(String owner) throws DatabaseAccessException {
        return petDao.getAllPetsData(owner);
    }

    @Override
    public Object getSimpleField(String owner, String name, String field) throws DatabaseAccessException {
        return petDao.getSimpleField(owner, name, field);
    }

    @Override
    public void updateSimpleField(String owner, String name, String field, Object value) {
        petDao.updateSimpleField(owner, name, field, value);
    }

    @Override
    public void deleteMapField(String owner, String name, String field) throws DatabaseAccessException {
        petDao.deleteMapField(owner, name, field);
    }

    @Override
    public Map<String, Object> getMapField(String owner, String name, String field) throws DatabaseAccessException {
        return petDao.getMapField(owner, name, field);
    }

    @Override
    public void addMapFieldElement(String owner, String name, String field, String key, Object body) throws DatabaseAccessException {
        petDao.addMapFieldElement(owner, name, field, key, body);
    }

    @Override
    public void deleteMapFieldElement(String owner, String name, String field, String key) throws DatabaseAccessException {
        petDao.deleteMapFieldElement(owner, name, field, key);
    }

    @Override
    public void updateMapFieldElement(String owner, String name, String field, String key, Object body) throws DatabaseAccessException {
        petDao.updateMapFieldElement(owner, name, field, key, body);
    }

    @Override
    public Object getMapFieldElement(String owner, String name, String field, String key) throws DatabaseAccessException {
        return petDao.getMapFieldElement(owner, name, field, key);
    }

    @Override
    public Map<String, Object> getMapFieldElementsBetweenKeys(String owner, String name, String field, String key1, String key2) throws DatabaseAccessException {
        return petDao.getMapFieldElementsBetweenKeys(owner, name, field, key1, key2);
    }
}
