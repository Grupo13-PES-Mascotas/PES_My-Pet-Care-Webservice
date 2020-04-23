package org.pesmypetcare.webservice.service;

import com.google.api.services.calendar.model.Event;
import org.pesmypetcare.webservice.entity.EventEntity;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.List;

/**
 * @author Marc Simó
 */
public interface GoogleCalendarService {

    /**
     * Creates a Secondary Google Calendar in the account specified by the accessToken.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar is created for
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     */
    void createSecondaryCalendar(String accessToken, String owner, String petName) throws CalendarAccessException;

    /**
     * Deletes a Secondary Google Calendar in the account specified by the accessToken.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteSecondaryCalendar(String accessToken, String owner, String petName)
        throws CalendarAccessException, DatabaseAccessException;

    /**
     * Returns all Calendar Events from a specified Calendar.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @return List containing all the Events from the specified Calendar
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    List<EventEntity> getAllEventsFromCalendar(String accessToken, String owner, String petName)
        throws CalendarAccessException, DatabaseAccessException;

    /**
     * Creates an Event in a specified Google Calendar.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param eventEntity Event to create
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void createEvent(String accessToken, String owner, String petName, EventEntity eventEntity)
        throws CalendarAccessException, DatabaseAccessException;

    /**
     * Retrieves an Event in a specified Google Calendar.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param eventId Id of the event to retrieve
     * @return Event retrieved
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    EventEntity retrieveEvent(String accessToken, String owner, String petName, String eventId)
        throws CalendarAccessException, DatabaseAccessException;

    /**
     * Updates an Event in a specified Google Calendar.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param eventEntity New Event that overwrites the past event with the same id
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void updateEvent(String accessToken, String owner, String petName, EventEntity eventEntity)
        throws CalendarAccessException, DatabaseAccessException;

    /**
     * Deletes an Event in a specified Google Calendar.
     * @param accessToken oauth2 token needed to access the Google Calendar
     * @param owner Name of the owner of the pet
     * @param petName Name of the pet the calendar belongs to
     * @param eventId Id of the event to delete
     * @throws CalendarAccessException If an error occurs when accessing the calendar
     * @throws DatabaseAccessException If an error occurs when accessing the database
     */
    void deleteEvent(String accessToken, String owner, String petName, String eventId)
        throws CalendarAccessException, DatabaseAccessException;
}
