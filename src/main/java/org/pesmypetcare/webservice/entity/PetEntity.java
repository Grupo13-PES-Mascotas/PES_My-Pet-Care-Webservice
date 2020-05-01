package org.pesmypetcare.webservice.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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
    private Map<String, Map<String, MedicationEntity>> medicaments;

    public static void checkKeyAndBody(String field, String key, Object body) {
    }

    public void initializeMaps() {
        meals = new HashMap<>();
        trainings = new HashMap<>();
        washes = new HashMap<>();
        weights = new HashMap<>();
        medicaments = new HashMap<>();
    }
}
