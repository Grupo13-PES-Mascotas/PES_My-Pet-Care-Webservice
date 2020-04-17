package org.pesmypetcare.webservice.dao;

import com.google.api.services.calendar.model.Calendar;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import com.google.api.services.calendar.model.Event;

import java.util.List;

/**
 * @author Marc Simó
 */
public interface GoogleCalendarDao {

    /**
     * Creates a Secondary Google Calendar in the account specified by the accessToken
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param calendar Calendar to create
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    String createSecondaryCalendar(String accessToken, Calendar calendar) throws CalendarAccessException;

    /**
     * Deletes a Secondary Google Calendar in the account specified by the accessToken
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param calendarId Id of the created calendar
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    void deleteSecondaryCalendar(String accessToken, String calendarId) throws CalendarAccessException;

    /**
     * Returns all Calendar Events from a specified Calendar
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param calendarId Id of the calendar to retrieve
     * @return List containing all the Events from the specified Calendar
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    List<Event> getAllEventsFromCalendar(String accessToken, String calendarId) throws CalendarAccessException;

    /**
     * Creates an Event in a specified Google Calendar
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param calendarId Id of the calendar where the event is created
     * @param event Event to create
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    void createEvent(String accessToken, String calendarId, Event event) throws CalendarAccessException;

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
    void updateEvent(String accessToken, String calendarId, String eventId, Event event) throws CalendarAccessException;

    /**
     * Deletes an Event in a specified Google Calendar
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param calendarId Id of the calendar where the event is deleted
     * @param eventId Id of the event to delete
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    void deleteEvent(String accessToken, String calendarId, String eventId) throws CalendarAccessException;
}
