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
    private byte[] medalIconPath;

    public Medal() { }

    public Medal(MedalEntity medal) {
        this.name = medal.getName();
        this.levels = medal.getLevels();
        this.description = medal.getDescription();
        this.medalIconPath = medal.getMedalIcon().toBytes();
    }
}
