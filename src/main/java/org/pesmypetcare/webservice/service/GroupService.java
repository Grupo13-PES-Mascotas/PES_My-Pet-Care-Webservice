
package org.pesmypetcare.webservice.service;
import org.pesmypetcare.webservice.entity.GroupEntity;

import java.util.List;
import java.util.Map;

public interface GroupService {
    /**
     * Creates a group of forums.
     * @param name The group name
     * @param entity The group entity with the group data
     */
    void createGroup(String name, GroupEntity entity);

    /**
     * Deletes a group by name.
     * @param name The group name.
     */
    void deleteGroup(String name);

    /**
     * Gets a group by its name.
     * @param name The group name
     * @return The group entity
     */
    GroupEntity getGroup(String name);

    /**
     * Gets all groups in database.
     * @return A list with all the groups and its names
     */
    List<Map<String, Object>> getAllGroups();

    /**
     * Updates a group field.
     * @param name The group name
     * @param field The field to update
     * @param newValue The new field value
     */
    void updateField(String name, String field, String newValue);
}
