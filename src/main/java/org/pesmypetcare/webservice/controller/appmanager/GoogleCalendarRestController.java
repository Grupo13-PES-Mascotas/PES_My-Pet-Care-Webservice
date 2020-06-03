package org.pesmypetcare.webservice.controller.appmanager;
import org.pesmypetcare.webservice.entity.appmanager.EventEntity;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.service.appmanager.GoogleCalendarService;
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
    private static final String GOOGLE_TOKEN = "google-token";
    private static final String TOKEN = "token";

    @Autowired
    private GoogleCalendarService googleCalendarService;

    /**
     * Creates a Secondary Google Calendar in the account specified by the googleToken.
     * @param googleToken oauth2 token needed to access the Google Calendar
     * @param token Access token of the owner of the pet
     * @param petName Name of the pet the calendar is created for
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PostMapping("/{petName}")
    public void createSecondaryCalendar(@RequestHeader(GOOGLE_TOKEN) String googleToken, @RequestHeader(TOKEN) String token,
                                        @PathVariable String petName)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        googleCalendarService.createSecondaryCalendar(googleToken, token, petName);
    }

    /**
     * Deletes a Secondary Google Calendar in the account specified by the googleToken.
     * @param googleToken oauth2 token needed to access the Google Calendar
     * @param token Access token of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping("/{petName}")
    public void deleteSecondaryCalendar(@RequestHeader(GOOGLE_TOKEN) String googleToken, @RequestHeader(TOKEN) String token,
                                        @PathVariable String petName)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        googleCalendarService.deleteSecondaryCalendar(googleToken, token, petName);
    }

    /**
     * Returns all Calendar Events from a specified Calendar.
     * @param googleToken oauth2 token needed to access the Google Calendar
     * @param token Access token of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @return List containing all the Events from the specified Calendar
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/{petName}")
    public List<EventEntity> getAllEventsFromCalendar(@RequestHeader(GOOGLE_TOKEN) String googleToken,
                                                    @RequestHeader(TOKEN) String token,
                                         @PathVariable String petName)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        return googleCalendarService.getAllEventsFromCalendar(googleToken, token, petName);
    }

    /**
     * Creates an Event in a specified Google Calendar.
     * @param googleToken oauth2 token needed to access the Google Calendar
     * @param token Access token of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param eventEntity Event to create
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PostMapping("/event/{petName}")
    public void createEvent(@RequestHeader(GOOGLE_TOKEN) String googleToken, @RequestHeader(TOKEN) String token,
                            @PathVariable String petName, @RequestBody EventEntity eventEntity)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        googleCalendarService.createEvent(googleToken, token, petName, eventEntity);
    }

    /**
     * Retrieves an Event in a specified Google Calendar.
     * @param googleToken oauth2 token needed to access the Google Calendar
     * @param token Access token of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param body Body of the request containing the id of the event to retrieve with key eventId assigned
     * @return Event retrieved
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @GetMapping("/event/{petName}")
    public EventEntity retrieveEvent(@RequestHeader(GOOGLE_TOKEN) String googleToken, @RequestHeader(TOKEN) String token,
                                     @PathVariable String petName, @RequestBody Map<String, Object> body)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        return googleCalendarService.retrieveEvent(googleToken, token, petName, (String) body.get("eventId"));
    }

    /**
     * Updates an Event in a specified Google Calendar.
     * @param googleToken oauth2 token needed to access the Google Calendar
     * @param token Access token of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param eventEntity New Event that overwrites the past event with the same id
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @PutMapping("/event/{petName}")
    public void updateEvent(@RequestHeader(GOOGLE_TOKEN) String googleToken, @RequestHeader(TOKEN) String token,
                            @PathVariable String petName, @RequestBody EventEntity eventEntity)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        googleCalendarService.updateEvent(googleToken, token, petName, eventEntity);
    }

    /**
     * Deletes an Event in a specified Google Calendar.
     * @param googleToken oauth2 token needed to access the Google Calendar
     * @param token Access token of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param body Body of the request containing the id of the event to delete with key eventId assigned
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     * @throws DocumentException When the document does not exist
     */
    @DeleteMapping("/event/{petName}")
    public void deleteEvent(@RequestHeader(GOOGLE_TOKEN) String googleToken, @RequestHeader(TOKEN) String token,
                            @PathVariable String petName, @RequestBody Map<String, String> body)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        googleCalendarService.deleteEvent(googleToken, token, petName, body.get("eventId"));
    }

}
