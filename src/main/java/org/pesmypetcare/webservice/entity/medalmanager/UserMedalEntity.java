package org.pesmypetcare.webservice.entity.medalmanager;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oriol Catalán
 */
@Data
public class UserMedalEntity {
    public static final String NAME = "name";
    public static final String PROGRESS = "progress";
    public static final String CURRENT_LEVEL = "currentLevel";
    public static final String COMPLETED_LEVELS_DATE = "completedLevelsDate";
    private String name;
    private Double progress;
    private Double currentLevel;
    private List<String> completedLevelsDate;

    public UserMedalEntity() { }

    public UserMedalEntity(String name, Double progress, Double currentLevel, ArrayList<String> completedLevelsDate) {
        for (String date : completedLevelsDate) {
            checkDateFormat(date);
        }
        this.name = name;
        this.progress = progress;
        this.currentLevel = currentLevel;
        this.completedLevelsDate = completedLevelsDate;
    }

    /**
     * Checks that field has the correct format for a Medal simple attribute.
     * @param field Name of the attribute.
     */
    public static void checkSimpleField(String field) {
        if (!NAME.equals(field) && !PROGRESS.equals(field) && !CURRENT_LEVEL.equals(field)
            && !COMPLETED_LEVELS_DATE.equals(field)) {
            throw new IllegalArgumentException("Field does not exists");
        }
    }

    /**
     * Checks that the field and the new value for this field have the correct format for a UserMedal simple attribute.
     * @param field Name of the attribute.
     * @param newValue Value of the attribute.
     */
    public static void checkFieldAndValues(String field, Object newValue) {
        if ((field.equals(NAME) || field.equals(COMPLETED_LEVELS_DATE)) && !(newValue instanceof String)) {
            throw new IllegalArgumentException("New value must be a String");
        } else if ((field.equals(PROGRESS) || field.equals(CURRENT_LEVEL)) && !(newValue instanceof Double)) {
            throw new IllegalArgumentException("New value must be a Double");
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
}
