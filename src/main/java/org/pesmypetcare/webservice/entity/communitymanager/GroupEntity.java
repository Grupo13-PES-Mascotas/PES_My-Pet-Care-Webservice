package org.pesmypetcare.webservice.entity.communitymanager;

import lombok.Data;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
@Data
public class GroupEntity {
    private String name;
    private String creator;
    private String creationDate;
    private String icon;
    private String description;
    private List<String> tags;

    public GroupEntity() {
    }

    public GroupEntity(String name, String creator, String description, List<String> tags) {
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.tags = tags;
    }
}
