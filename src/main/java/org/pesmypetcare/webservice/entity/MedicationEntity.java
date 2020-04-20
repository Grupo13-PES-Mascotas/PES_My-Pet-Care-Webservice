package org.pesmypetcare.webservice.entity;
import lombok.Data;

@Data
public class MedicationEntity {
    private Double quantity;
    private int duration;
    private int periodicity;
}
