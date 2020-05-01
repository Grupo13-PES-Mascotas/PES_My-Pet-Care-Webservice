package org.pesmypetcare.webservice.entity;
import lombok.Data;

/**
 * @author Álvaro Trius
 */
@Data
public class MedicationEntity {
    private double quantity;
    private int duration;
    private int periodicity;
}
