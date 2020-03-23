package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.dao.PetDao;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetServiceImpl implements PetService{
    @Autowired
    private PetDao petDao;

    @Override
    public void createPet(String owner, String name, PetEntity petEntity) {
        petDao.createPet(owner, name, petEntity);
    }

    @Override
    public void deleteByOwnerAndName(String owner, String name) {
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
    public List<PetEntity> getAllPetsData(String owner) throws DatabaseAccessException {
        return petDao.getAllPetsData(owner);
    }

    @Override
    public void updateField(String owner, String name, String field, Object value) {
        petDao.updateField(owner, name, field, value);
    }
}
