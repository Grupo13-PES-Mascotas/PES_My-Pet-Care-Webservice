package org.pesmypetcare.webservice.controller.communitymanager;

import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.entity.communitymanager.TagEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.communitymanager.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Santiago Del Rey
 */
@RestController
@RequestMapping("/community")
public class GroupRestController {
    @Autowired
    private GroupService service;

    /**
     * Creates a group.
     *
     * @param entity The group entity
     * @throws DatabaseAccessException If an error occurs when accessing or modifying the database
     */
    @PostMapping
    public void createGroup(@RequestBody GroupEntity entity) throws DatabaseAccessException {
        service.createGroup(entity);
    }

    /**
     * Deletes a group.
     *
     * @param group The group name
     * @throws DatabaseAccessException If an error occurs when accessing or modifying the database
     */
    @DeleteMapping
    public void deleteGroup(@RequestParam String group) throws DatabaseAccessException {
        service.deleteGroup(group);
    }

    /**
     * Gets a group if a name is specified, otherwise gets all the groups.
     *
     * @param group The group name
     * @return The requested group entity or all the groups if none is specified
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping
    public Object getGroup(@RequestParam(defaultValue = "all") String group) throws DatabaseAccessException {
        if ("all".equals(group)) {
            return service.getAllGroups();
        }
        return service.getGroup(group);
    }

    /**
     * Updates a group field.
     *
     * @param group The group name
     * @param field The field to update
     * @param updateValue The new value stored in a map
     * @throws DatabaseAccessException If an error occurs when accessing or modifying the database
     */
    @PutMapping
    public void updateField(@RequestParam String group, @RequestParam String field,
                            @RequestBody Map<String, String> updateValue) throws DatabaseAccessException {
        if (!("name".equals(field) || "description".equals(field))) {
            throw new IllegalArgumentException("Field must be either name or description");
        }
        service.updateField(group, field, updateValue.get("value"));
    }

    /**
     * Gets all the existent tags.
     *
     * @return The tags and the groups that have them
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/tags")
    public Map<String, TagEntity> getAllTags() throws DatabaseAccessException {
        return service.getAllTags();
    }

    /**
     * Updates a the group tags.
     *
     * @param group The group name
     * @param updateValue The new value stored in a map
     * @throws DatabaseAccessException If an error occurs when accessing or modifying the database
     */
    @PutMapping("/tags")
    public void updateTags(@RequestParam String group, @RequestBody Map<String, List<String>> updateValue)
        throws DatabaseAccessException {
        service.updateTags(group, updateValue.get("new"), updateValue.get("deleted"));
    }

    /**
     * Subscribes a user to a group.
     *
     * @param token The personal access token ofDocument the user
     * @param group The group name
     * @param username The user's username
     * @throws DatabaseAccessException If an error occurs when accessing or modifying the database
     */
    @PostMapping("/subscribe")
    public void subscribe(@RequestHeader("token") String token, @RequestParam String group,
                          @RequestParam String username) throws DatabaseAccessException {
        service.subscribe(token, group, username);
    }

    /**
     * Unsubscribes a user to a group.
     *
     * @param token The personal access token ofDocument the user
     * @param group The group name
     * @param username The user's username
     * @throws DatabaseAccessException If an error occurs when accessing or modifying the database
     */
    @DeleteMapping("/unsubscribe")
    public void unsubscribe(@RequestHeader("token") String token, @RequestParam String group,
                            @RequestParam String username) throws DatabaseAccessException {
        service.unsubscribe(token, group, username);
    }

    /**
     * Checks if a group exists.
     *
     * @param group The group name
     * @return True if the group exists
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/groups")
    public Map<String, Boolean> existsGroup(@RequestParam String group) throws DatabaseAccessException {
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("exists", service.groupNameInUse(group));
        return resp;
    }
}
