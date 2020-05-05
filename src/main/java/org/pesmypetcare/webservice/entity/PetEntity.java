package org.pesmypetcare.webservice.entity;

import lombok.Data;

import java.util.Map;

/**
 * @author Marc Simó
 */
@Data
public class PetEntity {
    public static final String MEALS = "meals";
    public static final String TRAININGS = "trainings";
    public static final String WASHES = "washes";
    public static final String WEIGHTS = "weights";
    public static final String GENDER = "gender";
    public static final String BIRTH = "birth";
    public static final String BREED = "breed";
    public static final String PATHOLOGIES = "pathologies";
    public static final String RECOMMENDED_KCAL = "recommendedKcal";
    public static final String NEEDS = "needs";
    private GenderType gender;
    private String breed;
    private String birth;
    private String pathologies;
    private String needs;
    private Double recommendedKcal;
    private String profileImageLocation;
    private String calendarId;

    public PetEntity() { }

    public PetEntity(GenderType gender, String breed, String birth, String pathologies, String needs,
                     Double recommendedKcal, String profileImageLocation, String calendarId) {
        checkDateFormat(birth);
        this.gender = gender;
        this.breed = breed;
        this.birth = birth;
        this.pathologies = pathologies;
        this.needs = needs;
        this.recommendedKcal = recommendedKcal;
        this.profileImageLocation = null;
        this.calendarId = null;
    }

    /**
     * Checks that field has the correct format for a Pet simple attribute.
     * @param field Name of the attribute.
     */
    public static void checkSimpleField(String field) {
        if (!GENDER.equals(field) && !BREED.equals(field) && !BIRTH.equals(field)
            && !PATHOLOGIES.equals(field) && !NEEDS.equals(field) && !RECOMMENDED_KCAL.equals(field)) {
            throw new IllegalArgumentException("Field does not exists");
        }
    }

    /**
     * Checks that the field and the new value for this field have the correct format for a Pet simple attribute.
     * @param field Name of the attribute.
     * @param newValue Value of the attribute.
     */
    public static void checkSimpleFieldAndValues(String field, Object newValue) {
        if ((field.equals(BIRTH) || field.equals(BREED) || (field.equals(PATHOLOGIES)
            || field.equals(NEEDS))) && !(newValue instanceof String)) {
            throw new IllegalArgumentException("New value must be a String");
        } else if (field.equals(RECOMMENDED_KCAL) && !(newValue instanceof Double)) {
            throw new IllegalArgumentException("New value must be a Double");
        } else if (field.equals(GENDER) && !(newValue instanceof GenderType)) {
            throw new IllegalArgumentException("New value must be a GenderType");
        }
    }

    /**
     * Checks that field has the correct format for a Pet collection attribute.
     * @param field Name of the attribute collection. Possible fields: meals, trainings, washes, weights
     */
    public static void checkCollectionField(String field) {
        if (!MEALS.equals(field) && !TRAININGS.equals(field) && !WASHES.equals(field)
            && !WEIGHTS.equals(field)) {
            throw new IllegalArgumentException("Field does not exists");
        }
    }

    /**
     * Checks that field, key and body have the correct format of a Pet attribute.
     * @param field Name of the attribute collection. Possible fields: meals, trainings, washes, weights
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkCollectionKeyAndBody(String field, String key, Map<String, Object> body) {
        switch (field) {
            case MEALS:
                checkMeals(key, body);
                break;
            case TRAININGS:
            case WASHES:
            case WEIGHTS:
                checkDateAndValueInteger(key, body);
                break;
            default:
                throw new IllegalArgumentException("Field does not exists");
        }
    }

    /**
     * Checks that key and body have the correct format for a meal.
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkMeals(String key, Map<String, Object> body) {
        checkDateFormat(key);
        if (body.size() != 2 || !body.containsKey("kcal") || !body.containsKey("mealName")) {
            throw new IllegalArgumentException("Request body does not have a correct format");
        }
        if (!(body.get("kcal") instanceof Double) || !(body.get("mealName") instanceof String)) {
            throw new IllegalArgumentException("Request body does not have a correct format");
        }
    }

    /**
     * Checks that key and body have the correct format for a date key and body with one element whose key is 'value'
     * and has an Object of type Integer.
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkDateAndValueInteger(String key, Map<String, Object> body) {
        checkDateFormat(key);
        if (body.size() != 1 || !body.containsKey("value")) {
            throw new IllegalArgumentException("Request body does not have a correct format");
        }
        if (!(body.get("value") instanceof Integer)) {
            throw new IllegalArgumentException("Request body does not have a correct format");
        }
    }

    /**
     * Checks that the string date follows the specified .
     * @param date String that contains a date
     */
    public static void checkDateFormat(String date) {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Incorrect date format");
        }
    }
}
