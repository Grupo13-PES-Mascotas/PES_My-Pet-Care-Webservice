package org.pesmypetcare.webservice.entity;

import lombok.Data;

/**
 * @author Santiago Del Rey
 */
@Data
public class ImageEntity {
    private String uid;
    private String imgName;
    private byte[] img;
}
