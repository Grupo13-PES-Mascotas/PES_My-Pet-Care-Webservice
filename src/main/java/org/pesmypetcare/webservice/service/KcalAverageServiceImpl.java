package org.pesmypetcare.webservice.service;


import org.pesmypetcare.webservice.dao.KcalAverageDao;
import org.pesmypetcare.webservice.entity.KcalAverageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KcalAverageServiceImpl implements KcalAverageService {
    @Autowired
    private KcalAverageDao kcalAverageDao;


    @Override
    public void createKcalAverage(String owner, String petName, String date, KcalAverageEntity kcalAverageEntity) {
        kcalAverageDao.createKcalAverage(owner, petName, date, kcalAverageEntity);
    }

    @Override
    public void deleteAllKcalAverages(String owner, String petName) throws DatabaseAccessException {
        kcalAverageDao.deleteAllKcalAverages(owner, petName);
    }

    @Override
    public void deleteKcalAverageByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        kcalAverageDao.deleteKcalAverageByDate(owner, petName, petDate);
    }

    @Override
    public KcalAverageEntity getKcalAverageByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        return kcalAverageDao.getKcalAverageByDate(owner, petName, petDate);
    }

    @Override
    public List<Map<String, Object>> getAllKcalAverage(String owner, String petName) throws DatabaseAccessException {
        return kcalAverageDao.getAllKcalAverage(owner, petName);
    }

    @Override
    public List<Map<String, Object>> getAllKcalAveragesBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        return kcalAverageDao.getAllKcalAveragesBetween(owner, petName, initialDate, finalDate);
    }

    @Override
    public void updateKcalAverage(String owner, String petName, String petDate, Object value)
        throws DatabaseAccessException {
        kcalAverageDao.updateKcalAverage(owner, petName, petDate, value);
    }
}
