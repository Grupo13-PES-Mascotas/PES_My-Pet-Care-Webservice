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


    private Map<String, MealEntity> meals;
    private Map<String, Integer> trainings;
    private Map<String, Integer> washes;
    private Map<String, Integer> weights;
    private Map<String, Map<String, MedicationEntity>> medications;
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

    public static void checkDateFormat(String date) {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}'T'\\d{2}:\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Incorrect date format");
        }
    }
}
