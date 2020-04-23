package org.pesmypetcare.webservice.entity.petmanager;
import lombok.Data;

@Data
public class MedicationEntity {
    private double quantity;
    private int duration;
    private int periodicity;
}
