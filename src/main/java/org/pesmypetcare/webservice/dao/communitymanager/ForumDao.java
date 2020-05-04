package org.pesmypetcare.webservice.dao.communitymanager;

import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.MessageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
public interface ForumDao {
    /**
     * Checks if a forum name is already in use in a group.
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @return True if the forum name is already in use
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     */
    boolean forumNameInUse(String parentGroup, String forumName) throws DatabaseAccessException;

    /**
     * Creates a forum in the given group.
     * @param parentGroup The group name
     * @param forumEntity The forum entity
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the group does not exist
     */
    void createForum(String parentGroup, ForumEntity forumEntity) throws DatabaseAccessException, DocumentException;

    /**
     * Deletes a forum form the given group.
     * @param parentGroup The group name
     * @param forumName The forum name
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    void deleteForum(String parentGroup, String forumName)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets a forum from a group.
     * @param parentGroup The group name
     * @param forumName The forum name
     * @return The forum entity
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    ForumEntity getForum(String parentGroup, String forumName) throws DatabaseAccessException, DocumentException;

    /**
     * Gets all forums from a group.
     * @param groupName The group name
     * @return A list of forum entities
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     */
    List<ForumEntity> getAllForumsFromGroup(String groupName) throws DatabaseAccessException;

    /**
     * Updates a forum name.
     * @param parentGroup The parent group name
     * @param currentName The current name
     * @param newName The new name
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    void updateName(String parentGroup, String currentName, String newName)
        throws DatabaseAccessException, DocumentException;

    /**
     * Updates the tags of a group.
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param newTags The list of new tags
     * @param deletedTags The list of deleted tags
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    void updateTags(String parentGroup, String forumName, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException, DocumentException;

    /**
     * Posts a message in a forum.
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param messageEntity The message entity
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    void postMessage(String parentGroup, String forumName, MessageEntity messageEntity)
        throws DatabaseAccessException, DocumentException;

    /**
     * Deletes a message from a forum.
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's username
     * @param date The message publication date
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    void deleteMessage(String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException;
}
