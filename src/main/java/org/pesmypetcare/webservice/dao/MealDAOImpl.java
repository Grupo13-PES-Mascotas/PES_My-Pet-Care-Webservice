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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    public void createMeal(String owner, String petName, String date, MealEntity meal) {
        DocumentReference mealRef = getMealsRef(owner, petName).document(date);
        mealRef.set(meal);
    }

    @Override
    public void deleteByDate(String owner, String petName, String date) {
        DocumentReference mealRef = getMealsRef(owner, petName).document(date);
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
    public MealEntity getMealData(String owner, String petName, String date) throws DatabaseAccessException {
        CollectionReference mealsRef = getMealsRef(owner, petName);
        DocumentReference mealRef = mealsRef.document(date);
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
                internalList.put("date", mealDocument.getId());
                internalList.put("body", mealDocument.toObject(MealEntity.class));
                externalList.add(internalList);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public List<Map<String, Object>> getAllMealsBetween(String owner, String petName, String initialDate,
                                                        String finalDate) throws DatabaseAccessException {
        CollectionReference mealsRef = getMealsRef(owner, petName);
        List<Map<String, Object>> externalList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            ApiFuture<QuerySnapshot> future = mealsRef.get();
            List<QueryDocumentSnapshot> mealDocuments = future.get().getDocuments();
            for (QueryDocumentSnapshot mealDocument : mealDocuments) {
                String date = mealDocument.getId();
                if (initialDate.compareTo(date)<0 && finalDate.compareTo(date)>0) {
                    Map<String, Object> internalList = new HashMap<>();
                    internalList.put("date", date);
                    internalList.put("body", mealDocument.toObject(MealEntity.class));
                    externalList.add(internalList);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new DatabaseAccessException(DELFAIL_KEY, e.getMessage());
        }
        return externalList;
    }

    @Override
    public Object getMealField(String owner, String petName, String date, String field) throws DatabaseAccessException {
        CollectionReference mealsRef = getMealsRef(owner, petName);
        DocumentReference mealRef = mealsRef.document(date);
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
    public void updateMealField(String owner, String petName, String date, String field, Object value) {
        CollectionReference mealsRef = getMealsRef(owner, petName);
        DocumentReference mealRef = mealsRef.document(date);
        mealRef.update(field, value);
    }

    public CollectionReference getMealsRef(String owner, String petName) {
        return db.collection("users").document(owner).collection("pets").document(petName).collection("meals");
    }
}
