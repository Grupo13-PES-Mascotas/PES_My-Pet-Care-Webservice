package org.pesmypetcare.webservice.service;


import org.pesmypetcare.webservice.dao.FreqWashDao;
import org.pesmypetcare.webservice.entity.FreqWashEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FreqWashServiceImpl implements FreqWashService {
    @Autowired
    private FreqWashDao freqWashDao;


    @Override
    public void createFreqWash(String owner, String petName, String date, FreqWashEntity freqWashEntity) {
        freqWashDao.createFreqWash(owner, petName, date, freqWashEntity);
    }

    @Override
    public void deleteAllFreqWashs(String owner, String petName) throws DatabaseAccessException {
        freqWashDao.deleteAllFreqWashs(owner, petName);
    }

    @Override
    public void deleteFreqWashByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        freqWashDao.deleteFreqWashByDate(owner, petName, petDate);
    }

    @Override
    public FreqWashEntity getFreqWashByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        return freqWashDao.getFreqWashByDate(owner, petName, petDate);
    }

    @Override
    public List<Map<String, Object>> getAllFreqWash(String owner, String petName) throws DatabaseAccessException {
        return freqWashDao.getAllFreqWash(owner, petName);
    }

    @Override
    public List<Map<String, Object>> getAllFreqWashsBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        return freqWashDao.getAllFreqWashsBetween(owner, petName, initialDate, finalDate);
    }

    @Override
    public void updateFreqWash(String owner, String petName, String petDate, Object value)
        throws DatabaseAccessException {
        freqWashDao.updateFreqWash(owner, petName, petDate, value);
    }
}
