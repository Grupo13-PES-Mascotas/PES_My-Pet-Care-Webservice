package org.pesmypetcare.webservice.controller.appmanager;
import com.google.api.services.calendar.model.Event;
import org.pesmypetcare.webservice.entity.EventEntity;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * @author Marc Simó
 */
@RestController
@RequestMapping("/calendar")
public class GoogleCalendarRestController {
    private static final String TOKEN = "token";

    @Autowired
    private GoogleCalendarService googleCalendarService;

    /**
     * Creates a Secondary Google Calendar in the account specified by the accessToken.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar is created for
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    @PostMapping("/{owner}/{petName}")
    public void createSecondaryCalendar(@RequestHeader(TOKEN) String accessToken, @PathVariable String owner,
                                 @PathVariable String petName) throws CalendarAccessException {
        googleCalendarService.createSecondaryCalendar(accessToken, owner, petName);
    }

    /**
     * Deletes a Secondary Google Calendar in the account specified by the accessToken.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/{owner}/{petName}")
    public void deleteSecondaryCalendar(@RequestHeader(TOKEN) String accessToken, @PathVariable String owner,
                                 @PathVariable String petName)
        throws CalendarAccessException, DatabaseAccessException{
        googleCalendarService.deleteSecondaryCalendar(accessToken, owner, petName);
    }

    /**
     * Returns all Calendar Events from a specified Calendar.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @return List containing all the Events from the specified Calendar
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/{owner}/{petName}")
    public List<Event> getAllEventsFromCalendar(@RequestHeader(TOKEN) String accessToken, @PathVariable String owner,
                                         @PathVariable String petName)
        throws CalendarAccessException, DatabaseAccessException{
        return googleCalendarService.getAllEventsFromCalendar(accessToken, owner, petName);
    }

    /**
     * Creates an Event in a specified Google Calendar.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param eventEntity Event to create
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PostMapping("/event/{owner}/{petName}")
    public void createEvent(@RequestHeader(TOKEN) String accessToken, @PathVariable String owner,
                     @PathVariable String petName, @RequestBody EventEntity eventEntity)
        throws CalendarAccessException, DatabaseAccessException{
        googleCalendarService.createEvent(accessToken, owner, petName, eventEntity);
    }

    /**
     * Retrieves an Event in a specified Google Calendar.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param body Body of the request containing the id of the event to retrieve with key eventId assigned
     * @return Event retrieved
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @GetMapping("/event/{owner}/{petName}")
    public EventEntity retrieveEvent(@RequestHeader(TOKEN) String accessToken, @PathVariable String owner,
                              @PathVariable String petName, @RequestBody Map<String, Object> body)
        throws CalendarAccessException, DatabaseAccessException{
        return googleCalendarService.retrieveEvent(accessToken, owner, petName, (String) body.get("eventId"));
    }

    /**
     * Updates an Event in a specified Google Calendar.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param eventEntity New Event that overwrites the past event with the same id
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @PutMapping("/event/{owner}/{petName}")
    public void updateEvent(@RequestHeader(TOKEN) String accessToken, @PathVariable String owner, @PathVariable String petName,
                     @RequestBody EventEntity eventEntity)
        throws CalendarAccessException, DatabaseAccessException {
        googleCalendarService.updateEvent(accessToken, owner, petName, eventEntity);
    }

    /**
     * Deletes an Event in a specified Google Calendar.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param body Body of the request containing the id of the event to delete with key eventId assigned
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    @DeleteMapping("/event/{owner}/{petName}")
    public void deleteEvent(@RequestHeader(TOKEN) String accessToken, @PathVariable String owner, @PathVariable String petName,
                     @RequestBody Map<String, Object> body) throws CalendarAccessException
        ,DatabaseAccessException {
        googleCalendarService.deleteEvent(accessToken, owner, petName, (String) body.get("eventId"));
    }

}
