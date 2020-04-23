package org.pesmypetcare.webservice.dao.communitymanager;

import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.MessageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
public interface ForumDao {
    boolean forumNameInUse(String parentGroup, String forumName) throws DatabaseAccessException;
    void createForum(String parentGroup, ForumEntity forumEntity) throws DatabaseAccessException;
    void deleteForum(String parentGroup, String forumName) throws DatabaseAccessException;
    ForumEntity getForum(String parentGroup, String forumName) throws DatabaseAccessException;
    List<ForumEntity> getAllForumsFromGroup(String parentGroup) throws DatabaseAccessException;
    void updateName(String parentGroup, String currentName, String newName) throws DatabaseAccessException;
    void updateTags(String parentGroup, String forumName, List<String> newTags, List<String> deletedTags) throws DatabaseAccessException;
    void postMessage(String parentGroup, String forumName, MessageEntity post) throws DatabaseAccessException;
    void deleteMessage(String parentGroup, String forumName, String creator, String date) throws DatabaseAccessException;
    //void subscribe(String parentGroup, String forumName, String username);
    //void unsubscribe(String parentGroup, String forumName, String username);
}
