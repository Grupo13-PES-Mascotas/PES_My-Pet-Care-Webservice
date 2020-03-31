package org.pesmypetcare.webservice.entity;

import lombok.Data;
import java.time.LocalTime;
import java.util.Date;

@Data
public class MealEntity {
    private Date date;
    private LocalTime hour;
    private String name;
    private Double kcal;
}
