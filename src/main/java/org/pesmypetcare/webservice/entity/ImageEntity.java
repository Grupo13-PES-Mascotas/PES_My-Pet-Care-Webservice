package org.pesmypetcare.webservice.entity;

import lombok.Data;

@Data
public class ImageEntity {
    private String uid;
    private String imgName;
    private byte[] img;
}
