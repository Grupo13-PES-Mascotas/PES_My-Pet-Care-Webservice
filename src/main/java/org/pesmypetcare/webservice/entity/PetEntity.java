package org.pesmypetcare.webservice.entity;

import lombok.Data;

@Data
public class PetEntity {
    private GenderType gender;
    private String breed;
    private String birth;
    private String pathologies;
    private String needs;
    private Double recommendedKcal;
    private String profileImageLocation;
}
