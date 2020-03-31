package org.pesmypetcare.webservice.dao;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.pesmypetcare.webservice.entity.MealEntity;
import org.pesmypetcare.webservice.entity.PetEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class MealDAOImpl implements MealDAO {

    public MealDAOImpl() {
    }

    @Override
    public void createMeal(String owner, String petName, MealEntity meal) {
        String mealPK = meal.getDate().toString()+meal.getHour().toString();
        DocumentReference mealRef = getMealsRef(owner, petName).document(mealPK);
        mealRef.set(meal);
    }

    @Override
    public void deleteByDateAndHour(String owner, String petName, Date date, LocalTime hour) {
        String mealPK = date.toString()+hour.toString();
        DocumentReference mealRef = getMealsRef(owner, petName).document(mealPK);
        mealRef.delete();
    }

    @Override
    public void deleteAllMeals(String owner, String petName) throws DatabaseAccessException {

    }

    @Override
    public PetEntity getMealData(String owner, String petName, Date date, LocalTime hour) throws DatabaseAccessException {
        return null;
    }

    @Override
    public List<Map<String, Object>> getAllMealData(String owner, String petName) throws DatabaseAccessException {
        return null;
    }

    @Override
    public List<Map<String, Object>> getAllMealsBetween(String owner, String petName, Date initialDate, LocalTime initialHour, Date finalDate, LocalTime finalHour) throws DatabaseAccessException {
        return null;
    }

    @Override
    public Object getMealField(String owner, String petName, Date date, LocalTime hour, String field) throws DatabaseAccessException {
        return null;
    }

    @Override
    public void updateMealField(String owner, String petName, Date date, LocalTime hour, String field, Object value) {

    }

    public CollectionReference getMealsRef(String owner, String petName) {
        Firestore db = FirebaseFactory.getInstance().getFirestore();
        return db.collection("users").document(owner).collection("pets").document(petName).collection("meals");
    }
}
