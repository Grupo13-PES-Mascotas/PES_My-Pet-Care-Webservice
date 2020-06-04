package org.pesmypetcare.webservice.entity.medalmanager;

import lombok.Data;

import java.util.List;

/**
 * @author Oriol Catalán
 */
@Data
public class Medal {
    private String name;
    private List<Double> levels;
    private String description;
    private byte[] medalIcon;

    public Medal() { }

    public Medal(MedalEntity medal) {
        this.name = medal.getName();
        this.levels = medal.getLevels();
        this.description = medal.getDescription();
        if (medal.getMedalIcon() != null) {
            this.medalIcon = medal.getMedalIcon().toBytes();
        } else {
            medalIcon = null;
        }
    }
}
