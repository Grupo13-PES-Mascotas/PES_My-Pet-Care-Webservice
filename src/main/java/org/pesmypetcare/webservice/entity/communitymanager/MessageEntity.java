package org.pesmypetcare.webservice.entity.communitymanager;

import lombok.Data;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
@Data
public class MessageEntity {
    private String creator;
    private String publicationDate;
    private String text;
    private String imagePath;
    private boolean banned;
    private List<String> likedBy;
}
