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
    private String medalIconPath;
}
