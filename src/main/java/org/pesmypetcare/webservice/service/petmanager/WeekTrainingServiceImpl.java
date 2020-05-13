package org.pesmypetcare.webservice.service.petmanager;


import org.pesmypetcare.webservice.dao.petmanager.WeekTrainingDao;
import org.pesmypetcare.webservice.entity.petmanager.WeekTrainingEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Oriol Catalán
 */
@Service
public class WeekTrainingServiceImpl implements WeekTrainingService {
    @Autowired
    private WeekTrainingDao weekTrainingDao;


    @Override
    public void createWeekTraining(String owner, String petName, String date, WeekTrainingEntity weekTrainingEntity) {
        weekTrainingDao.createWeekTraining(owner, petName, date, weekTrainingEntity);
    }

    @Override
    public void deleteAllWeekTrainings(String owner, String petName) throws DatabaseAccessException {
        weekTrainingDao.deleteAllWeekTrainings(owner, petName);
    }

    @Override
    public void deleteWeekTrainingByDate(String owner, String petName, String petDate) throws DatabaseAccessException {
        weekTrainingDao.deleteWeekTrainingByDate(owner, petName, petDate);
    }

    @Override
    public WeekTrainingEntity getWeekTrainingByDate(String owner, String petName, String petDate)
        throws DatabaseAccessException {
        return weekTrainingDao.getWeekTrainingByDate(owner, petName, petDate);
    }

    @Override
    public List<Map<String, Object>> getAllWeekTraining(String owner, String petName) throws DatabaseAccessException {
        return weekTrainingDao.getAllWeekTraining(owner, petName);
    }

    @Override
    public List<Map<String, Object>> getAllWeekTrainingsBetween(String owner, String petName, String initialDate,
                                                          String finalDate) throws DatabaseAccessException {
        return weekTrainingDao.getAllWeekTrainingsBetween(owner, petName, initialDate, finalDate);
    }

    @Override
    public void updateWeekTraining(String owner, String petName, String petDate, Object value)
        throws DatabaseAccessException {
        weekTrainingDao.updateWeekTraining(owner, petName, petDate, value);
    }
}
