package org.pesmypetcare.webservice.entity;

import lombok.Data;

@Data
public class PetEntity {
    private GenderType gender;
    private String breed;
    private String birth;
    private Double weight;
    private String pathologies;
    private Double recommendedKcal;
    private int washFreq;
    private String profileImageLocation;
    private String calendarId;
}
