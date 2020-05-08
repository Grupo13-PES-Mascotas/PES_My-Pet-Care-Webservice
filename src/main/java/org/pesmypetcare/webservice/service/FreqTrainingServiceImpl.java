package org.pesmypetcare.webservice.service;


import org.pesmypetcare.webservice.dao.FreqTrainingDao;
import org.pesmypetcare.webservice.entity.FreqTrainingEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
@Service
public class FreqTrainingServiceImpl implements FreqTrainingService {
    @Autowired
    private FreqTrainingDao freqTrainingDao;


    @Override
    public void createFreqTraining(String owner, String petName, String date, FreqTrainingEntity freqTrainingEntity) {
        freqTrainingDao.createFreqTraining(owner, petName, date, freqTrainingEntity);
    }

    @Override
    public void deleteAllFreqTrainings(String owner, String petName) throws DatabaseAccessException {
        freqTrainingDao.deleteAllFreqTrainings(owner, petName);
    }

    @Override
    public void deleteFreqTrainingByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        freqTrainingDao.deleteFreqTrainingByDate(owner, petName, petDate);
    }

    @Override
    public FreqTrainingEntity getFreqTrainingByDate(String owner, String petName, String petDate)
        throws DatabaseAccessException {
        return freqTrainingDao.getFreqTrainingByDate(owner, petName, petDate);
    }

    @Override
    public List<Map<String, Object>> getAllFreqTraining(String owner, String petName) throws DatabaseAccessException {
        return freqTrainingDao.getAllFreqTraining(owner, petName);
    }

    @Override
    public List<Map<String, Object>> getAllFreqTrainingsBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        return freqTrainingDao.getAllFreqTrainingsBetween(owner, petName, initialDate, finalDate);
    }

    @Override
    public void updateFreqTraining(String owner, String petName, String petDate, Object value)
        throws DatabaseAccessException {
        freqTrainingDao.updateFreqTraining(owner, petName, petDate, value);
    }
}
