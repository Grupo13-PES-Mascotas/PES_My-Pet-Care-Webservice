package org.pesmypetcare.webservice.entity.medalmanager;

import lombok.Data;

/**
 * @author Oriol Catalán
 */
@Data
public class MedalEntity {
    public static final String NAME = "name";
    public static final String LEVELS = "levels";
    public static final String DESCRIPTION = "description";
    public static final String ICON_LOCATION = "iconLocation";
    private String name;
    private Double [] levels;
    private String description;
    private String iconLocation;



    public MedalEntity() { }

    public MedalEntity(String name, Double [] levels, String description) {
        this.name = name;
        this.levels = levels;
        this.description = description;
        this.iconLocation = null;
    }

    /**
     * Checks that field has the correct format for a Medal simple attribute.
     * @param field Name of the attribute.
     */
    public static void checkSimpleField(String field) {
        if (!NAME.equals(field) && !LEVELS.equals(field) && !DESCRIPTION.equals(field)
            && !ICON_LOCATION.equals(field)) {
            throw new IllegalArgumentException("Field does not exists");
        }
    }
}
