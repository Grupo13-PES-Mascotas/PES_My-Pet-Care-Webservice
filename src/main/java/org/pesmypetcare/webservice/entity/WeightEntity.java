package org.pesmypetcare.webservice.entity;

import lombok.Data;
import java.util.Date;

@Data
public class WeightEntity {
    private Date weightDate;
    private double weightValue;
}
