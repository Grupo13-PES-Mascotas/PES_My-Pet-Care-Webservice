package org.pesmypetcare.webservice.entity.communitymanager;

import lombok.Data;
import org.pesmypetcare.webservice.entity.appmanager.ImageEntity;

/**
 * @author Santiago Del Rey
 */
@Data
public class Message {
    private ImageEntity image;
    private MessageEntity message;
}
