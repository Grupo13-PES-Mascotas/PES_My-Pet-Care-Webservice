package org.pesmypetcare.webservice.entity;

import lombok.Data;
import java.util.Date;

@Data
public class PetEntity {
    private GenderType gender;
    private String breed;
    private Date birth;
    private Double weight;
    private String pathologies;
    private Double recommendedKcal;
    private int washFreq;
}
