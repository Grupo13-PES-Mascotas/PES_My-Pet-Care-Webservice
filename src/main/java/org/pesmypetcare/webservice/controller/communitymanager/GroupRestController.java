package org.pesmypetcare.webservice.controller.communitymanager;

import org.pesmypetcare.webservice.service.communitymanager.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Santiago Del Rey
 */
@RestController
public class GroupRestController {
    @Autowired
    private GroupService service;
}
