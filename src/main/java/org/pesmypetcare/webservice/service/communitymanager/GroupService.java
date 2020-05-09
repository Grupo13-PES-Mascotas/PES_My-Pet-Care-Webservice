
package org.pesmypetcare.webservice.service.communitymanager;
import org.pesmypetcare.webservice.entity.communitymanager.Group;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.entity.communitymanager.TagEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;
import java.util.Map;

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
     * @return The group
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    Group getGroup(String name) throws DatabaseAccessException;

    /**
     * Gets all groups in database.
     * @return A list with all the groups and its names
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Group> getAllGroups() throws DatabaseAccessException;

    /**
     * Updates a group field.
     * @param name The group name
     * @param field The field to update
     * @param newValue The new field value
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateField(String name, String field, String newValue) throws DatabaseAccessException;

    /**
     * Subscribes a user to a group.
     *
     * @param token The personal access token of the user
     * @param group The group name
     * @param username The user's username
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void subscribe(String token, String group, String username) throws DatabaseAccessException;

    /**
     * Updates the group tags.
     *
     * @param group The group name
     * @param newTags The new tags
     * @param deletedTags The deleted tags
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateTags(String group, List<String> newTags, List<String> deletedTags) throws DatabaseAccessException;

    /**
     * Gets all the existent tags.
     *
     * @return The tags and the groups that have them
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    Map<String, TagEntity> getAllTags() throws DatabaseAccessException;

    /**
     * Unsubscribes a user to a group.
     *
     * @param token The personal access token of the user
     * @param group The group name
     * @param username The user's username
     * @throws DatabaseAccessException If an error occurs when accessing or modifying the database
     */
    void unsubscribe(String token, String group, String username) throws DatabaseAccessException;

    /**
     * Checks whether a group name is already in use or not.
     * @param name The group name
     * @return True if the name is being used
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    boolean groupNameInUse(String name) throws DatabaseAccessException;
}
