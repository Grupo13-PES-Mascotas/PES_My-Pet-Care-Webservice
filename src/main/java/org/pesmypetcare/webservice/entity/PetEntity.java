package org.pesmypetcare.webservice.entity;

import lombok.Data;

import java.util.Map;

/**
 * @author Marc Simó
 */
@Data
public class PetEntity {
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
        if (!"gender".equals(field) && !"breed".equals(field) && !"birth".equals(field)
            && !"pathologies".equals(field) && !"needs".equals(field) && !"recommendedKcal".equals(field)) {
            throw new IllegalArgumentException("Field does not exists");
        }
    }

    /**
     * Checks that field has the correct format for a Pet collection attribute.
     * @param field Name of the attribute collection. Possible fields: meals, trainings, washes, weights
     */
    public static void checkCollectionField(String field) {
        if (!"meals".equals(field) && !"trainings".equals(field) && !"washes".equals(field)
            && !"weights".equals(field)) {
            throw new IllegalArgumentException("Field does not exists");
        }
    }

    /**
     * Checks that field, key and body have the correct format of a Pet attribute.
     * @param field Name of the attribute collection. Possible fields: meals, trainings, washes, weights
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkKeyAndBody(String field, String key, Map<String, Object> body) {
        switch (field) {
            case "meals":
                checkMeals(key, body);
                break;
            case "trainings":
            case "washes":
            case "weights":
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
