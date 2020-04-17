package org.pesmypetcare.webservice.service;

import com.google.api.services.calendar.model.Event;
import org.pesmypetcare.webservice.entity.EventEntity;
import org.pesmypetcare.webservice.error.CalendarAccessException;

import java.util.List;

/**
 * @author Marc Simó
 */
public interface GoogleCalendarService {

    /**
     * Creates a Secondary Google Calendar in the account specified by the accessToken
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar is created for
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    void createSecondaryCalendar(String accessToken, String owner, String petName) throws CalendarAccessException;

    /**
     * Creates a Secondary Google Calendar in the account specified by the accessToken
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param petName Name of the pet the calendar belongs to
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    void deleteSecondaryCalendar(String accessToken, String petName) throws CalendarAccessException;

    /**
     * Returns all Calendar Events from a specified Calendar
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param petName Name of the pet the calendar belongs to
     * @return List containing all the Events from the specified Calendar
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    List<Event> getAllEventsFromCalendar(String accessToken, String petName) throws CalendarAccessException;

    /**
     * Creates an Event in a specified Google Calendar
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param calendarId Id of the calendar where the event is created
     * @param event Event to create
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    void createEvent(String accessToken, String calendarId, EventEntity event) throws CalendarAccessException;

    /**
     * Retrieves an Event in a specified Google Calendar
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param calendarId Id of the calendar where the event is located
     * @param eventId Id of the event to retrieve
     * @return Event retrieved
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    Event retrieveEvent(String accessToken, String calendarId, String eventId) throws CalendarAccessException;

    /**
     * Updates an Event in a specified Google Calendar
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param calendarId Id of the calendar where the event is updated
     * @param eventId Id of the event to update
     * @param event New Event that overwrites the past event
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    void updateEvent(String accessToken, String calendarId, String eventId, EventEntity event) throws CalendarAccessException;

    /**
     * Deletes an Event in a specified Google Calendar
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param calendarId Id of the calendar where the event is deleted
     * @param eventId Id of the event to delete
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    void deleteEvent(String accessToken, String calendarId, String eventId) throws CalendarAccessException;
}
