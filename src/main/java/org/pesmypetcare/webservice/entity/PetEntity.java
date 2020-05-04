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

    public PetEntity(GenderType gender, String breed, String birth, String pathologies, String needs,
                     Double recommendedKcal, String profileImageLocation, String calendarId) {
        checkDateFormat(birth);
        this.gender = gender;
        this.breed = breed;
        this.birth = birth;
        this.pathologies = pathologies;
        this.needs = needs;
        this.recommendedKcal = recommendedKcal;
        this.profileImageLocation = profileImageLocation;
        this.calendarId = calendarId;
    }

    /**
     * Checks that field, key and body have the correct format of a Pet attribute.
     * @param field Name of the attribute collection. Possible fields: meals, trainings, washes, weights
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkKeyAndBody(String field, String key, Map<String, Object> body) {
        if (field.equals("meals")) {
            checkDateFormat(key);
            if ( body.size() != 2 || !body.containsKey("kcal") || !body.containsKey("mealName"))
                throw new IllegalArgumentException("Request body does not have a correct format");
            if (!(body.get("kcal") instanceof Double) || !(body.get("mealName") instanceof String))
                throw new IllegalArgumentException("Request body does not have a correct format");
        }
        else if (field.equals("trainings") || field.equals("washes") || field.equals("weights")) {
            checkDateFormat(key);
            if ( body.size() != 1 || !body.containsKey("value"))
                throw new IllegalArgumentException("Request body does not have a correct format");
            if (!(body.get("value") instanceof Integer))
                throw new IllegalArgumentException("Request body does not have a correct format");
        }
        else throw new IllegalArgumentException("Field does not exists");
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
