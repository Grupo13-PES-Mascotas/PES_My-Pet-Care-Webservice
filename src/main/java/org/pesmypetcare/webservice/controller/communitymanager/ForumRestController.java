package org.pesmypetcare.webservice.controller.communitymanager;

import org.pesmypetcare.webservice.entity.communitymanager.ForumEntity;
import org.pesmypetcare.webservice.entity.communitymanager.MessageEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.communitymanager.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/{parentGroup}")
    public void createForum(@PathVariable String parentGroup,
                            @RequestBody ForumEntity forumEntity) throws DatabaseAccessException {
        service.createForum(parentGroup, forumEntity);
    }

    @DeleteMapping("/{parentGroup}")
    public void deleteForum(@PathVariable String parentGroup,
                            @RequestParam String forum) throws DatabaseAccessException {
        service.deleteForum(parentGroup, forum);
    }

    @GetMapping("/{parentGroup}")
    public Object getForums(@PathVariable String parentGroup,
                            @RequestParam(defaultValue = "all") String forum) throws DatabaseAccessException {
        if ("all".equals(forum)) {
            return service.getAllForumsFromGroup(parentGroup);
        }
        return service.getForum(parentGroup, forum);
    }

    @PutMapping("/{parentGroup}/{forum}")
    public void updateName(@PathVariable String parentGroup, @PathVariable String forum,
                           @RequestParam String newName) throws DatabaseAccessException {
        service.updateName(parentGroup, forum, newName);
    }

    @PutMapping("/tags/{parentGroup}/{forumName}")
    public void updateTags(@PathVariable String parentGroup,
                           @PathVariable String forumName,
                           @RequestBody Map<String , List<String>> tags) throws DatabaseAccessException {
        service.updateTags(parentGroup, forumName, tags.get("new"), tags.get("deleted"));
    }

    @PostMapping("/{parentGroup}/{forumName}")
    public void postMessage(@RequestHeader String token, @PathVariable String parentGroup, @PathVariable String forumName,
                            @RequestBody MessageEntity post) throws DatabaseAccessException {
        service.postMessage(token, parentGroup, forumName, post);
    }

    /*@PostMapping("/{parentGroup}/subscribe")
    public void subscribe(@RequestHeader String token, @PathVariable String parentGroup, @RequestParam String forumName,
                          @RequestParam String username) throws DatabaseAccessException {
        service.subscribe(token, parentGroup, forumName, username);
    }

    @DeleteMapping("/{parentGroup}/subscribe")
    public void unsubscribe(@RequestHeader String token, @PathVariable String parentGroup, @RequestParam String forumName,
                            @RequestParam String username) throws DatabaseAccessException {
        service.unsubscribe(token, parentGroup, forumName, username);
    }*/
}
