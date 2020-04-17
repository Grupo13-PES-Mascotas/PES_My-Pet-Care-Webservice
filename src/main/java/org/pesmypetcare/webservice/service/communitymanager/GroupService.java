
package org.pesmypetcare.webservice.service.communitymanager;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
public interface GroupService {
    /**
     * Creates a group.
     * @param entity The group entity with the group data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void createGroup(GroupEntity entity) throws DatabaseAccessException;

    /**
     * Deletes a group by name.
     * @param name The group name.
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteGroup(String name) throws DatabaseAccessException;

    /**
     * Gets a group by its name.
     * @param name The group name
     * @return The group entity
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    GroupEntity getGroup(String name) throws DatabaseAccessException;

    /**
     * Gets all groups in database.
     * @return A list with all the groups and its names
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<GroupEntity> getAllGroups() throws DatabaseAccessException;

    /**
     * Updates a group field.
     * @param name The group name
     * @param field The field to update
     * @param newValue The new field value
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateField(String name, String field, Object newValue) throws DatabaseAccessException;
}
