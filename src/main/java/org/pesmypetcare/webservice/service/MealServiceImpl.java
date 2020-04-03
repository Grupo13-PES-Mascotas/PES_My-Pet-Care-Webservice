package org.pesmypetcare.webservice.service;

import org.pesmypetcare.webservice.dao.MealDao;
import org.pesmypetcare.webservice.entity.MealEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MealServiceImpl implements MealService {
    @Autowired
    private MealDao mealDao;

    @Override
    public void createMeal(String owner, String petName, String date, MealEntity meal) {
        mealDao.createMeal(owner, petName, date, meal);
    }

    @Override
    public void deleteByDate(String owner, String petName, String date) {
        mealDao.deleteByDate(owner, petName, date);
    }

    @Override
    public void deleteAllMeals(String owner, String petName) throws DatabaseAccessException {
        mealDao.deleteAllMeals(owner, petName);
    }

    @Override
    public MealEntity getMealData(String owner, String petName, String date) throws DatabaseAccessException {
        return mealDao.getMealData(owner, petName, date);
    }

    @Override
    public List<Map<String, Object>> getAllMealData(String owner, String petName) throws DatabaseAccessException {
        return mealDao.getAllMealData(owner, petName);
    }

    @Override
    public List<Map<String, Object>> getAllMealsBetween(String owner, String petName, String initialDate,
                                                        String finalDate) throws DatabaseAccessException {
        return mealDao.getAllMealsBetween(owner, petName, initialDate, finalDate);
    }

    @Override
    public Object getMealField(String owner, String petName, String date, String field) throws DatabaseAccessException {
        return mealDao.getMealField(owner, petName, date, field);
    }

    @Override
    public void updateMealField(String owner, String petName, String date, String field, Object value) {
        mealDao.updateMealField(owner, petName, date, field, value);
    }
}
