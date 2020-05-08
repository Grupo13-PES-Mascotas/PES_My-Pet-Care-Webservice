package org.pesmypetcare.webservice.entity;

import lombok.Data;

/**
 * @author Oriol Catalán
 */
@Data
public class MedalEntity {
    public static final String PROGRESS = "progress";
    public static final String GOALS = "goals";
    public static final String CURRENT_GOAL = "currentGoal";
    public static final String COMPLETED_GOALS_DATE = "completedGoalsDate";
    public static final String MEDAL_DESCRIPTION = "medalDescription";
    private double progress;
    private double goals [];
    private double currentGoal;
    private String completedGoalsDate[];
    private String medalDescription;

    public MedalEntity() { }

    public MedalEntity(double progress, double goals[], double currentGoal, String completedGoalsDate[],
                       String medalDescription) {
        for (String date : completedGoalsDate) {
            checkDateFormat(date);
        }
        this.progress = progress;
        this.goals = goals;
        this.currentGoal = currentGoal;
        this.completedGoalsDate = completedGoalsDate;
        this.medalDescription = medalDescription;
    }

    /**
     * Checks that field has the correct format for a Medal simple attribute.
     * @param field Name of the attribute.
     */
    public static void checkSimpleField(String field) {
        if (!PROGRESS.equals(field) && !GOALS.equals(field) && !CURRENT_GOAL.equals(field)
            && !COMPLETED_GOALS_DATE.equals(field) && !MEDAL_DESCRIPTION.equals(field)) {
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
