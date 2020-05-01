package org.pesmypetcare.webservice.service;


import org.pesmypetcare.webservice.dao.KcalDao;
import org.pesmypetcare.webservice.entity.KcalEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KcalServiceImpl implements KcalService {
    @Autowired
    private KcalDao kcalDao;


    @Override
    public void createKcal(String owner, String petName, String date, KcalEntity kcalEntity) {
        kcalDao.createKcal(owner, petName, date, kcalEntity);
    }

    @Override
    public void deleteAllKcals(String owner, String petName) throws DatabaseAccessException {
        kcalDao.deleteAllKcals(owner, petName);
    }

    @Override
    public void deleteKcalByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        kcalDao.deleteKcalByDate(owner, petName, petDate);
    }

    @Override
    public KcalEntity getKcalByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        return kcalDao.getKcalByDate(owner, petName, petDate);
    }

    @Override
    public List<Map<String, Object>> getAllKcal(String owner, String petName) throws DatabaseAccessException {
        return kcalDao.getAllKcal(owner, petName);
    }

    @Override
    public List<Map<String, Object>> getAllKcalsBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        return kcalDao.getAllKcalsBetween(owner, petName, initialDate, finalDate);
    }

    @Override
    public void updateKcal(String owner, String petName, String petDate, Object value)
        throws DatabaseAccessException {
        kcalDao.updateKcal(owner, petName, petDate, value);
    }
}
