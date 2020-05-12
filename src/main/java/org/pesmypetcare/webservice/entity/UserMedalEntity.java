package org.pesmypetcare.webservice.entity;

import lombok.Data;

/**
 * @author Oriol Catalán
 */
@Data
public class UserMedalEntity {
    public static final String PROGRESS = "progress";
    public static final String CURRENT_LEVEL = "currentLevel";
    public static final String COMPLETED_LEVELS_DATE = "completedLevelsDate";
    private double progress;
    private double currentLevel;
    private String completedLevelsDate[];

    public UserMedalEntity() { }

    public UserMedalEntity(double progress, double currentLevel, String completedLevelsDate[]) {
        for (String date : completedLevelsDate) {
            checkDateFormat(date);
        }
        this.progress = progress;
        this.currentLevel = currentLevel;
        this.completedLevelsDate = completedLevelsDate;
    }

    /**
     * Checks that field has the correct format for a Medal simple attribute.
     * @param field Name of the attribute.
     */
    public static void checkSimpleField(String field) {
        if (!PROGRESS.equals(field) && !CURRENT_LEVEL.equals(field)
            && !COMPLETED_LEVELS_DATE.equals(field)) {
            throw new IllegalArgumentException("Field does not exists");
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
