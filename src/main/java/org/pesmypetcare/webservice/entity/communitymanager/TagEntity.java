package org.pesmypetcare.webservice.entity.communitymanager;

import lombok.Data;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
@Data
public class TagEntity {
    private List<String> groups;
    private List<String> forums;
}
