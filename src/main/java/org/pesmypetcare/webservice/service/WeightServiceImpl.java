package org.pesmypetcare.webservice.service;


import org.pesmypetcare.webservice.entity.WeightEntity;
import org.pesmypetcare.webservice.dao.WeightDao;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
@Service
public class WeightServiceImpl implements WeightService {
    @Autowired
    private WeightDao weightDao;


    @Override
    public void createWeight(String owner, String petName, String date, WeightEntity weightEntity) {
        weightDao.createWeight(owner, petName, date, weightEntity);
    }

    @Override
    public void deleteAllWeights(String owner, String petName) throws DatabaseAccessException {
        weightDao.deleteAllWeights(owner, petName);
    }

    @Override
    public void deleteWeightByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        weightDao.deleteWeightByDate(owner, petName, petDate);
    }

    @Override
    public WeightEntity getWeightByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        return weightDao.getWeightByDate(owner, petName, petDate);
    }

    @Override
    public List<Map<String, Object>> getAllWeight(String owner, String petName) throws DatabaseAccessException {
        return weightDao.getAllWeight(owner, petName);
    }

    @Override
    public List<Map<String, Object>> getAllWeightsBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        return weightDao.getAllWeightsBetween(owner, petName, initialDate, finalDate);
    }

    @Override
    public void updateWeight(String owner, String petName, String petDate, Object value)
        throws DatabaseAccessException {
        weightDao.updateWeight(owner, petName, petDate, value);
    }
}
