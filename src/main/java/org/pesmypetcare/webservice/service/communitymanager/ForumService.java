package org.pesmypetcare.webservice.service.communitymanager;

import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.MessageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;

import java.util.List;

/**
 * @author Santiago Del Rey
 */
public interface ForumService {
    void createForum(String parentGroup, ForumEntity forumEntity) throws DatabaseAccessException, DocumentException;
    void deleteForum(String parentGroup, String forumName) throws DatabaseAccessException, DocumentException;
    ForumEntity getForum(String parentGroup, String forumName) throws DatabaseAccessException, DocumentException;
    List<ForumEntity> getAllForumsFromGroup(String parentGroup) throws DatabaseAccessException;
    void updateName(String parentGroup, String currentName, String newName)
        throws DatabaseAccessException, DocumentException;
    void updateTags(String parentGroup, String forumName, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException, DocumentException;
    void postMessage(String token, String parentGroup, String forumName, MessageEntity post)
        throws DatabaseAccessException, DocumentException;
    void deleteMessage(String token, String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException;
    //void subscribe(String token, String parentGroup, String forumName, String username) throws DatabaseAccessException;
    //void unsubscribe(String token, String parentGroup, String forumName, String username) throws DatabaseAccessException;
}
