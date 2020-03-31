package org.pesmypetcare.webservice.dao;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.pesmypetcare.webservice.entity.MealEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.firebaseservice.FirebaseFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class MealDAOImpl implements MealDAO {
    private final String DELFAIL_KEY;
    private final String MEAL_DOES_NOT_EXIST_EXC;
    private final String INVALID_MEAL_EXC;

    Firestore db;

    public MealDAOImpl() {
        db =  FirebaseFactory.getInstance().getFirestore();

        DELFAIL_KEY = "deletion-failed";
        MEAL_DOES_NOT_EXIST_EXC = "The meal does not exist";
        INVALID_MEAL_EXC = "invalid-meal";
    }

    @Override
    public void createMeal(String owner, String petName, MealEntity meal) {
        String mealPK = getMealPK(meal.getDate(), meal.getHour());
        DocumentReference mealRef = getMealsRef(owner, petName).document(mealPK);
        mealRef.set(meal);
    }

    @Override
    public void deleteByDateAndHour(String owner, String petName, Date date, LocalTime hour) {
        String mealPK = getMealPK(date, hour);
        DocumentReference mealRef = getMealsRef(owner, petName).document(mealPK);
        mealRef.delete();
    }

    @Override
    public void deleteAllMeals(String owner, String petName) throws DatabaseAccessException {
        CollectionReference mealsRef = getMealsRef(owner, petName);
        try {
            ApiFuture<QuerySnapshot> future = mealsRef.get();
            List<QueryDocumentSnapshot> mealDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot mealDocument : mealDocuments) {
                mealDocument.getReference().delete();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
    }

    @Override
    public MealEntity getMealData(String owner, String petName, Date date, LocalTime hour) throws DatabaseAccessException {
        CollectionReference mealsRef = getMealsRef(owner, petName);
        DocumentReference mealRef = mealsRef.document(getMealPK(date, hour));
        ApiFuture<DocumentSnapshot> future = mealRef.get();
        DocumentSnapshot mealDoc;
        try {
            mealDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!mealDoc.exists()) {
            throw new DatabaseAccessException(INVALID_MEAL_EXC, MEAL_DOES_NOT_EXIST_EXC);
        }
        return mealDoc.toObject(MealEntity.class);
    }

    @Override
    public List<Map<String, Object>> getAllMealData(String owner, String petName) throws DatabaseAccessException {
        CollectionReference mealsRef = getMealsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = mealsRef.get();
            List<QueryDocumentSnapshot> mealDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot mealDocument : mealDocuments) {
                Map<String, Object> internalList = new HashMap<>();
                internalList.put("name", mealDocument.getId());
                internalList.put("body", mealDocument.toObject(MealEntity.class));
                externalList.add(internalList);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public List<Map<String, Object>> getAllMealsBetween(String owner, String petName, Date initialDate, LocalTime initialHour, Date finalDate, LocalTime finalHour) throws DatabaseAccessException {
        CollectionReference mealsRef = getMealsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = mealsRef.get();
            List<QueryDocumentSnapshot> mealDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot mealDocument : mealDocuments) {
                MealEntity mealEntity = mealDocument.toObject(MealEntity.class);
                if (initialHour.compareTo(mealEntity.getHour())<0 && finalHour.compareTo(mealEntity.getHour())>0 &&
                    initialDate.compareTo(mealEntity.getDate())<0 && finalDate.compareTo(mealEntity.getDate())>0) {
                    Map<String, Object> internalList = new HashMap<>();
                    internalList.put("name", mealDocument.getId());
                    internalList.put("body", mealEntity);
                    externalList.add(internalList);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public Object getMealField(String owner, String petName, Date date, LocalTime hour, String field) throws DatabaseAccessException {
        CollectionReference mealsRef = getMealsRef(owner, petName);
        DocumentReference mealRef = mealsRef.document(getMealPK(date, hour));
        ApiFuture<DocumentSnapshot> future = mealRef.get();
        DocumentSnapshot mealDoc;
        try {
            mealDoc = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        if (!mealDoc.exists()) {
            throw new DatabaseAccessException(INVALID_MEAL_EXC, MEAL_DOES_NOT_EXIST_EXC);
        }
        return mealDoc.get(field);
    }

    @Override
    public void updateMealField(String owner, String petName, Date date, LocalTime hour, String field, Object value) {
        CollectionReference mealsRef = getMealsRef(owner, petName);
        DocumentReference mealRef = mealsRef.document(getMealPK(date, hour));
        mealRef.update(field, value);
    }

    public CollectionReference getMealsRef(String owner, String petName) {
        return db.collection("users").document(owner).collection("pets").document(petName).collection("meals");
    }

    public String getMealPK(Date date, LocalTime hour) {
        return date.toString()+hour.toString();
    }
}
