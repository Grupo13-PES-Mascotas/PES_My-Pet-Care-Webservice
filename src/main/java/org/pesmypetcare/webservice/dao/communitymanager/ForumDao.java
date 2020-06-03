package org.pesmypetcare.webservice.dao.communitymanager;

import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.Message;
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
     * @throws DocumentException When either the group or forum do not exist
     */
    void deleteForum(String parentGroup, String forumName)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets a forum from a group.
     * @param parentGroup The group name
     * @param forumName The forum name
     * @return The forum entity
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    ForumEntity getForum(String parentGroup, String forumName) throws DatabaseAccessException, DocumentException;

    /**
     * Gets all forums from a group.
     * @param groupName The group name
     * @return A list of forum entities
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    List<ForumEntity> getAllForumsFromGroup(String groupName) throws DatabaseAccessException, DocumentException;

    /**
     * Updates a forum name.
     * @param parentGroup The parent group name
     * @param currentName The current name
     * @param newName The new name
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
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
     * @throws DocumentException When either the group or forum do not exist
     */
    void updateTags(String parentGroup, String forumName, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException, DocumentException;

    /**
     * Posts a message in a forum.
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param message The message to post
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    void postMessage(String parentGroup, String forumName, Message message)
        throws DatabaseAccessException, DocumentException;

    /**
     * Deletes a message from a forum.
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's username
     * @param date The message publication date
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    void deleteMessage(String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException;

    /**
     * Reports a message from a forum and sets it as banned when the number of reports is higher than 3.
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's username
     * @param reporter The reporter's username
     * @param date The message publication date
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    void reportMessage(String parentGroup, String forumName, String creator, String reporter, String date)
        throws DatabaseAccessException, DocumentException;

    /**
     * Unbans a message from a forum.
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's username
     * @param date The message publication date
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    void unbanMessage(String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets all images paths from all posts of a forum that have an image.
     * @param group The group name
     * @param forum The forum name
     * @return A list of paths to the posts images
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    List<String> getAllPostImagesPaths(String group, String forum) throws DatabaseAccessException, DocumentException;

    /**
     * Adds a user to the likedBy list of a message.
     * @param username The user's username
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's name
     * @param date The publication date of the message
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    void addUserToLikedByOfMessage(String username, String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException;

    /**
     * Deletes a user from the likedBy list of a message.
     * @param username The user's username
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's name
     * @param date The publication date of the message
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    void removeUserFromLikedByOfMessage(String username, String parentGroup, String forumName, String creator,
                                        String date) throws DatabaseAccessException, DocumentException;
}
