package org.pesmypetcare.webservice.service.communitymanager;

import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.Message;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.error.InvalidOperationException;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
public interface ForumService {
    /**
     * Creates a forum in the given group.
     *
     * @param token The creator's personal access token
     * @param parentGroup The group name
     * @param forumEntity The forum entity
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the group does not exist
     */
    void createForum(String token, String parentGroup, ForumEntity forumEntity)
        throws DatabaseAccessException, DocumentException;

    /**
     * Deletes a forum form the given group.
     *
     * @param token The creator's personal access token
     * @param parentGroup The group name
     * @param forumName The forum name
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    void deleteForum(String token, String parentGroup, String forumName)
        throws DatabaseAccessException, DocumentException;

    /**
     * Gets a forum from a group.
     *
     * @param parentGroup The group name
     * @param forumName The forum name
     * @return The forum entity
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    ForumEntity getForum(String parentGroup, String forumName) throws DatabaseAccessException, DocumentException;

    /**
     * Gets all forums from a group.
     *
     * @param groupName The group name
     * @return A list of forum entities
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    List<ForumEntity> getAllForumsFromGroup(String groupName) throws DatabaseAccessException, DocumentException;

    /**
     * Updates a forum name.
     *
     * @param token The user's personal access token
     * @param parentGroup The parent group name
     * @param currentName The current name
     * @param newName The new name
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    void updateName(String token, String parentGroup, String currentName, String newName)
        throws DatabaseAccessException, DocumentException;

    /**
     * Updates the tags of a group.
     *
     * @param token The user's personal access token
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param newTags The list of new tags
     * @param deletedTags The list of deleted tags
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    void updateTags(String token, String parentGroup, String forumName, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException, DocumentException;

    /**
     * Posts a message in a forum.
     *
     * @param token The user's personal access token
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param message The message to post
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    void postMessage(String token, String parentGroup, String forumName, Message message)
        throws DatabaseAccessException, DocumentException;

    /**
     * Deletes a message from a forum.
     *
     * @param token The user's personal access token
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's username
     * @param date The message publication date
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or the forum do not exist
     */
    void deleteMessage(String token, String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException;

    /**
     * Report a message from a forum.
     *
     * @param token The user's personal access token
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's username
     * @param date The message publication date
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     * @throws InvalidOperationException When the operation isn't allowed
     */
    void reportMessage(String token, String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException, InvalidOperationException;

    /**
     * Unbans a message from a forum.
     *
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's username
     * @param date The message publication date
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    void unbanMessage(String token, String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException, InvalidOperationException;


    /**
     * Adds a user to the likedBy list of a message.
     *
     * @param token The user's personal access token
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's name
     * @param date The publication date of the message
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    void addUserToLikedByOfMessage(String token, String parentGroup, String forumName, String creator,
                                   String date) throws DatabaseAccessException, DocumentException;

    /**
     * Deletes a user from the likedBy list of a message.
     *
     * @param token The user's personal access token
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's name
     * @param date The publication date of the message
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    void removeUserFromLikedByOfMessage(String token, String parentGroup, String forumName,
                                        String creator, String date) throws DatabaseAccessException, DocumentException;
}
