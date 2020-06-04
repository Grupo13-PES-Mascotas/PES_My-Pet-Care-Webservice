
package org.pesmypetcare.webservice.service.communitymanager;
import org.pesmypetcare.webservice.entity.communitymanager.Group;
import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.entity.communitymanager.TagEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.List;
import java.util.Map;

/**
 * @author Santiago Del Rey
 */
public interface GroupService {
    /**
     * Creates a group.
     *
     * @param token The user's personal access token
     * @param entity The group entity with the group data
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the group does not exist
     */
    void createGroup(String token, GroupEntity entity) throws DatabaseAccessException, DocumentException;

    /**
     * Deletes a group by name.
     *
     * @param token The user's personal access token
     * @param name The group name.
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the group does not exist
     */
    void deleteGroup(String token, String name) throws DatabaseAccessException, DocumentException;

    /**
     * Gets a group by its name.
     * @param name The group name
     * @return The group
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the group does not exist
     */
    Group getGroup(String name) throws DatabaseAccessException, DocumentException;

    /**
     * Gets all groups in database.
     * @return A list with all the groups and its names
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<Group> getAllGroups() throws DatabaseAccessException;

    /**
     * Updates a group field.
     *
     * @param token The user's personal access token
     * @param name The group name
     * @param field The field to update
     * @param newValue The new field value
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When updating the name to one that already exists
     */
    void updateField(String token, String name, String field, String newValue) throws DatabaseAccessException,
        DocumentException;

    /**
     * Subscribes a user to a group.
     *
     * @param token The personal access token of the user
     * @param group The group name
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the group does not exist
     */
    void subscribe(String token, String group) throws DatabaseAccessException, DocumentException;

    /**
     * Updates the group tags.
     *
     *
     * @param token The user's personal access token
     * @param group The group name
     * @param newTags The new tags
     * @param deletedTags The deleted tags
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the group does not exist
     */
    void updateTags(String token, String group, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException, DocumentException;

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
     * @throws DatabaseAccessException If an error occurs when accessing or modifying the database
     * @throws DocumentException When the group does not exist
     */
    void unsubscribe(String token, String group) throws DatabaseAccessException, DocumentException;

    /**
     * Checks whether a group name is already in use or not.
     * @param name The group name
     * @return True if the name is being used
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    boolean groupNameInUse(String name) throws DatabaseAccessException;
}
