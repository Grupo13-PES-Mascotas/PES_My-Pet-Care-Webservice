package org.pesmypetcare.webservice.firebaseservice.firestore;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Santiago Del Rey
 */
class DocumentPathTest {
    private String groupId = "bYJzvJ0LTCUYgJIJEmx3";
    private String forumId = "6wOqBt6p0tzOeySyUqAJ";
    private String userId = "iw2VHtSHeoZohD3dAWRafXnb5x42";
    private String groupName = "Dogs";
    private String forumName = "Huskies";
    private String tag = "huskies";
    private String username = "John";
    private String petName = "Rex";

    @Test
    public void buildGroupPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.groups, groupId);
        assertEquals("groups/" + groupId, path);
    }

    @Test
    public void buildGroupNamePath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.groupsNames, groupName);
        assertEquals("groups_names/" + groupName, path);
    }

    @Test
    public void buildTagPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.tags, tag);
        assertEquals("tags/" + tag, path);
    }

    @Test
    public void buildUsernamePath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.usernames, username);
        assertEquals("used_usernames/" + username, path);
    }

    @Test
    public void buildUserPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.users, userId);
        assertEquals("users/" + userId, path);
    }

    @Test
    public void buildForumPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.forums, groupId, forumId);
        assertEquals("groups/" + groupId + "/forums/" + forumId, path);
    }

    @Test
    public void buildForumNamePath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.forumsNames, groupName, forumName);
        assertEquals("groups_names/" + groupName + "/forums/" + forumName, path);
    }

    @Test
    public void buildPetPath() throws WrongNumberArgsException {
        String path = DocumentPath.of(Collections.pets, userId, petName);
        assertEquals("users/" + userId + "/pets/" + petName, path);
    }
}
