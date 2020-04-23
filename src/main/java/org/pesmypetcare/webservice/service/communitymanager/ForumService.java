package org.pesmypetcare.webservice.service.communitymanager;

import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.MessageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
public interface ForumService {
    void createForum(String parentGroup, ForumEntity forumEntity) throws DatabaseAccessException;
    void deleteForum(String parentGroup, String forumName) throws DatabaseAccessException;
    ForumEntity getForum(String parentGroup, String forumName) throws DatabaseAccessException;
    List<ForumEntity> getAllForumsFromGroup(String parentGroup) throws DatabaseAccessException;
    void updateName(String parentGroup, String currentName, String newName) throws DatabaseAccessException;
    void updateTags(String parentGroup, String forumName, List<String> newTags, List<String> deletedTags) throws DatabaseAccessException;
    void postMessage(String token, String parentGroup, String forumName, MessageEntity post) throws DatabaseAccessException;
    //void subscribe(String token, String parentGroup, String forumName, String username) throws DatabaseAccessException;
    //void unsubscribe(String token, String parentGroup, String forumName, String username) throws DatabaseAccessException;
}
