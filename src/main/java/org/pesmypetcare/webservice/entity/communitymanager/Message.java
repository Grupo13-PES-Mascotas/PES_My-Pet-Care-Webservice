package org.pesmypetcare.webservice.entity.communitymanager;

import lombok.Data;

/**
 * @author Santiago Del Rey
 */
@Data
public class Message {
    private String creator;
    private String text;
    private String encodedImage;
}
