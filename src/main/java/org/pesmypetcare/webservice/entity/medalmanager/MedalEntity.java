package org.pesmypetcare.webservice.entity.medalmanager;

import lombok.Data;

import com.google.cloud.firestore.Blob;

import java.util.List;

/**
 * @author Oriol Catalán
 */
@Data
public class MedalEntity {
    public static final String NAME = "name";
    public static final String LEVELS = "levels";
    public static final String DESCRIPTION = "description";
    public static final String MEDAL_ICON = "medalIcon";
    private String name;
    private List<Double> levels;
    private String description;
    private Blob medalIcon;



    public MedalEntity() { }

    public MedalEntity(Medal medal) {
        this.name = medal.getName();
        this.levels = medal.getLevels();
        this.description = medal.getDescription();
        this.medalIcon = Blob.fromBytes(medal.getMedalIcon());
    }

    /**
     * Checks that field has the correct format for a Medal attribute.
     * @param field Name of the attribute.
     */
    public static void checkField(String field) {
        if (!NAME.equals(field) && !LEVELS.equals(field) && !DESCRIPTION.equals(field)
            && !MEDAL_ICON.equals(field)) {
            throw new IllegalArgumentException("Field does not exists");
        }
    }

}
