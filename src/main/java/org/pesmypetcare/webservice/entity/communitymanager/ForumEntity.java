package org.pesmypetcare.webservice.entity.communitymanager;

import lombok.Data;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
@Data
public class ForumEntity {
    private String name;
    private String creator;
    private String creationDate;
    private List<String> tags;
}
