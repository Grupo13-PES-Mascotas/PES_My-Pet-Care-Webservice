package org.pesmypetcare.webservice.dao;

import org.pesmypetcare.webservice.entity.GroupEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;

public interface GroupDao {
    /**
     * Creates a group of forums.
     * @param entity The group entity with the group data
     */
    void createGroup(GroupEntity entity);

    /**
     * Deletes a group by name.
     * @param name The group name.
     */
    void deleteGroup(String name) throws DatabaseAccessException;

    /**
     * Gets a group by its name.
     * @param name The group name
     * @return The group entity
     */
    GroupEntity getGroup(String name) throws DatabaseAccessException;

    /**
     * Gets all groups in database.
     * @return A list with all the groups
     */
    List<GroupEntity> getAllGroups() throws DatabaseAccessException;

    /**
     * Updates a group field.
     * @param name     The group name
     * @param field    The field to update
     * @param newValue The new field value
     */
    void updateField(String name, String field, String newValue) throws DatabaseAccessException;

    /**
     * Checks whether a group name is already in use or not.
     * @param name The group name
     * @return True if the name is being used
     */
    boolean groupNameInUse(String name) throws DatabaseAccessException;
}
