package org.pesmypetcare.webservice.entity.petmanager;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Marc Simó
 */
@Data
public class PetEntity {
    public static final String GENDER = "gender";
    public static final String BIRTH = "birth";
    public static final String BREED = "breed";
    public static final String PATHOLOGIES = "pathologies";
    public static final String RECOMMENDED_KCAL = "recommendedKcal";
    public static final String NEEDS = "needs";
    public static final String MEALS = "meals";
    public static final String WEIGHTS = "weights";
    public static final String EXERCISES = "exercises";
    public static final String WASHES = "washes";
    public static final String VACCINATIONS = "vaccinations";
    public static final String ILLNESSES = "illnesses";
    public static final String MEDICATIONS = "medications";
    public static final String VET_VISITS = "vet_visits";
    private static final String DESCRIPTION = "description";
    private static final String COORDINATES = "coordinates";
    private static final String NAME = "name";
    private static final String END_DATE_TIME = "endDateTime";
    private static final String VALUE = "value";
    private static final String DURATION = "duration";
    private static final String QUANTITY = "quantity";
    private static final String FIELD_NOT_EXISTS = "Field does not exists";
    private static final String INCORRECT_BODY_FORMAT = "Request body does not have a correct format";
    private static final String MEAL_NAME = "mealName";
    private static final String KCAL = "kcal";
    private static final String INCORRECT_SEVERITY_FORMAT = "Incorrect severity format";
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
            throw new IllegalArgumentException(FIELD_NOT_EXISTS);
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
        } else if (field.equals(GENDER) && !"Male".equals(newValue) && !"Female".equals(newValue)
            && !"Other".equals(newValue)) {
            throw new IllegalArgumentException("New value must be a GenderType");
        }
    }

    /**
     * Checks that field has the correct format for a Pet collection attribute.
     * @param field Name of the attribute collection. Possible fields: meals, weights, exercises, washes,
     *              vaccinations, illnesses, medications, vet_visits
     */
    public static void checkCollectionField(String field) {
        if (!MEALS.equals(field) && !WEIGHTS.equals(field) && !EXERCISES.equals(field) && !WASHES.equals(field)
            && !VACCINATIONS.equals(field) && !ILLNESSES.equals(field) && !MEDICATIONS.equals(field)
            && !VET_VISITS.equals(field)) {
            throw new IllegalArgumentException(FIELD_NOT_EXISTS);
        }
    }

    /**
     * Checks that field and key have the correct format of a Pet attribute.
     * @param field Name of the attribute collection. Possible fields: meals, weights, exercises, washes,
     *      *              vaccinations, illnesses, medications, vet_visits
     * @param key Key of the attribute
     */
    public static void checkCollectionKey(String field, String key) {
        switch (field) {
            case MEALS:
            case WEIGHTS:
            case EXERCISES:
            case WASHES:
            case VACCINATIONS:
            case ILLNESSES:
            case VET_VISITS:
                checkDateFormat(key);
                break;
            case MEDICATIONS:
                checkDatePlusNameFormat(key);
                break;
            default:
                throw new IllegalArgumentException(FIELD_NOT_EXISTS);
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
            case WEIGHTS:
                checkDateAndValueInteger(key, body);
                break;
            case EXERCISES:
                checkExercises(key, body);
                break;
            case WASHES:
                checkWashes(key, body);
                break;
            case VACCINATIONS:
                checkVaccinations(key, body);
                break;
            case ILLNESSES:
                checkIllnesses(key, body);
                break;
            case MEDICATIONS:
                checkMedications(key, body);
                break;
            case VET_VISITS:
                checkVetVisits(key, body);
                break;
            default:
                throw new IllegalArgumentException(FIELD_NOT_EXISTS);
        }
    }

    /**
     * Checks that key and body have the correct format for a meal.
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkMeals(String key, Map<String, Object> body) {
        checkDateFormat(key);
        if (body.size() != 2 || !body.containsKey(KCAL) || !body.containsKey(MEAL_NAME)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
        if ((!(body.get(KCAL) instanceof Double) && !(body.get(KCAL) instanceof Integer))
            || !(body.get(MEAL_NAME) instanceof String)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
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
        if (body.size() != 1 || !body.containsKey(VALUE)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
        if (!(body.get(VALUE) instanceof Integer)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
    }

    /**
     * Checks that key and body have the correct format for an exercise.
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkExercises(String key, Map<String, Object> body) {
        checkDateFormat(key);
        if (body.size() != 4 || !body.containsKey(NAME) || !body.containsKey(DESCRIPTION)
            || !body.containsKey(END_DATE_TIME) || !body.containsKey(COORDINATES)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
        if ((!(body.get(NAME) instanceof String) && !(body.get(DESCRIPTION) instanceof String))
            || !(body.get(COORDINATES) instanceof List) || !(body.get(END_DATE_TIME) instanceof String)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
        checkDateFormat((String) body.get(END_DATE_TIME));
    }

    /**
     * Checks that key and body have the correct format for a wash.
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkWashes(String key, Map<String, Object> body) {
        checkDateFormat(key);
        if (body.size() != 2 || !body.containsKey(DESCRIPTION) || !body.containsKey(DURATION)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
        if (!(body.get(DESCRIPTION) instanceof String) || !(body.get(DURATION) instanceof Integer)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
    }

    /**
     * Checks that key and body have the correct format for a vaccination.
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkVaccinations(String key, Map<String, Object> body) {
        checkDateFormat(key);
        if (body.size() != 1 || !body.containsKey(DESCRIPTION)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
        if (!(body.get(DESCRIPTION) instanceof String)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
    }

    /**
     * Checks that key and body have the correct format for an illness.
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkIllnesses(String key, Map<String, Object> body) {
        checkDateFormat(key);
        String type = "type";
        String severity = "severity";
        if (body.size() != 4 || !body.containsKey(END_DATE_TIME) || !body.containsKey(type)
            || !body.containsKey(DESCRIPTION) || !body.containsKey(severity)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
        if (!(body.get(END_DATE_TIME) instanceof String) || !(body.get(type) instanceof String)
            || !(body.get(DESCRIPTION) instanceof String) || !(body.get(severity) instanceof String)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
        checkDateFormat((String) body.get(END_DATE_TIME));
        checkTypeValue((String) body.get(type));
        checkSeverityValue((String) body.get(severity));
    }

    /**
     * Checks that key and body have the correct format for a medication.
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkMedications(String key, Map<String, Object> body) {
        checkDatePlusNameFormat(key);
        String periodicity = "periodicity";
        if (body.size() != 3 || !body.containsKey(QUANTITY) || !body.containsKey(DURATION)
            || !body.containsKey(periodicity)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
        if ((!(body.get(QUANTITY) instanceof Double) && !(body.get(QUANTITY) instanceof Integer))
            || !(body.get(DURATION) instanceof Integer) || !(body.get(periodicity) instanceof Integer)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
    }

    /**
     * Checks that key and body have the correct format for a vet visit.
     * @param key Key of the attribute
     * @param body Body of the attribute
     */
    public static void checkVetVisits(String key, Map<String, Object> body) {
        checkDateFormat(key);
        String address = "address";
        String reason = "reason";
        if (body.size() != 2 || !body.containsKey(reason) || !body.containsKey(address)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
        if (!(body.get(reason) instanceof String) || !(body.get(address) instanceof String)) {
            throw new IllegalArgumentException(INCORRECT_BODY_FORMAT);
        }
    }

    /**
     * Checks wether the severity value is valid or not.
     * @param severity Severity value
     */
    private static void checkSeverityValue(String severity) {
        if (!"Low".equals(severity) && !"Medium".equals(severity) && !"High".equals(severity)) {
            throw new IllegalArgumentException(INCORRECT_SEVERITY_FORMAT);
        }
    }

    /**
     * Checks wether type value is valid or not.
     * @param type Type value
     */
    private static void checkTypeValue(String type) {
        if (!"Normal".equals(type) && !"Allergy".equals(type)) {
            throw new IllegalArgumentException(INCORRECT_SEVERITY_FORMAT);
        }
    }

    /**
     * Checks that the string date follows the specified format.
     * @param date String that contains a date
     */
    public static void checkDateFormat(String date) {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Incorrect date format");
        }
    }

    /**
     * Checks that the string key follows the specified format.
     * @param key String to checked
     */
    private static void checkDatePlusNameFormat(String key) {
        if (!key.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}-.*")) {
            throw new IllegalArgumentException("Incorrect date plus name format");
        }
    }
}
