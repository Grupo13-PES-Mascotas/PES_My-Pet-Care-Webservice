package org.pesmypetcare.webservice.entity;

import lombok.Data;

import java.util.List;

@Data
public class GroupEntity {
    private String name;
    private String creator;
    private String creationDate;
    private String icon;
    private String description;
    private List<String> tags;
    private List<String> forums;
    private List<String> members;
}
