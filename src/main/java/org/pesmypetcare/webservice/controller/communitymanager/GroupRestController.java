package org.pesmypetcare.webservice.controller.communitymanager;

import org.pesmypetcare.webservice.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupRestController {
    @Autowired
    private GroupService service;
}
