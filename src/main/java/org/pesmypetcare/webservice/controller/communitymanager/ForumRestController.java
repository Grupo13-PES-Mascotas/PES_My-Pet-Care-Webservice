package org.pesmypetcare.webservice.controller.communitymanager;

import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.Message;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.service.communitymanager.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@RestController
@RequestMapping("/community")
public class ForumRestController {
    @Autowired
    private ForumService service;

    /**
     * Creates a forum.
     *
     * @param parentGroup The parent group name
     * @param forumEntity The forum entity with the forum data
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the group does not exist
     */
    @PostMapping("/{parentGroup}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createForum(@PathVariable String parentGroup, @RequestBody ForumEntity forumEntity)
        throws DatabaseAccessException, DocumentException {
        service.createForum(parentGroup, forumEntity);
    }

    /**
     * Deletes a forum.
     *
     * @param parentGroup The parent group name
     * @param forum The forum name
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the group does not exist
     */
    @DeleteMapping("/{parentGroup}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteForum(@PathVariable String parentGroup, @RequestParam String forum)
        throws DatabaseAccessException, DocumentException {
        service.deleteForum(parentGroup, forum);
    }

    /**
     * Gets all the forums from a group if request param forum not specified, otherwise returns the specified forum.
     *
     * @param parentGroup The parent group name
     * @param forum The forum name
     * @return A list of all the forums in a group or the forum requested
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the group does not exist
     */
    @GetMapping("/{parentGroup}")
    @ResponseBody
    public Object getForums(@PathVariable String parentGroup, @RequestParam(defaultValue = "all") String forum)
        throws DatabaseAccessException, DocumentException {
        if ("all".equals(forum)) {
            return service.getAllForumsFromGroup(parentGroup);
        }
        return service.getForum(parentGroup, forum);
    }

    /**
     * Updates a forum's name.
     *
     * @param parentGroup The parent group name
     * @param forum The fourm name
     * @param newName The new name
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the group does not exist
     */
    @PutMapping("/{parentGroup}/{forum}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateName(@PathVariable String parentGroup, @PathVariable String forum, @RequestParam String newName)
        throws DatabaseAccessException, DocumentException {
        service.updateName(parentGroup, forum, newName);
    }

    /**
     * Updates the list of tags of a forum.
     *
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param tags A map with the lists of new tags and the deleted ones
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When the group does not exist
     */
    @PutMapping("/tags/{parentGroup}/{forumName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTags(@PathVariable String parentGroup, @PathVariable String forumName,
                           @RequestBody Map<String, List<String>> tags)
        throws DatabaseAccessException, DocumentException {
        service.updateTags(parentGroup, forumName, tags.get("new"), tags.get("deleted"));
    }

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
    @PostMapping("/{parentGroup}/{forumName}")
    @ResponseStatus(HttpStatus.CREATED)
    public void postMessage(@RequestHeader String token, @PathVariable String parentGroup,
                            @PathVariable String forumName, @RequestBody Message message)
        throws DatabaseAccessException, DocumentException {
        service.postMessage(token, parentGroup, forumName, message);
    }

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
    @DeleteMapping("/{parentGroup}/{forumName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@RequestHeader String token, @PathVariable String parentGroup,
                              @PathVariable String forumName, @RequestParam String creator, @RequestParam String date)
        throws DatabaseAccessException, DocumentException {
        service.deleteMessage(token, parentGroup, forumName, creator, date);
    }

    /**
     * Adds or remove a like to a message of a forum.
     *
     * @param token The user's personal access token
     * @param username The user's username
     * @param parentGroup The parent group name
     * @param forumName The forum name
     * @param creator The creator's name
     * @param date The publication date of the message
     * @param like If true adds a like else removes it
     * @throws DatabaseAccessException When the retrieval is interrupted or the execution fails
     * @throws DocumentException When either the group or forum do not exist
     */
    @PutMapping("/{parentGroup}/{forumName}/messages")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void likeMessage(@RequestHeader String token, @PathVariable String parentGroup,
                            @PathVariable String forumName, @RequestParam String username, @RequestParam String creator,
                            @RequestParam String date, @RequestParam boolean like) throws DatabaseAccessException,
        DocumentException {
        if (like) {
            service.addUserToLikedByOfMessage(token, parentGroup, forumName, username, creator, date);
        } else {
            service.removeUserFromLikedByOfMessage(token, username, parentGroup, forumName, creator, date);
        }
    }
}
