package org.pesmypetcare.webservice.entity.medalmanager;

import com.google.cloud.firestore.Blob;
import lombok.Data;

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
    public static final String LEVELS = "levels";
    public static final String DESCRIPTION = "description";
    public static final String MEDAL_ICON = "medalIcon";
    private String name;
    private Double progress;
    private Double currentLevel;
    private List<String> completedLevelsDate;
    private List<Double> levels;
    private String description;
    private Blob medalIcon;

    public UserMedalEntity() { }

    public UserMedalEntity(String name, Double progress, Double currentLevel, List<String> completedLevelsDate,
                           Medal medal) {
        for (String date : completedLevelsDate) {
            checkDateFormat(date);
        }
        this.name = name;
        this.progress = progress;
        this.currentLevel = currentLevel;
        this.completedLevelsDate = completedLevelsDate;
        this.levels = medal.getLevels();
        this.description = medal.getDescription();
        if (medal.getMedalIconPath() != null) {
            this.medalIcon = Blob.fromBytes(medal.getMedalIconPath());
        }
    }

    /**
     * Checks that field has the correct format for a Medal attribute.
     * @param field Name of the attribute.
     */
    public static void checkField(String field) {
        if (!NAME.equals(field) && !PROGRESS.equals(field) && !CURRENT_LEVEL.equals(field)
            && !COMPLETED_LEVELS_DATE.equals(field) && !LEVELS.equals(field) && !DESCRIPTION.equals(field)
            && !MEDAL_ICON.equals(field)) {
            throw new IllegalArgumentException("Field does not exists");
        }
    }

    /**
     * Checks that the field and the new value for this field have the correct format for a UserMedal attribute.
     * @param field Name of the attribute.
     * @param newValue Value of the attribute.
     */
    public static void checkFieldAndValues(String field, Object newValue) {
        if ((field.equals(NAME) || field.equals(COMPLETED_LEVELS_DATE) || field.equals(DESCRIPTION))
            && !(newValue instanceof String)) {
            throw new IllegalArgumentException("New value must be a String");
        } else if ((field.equals(PROGRESS) || field.equals(CURRENT_LEVEL) || field.equals(LEVELS))
            && !(newValue instanceof Double || newValue instanceof Integer)) {
            throw new IllegalArgumentException("New value must be a Double or Integer");
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
