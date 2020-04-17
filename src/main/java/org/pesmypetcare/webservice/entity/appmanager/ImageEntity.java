package org.pesmypetcare.webservice.entity.appmanager;

import lombok.Data;

@Data
public class ImageEntity {
    private String uid;
    private String imgName;
    private byte[] img;
}
