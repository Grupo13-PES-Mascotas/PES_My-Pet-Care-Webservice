package org.pesmypetcare.webservice.controller.communitymanager;

import org.pesmypetcare.webservice.entity.communitymanager.GroupEntity;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.communitymanager.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * @param name The group name
     * @throws DatabaseAccessException If an error occurs when accessing or modifying the database
     */
    @DeleteMapping
    public void deleteGroup(@RequestParam String name) throws DatabaseAccessException {
        service.deleteGroup(name);
    }

    /**
     * Gets a group if a name is specified, otherwise gets all the groups.
     *
     * @param name The group name
     * @return The requested group entity or all the groups if none is specified
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping
    public Object getGroup(@RequestParam(defaultValue = "all") String name) throws DatabaseAccessException {
        if ("all".equals(name)) {
            return service.getAllGroups();
        }
        return service.getGroup(name);
    }

    /**
     * Updates a group field.
     *
     * @param name The group name
     * @param field The field to update
     * @param updateValue The new value stored in a map
     * @throws DatabaseAccessException If an error occurs when accessing or modifying the database
     */
    @PutMapping
    public void updateField(@RequestParam String name, @RequestParam String field,
                            @RequestBody Map<String , Object> updateValue) throws DatabaseAccessException {
        service.updateField(name, field, updateValue.get("value"));
    }
}
