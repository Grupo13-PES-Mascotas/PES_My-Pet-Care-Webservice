package org.pesmypetcare.webservice.entity.communitymanager;

import lombok.Data;

import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@Data
public class Group extends GroupEntity {
    private Map<String, String> members;
}
