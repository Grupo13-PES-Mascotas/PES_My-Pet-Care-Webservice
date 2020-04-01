package org.pesmypetcare.webservice.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PetEntity {
    private GenderType gender;
    private String breed;
    private String birth;
    private Double weight;
    private String pathologies;
    private Double recommendedKcal;
    private int washFreq;
}
