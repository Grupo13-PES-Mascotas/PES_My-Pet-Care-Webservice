package org.pesmypetcare.webservice.dao.communitymanager;

import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
public interface GroupDao {
    /**
     * Creates a group.
     * @param entity The group entity with the group data
     */
    void createGroup(GroupEntity entity);

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
     * @return A list with all the groups
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<GroupEntity> getAllGroups() throws DatabaseAccessException;

    /**
     * Updates a group field.
     * @param name     The group name
     * @param field    The field to update
     * @param newValue The new field value
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateField(String name, String field, Object newValue) throws DatabaseAccessException;

    /**
     * Checks whether a group name is already in use or not.
     * @param name The group name
     * @return True if the name is being used
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    boolean groupNameInUse(String name) throws DatabaseAccessException;
}
