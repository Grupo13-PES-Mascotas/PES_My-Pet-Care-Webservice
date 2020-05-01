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
    boolean forumNameInUse(String parentGroup, String forumName) throws DatabaseAccessException, DocumentException;
    void createForum(String parentGroup, ForumEntity forumEntity) throws DatabaseAccessException, DocumentException;
    void deleteForum(String parentGroup, String forumName) throws DatabaseAccessException, DocumentException;
    ForumEntity getForum(String parentGroup, String forumName) throws DatabaseAccessException, DocumentException;
    List<ForumEntity> getAllForumsFromGroup(String parentGroup) throws DatabaseAccessException;
    void updateName(String parentGroup, String currentName, String newName)
        throws DatabaseAccessException, DocumentException;
    void updateTags(String parentGroup, String forumName, List<String> newTags, List<String> deletedTags)
        throws DatabaseAccessException, DocumentException;
    void postMessage(String parentGroup, String forumName, MessageEntity post)
        throws DatabaseAccessException, DocumentException;
    void deleteMessage(String parentGroup, String forumName, String creator, String date)
        throws DatabaseAccessException, DocumentException;
    //void subscribe(String parentGroup, String forumName, String username);
    //void unsubscribe(String parentGroup, String forumName, String username);
}
