package org.pesmypetcare.webservice.controller.appmanager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Marc Simó
 */
@RestController
@RequestMapping("/calendar")
public class GoogleCalendarRestController {
    private final String TOKEN = "token";

    /**
     * Creates a calendar
     */
    @PostMapping
    public void createCalendar(@RequestHeader(TOKEN) String token){
    }
}
