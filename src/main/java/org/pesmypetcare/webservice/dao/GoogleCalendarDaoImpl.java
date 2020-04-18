package org.pesmypetcare.webservice.dao;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Marc Simó
 */
@Repository
public class GoogleCalendarDaoImpl implements GoogleCalendarDao {
    private static final HttpTransport HTTP_TRANSPORT;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private com.google.api.services.calendar.Calendar service;

    static {
        HttpTransport temp;
        try {
            temp = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            temp = null;
        }
        HTTP_TRANSPORT = temp;
    }

    @Override
    public String createSecondaryCalendar(String accessToken, Calendar calendar) throws CalendarAccessException {
        service = initializeService(accessToken);
        Calendar createdCalendar;
        try {
            createdCalendar=service.calendars().insert(calendar).execute();
        } catch (IOException e) {
            throw new CalendarAccessException("Error inserting secondary calendar", e.getMessage());
        }
        return createdCalendar.getId();
    }

    @Override
    public void deleteSecondaryCalendar(String accessToken, String calendarId) throws CalendarAccessException {
        service = initializeService(accessToken);
        try {
            service.calendars().delete(calendarId).execute();
        } catch (IOException e) {
            throw new CalendarAccessException("Error deleting secondary calendar", e.getMessage());
        }
    }

    @Override
    public List<Event> getAllEventsFromCalendar(String accessToken, String calendarId) throws CalendarAccessException {
        service = initializeService(accessToken);
        List<Event> allEvents = new ArrayList<>();
        // Iterate over the events in the specified calendar, needed because there's a limit for the amount of events
        // Google Calendar returns at a time
        String pageToken = null;
        do {
            Events pageEvents;
            try {
                pageEvents = service.events().list(calendarId).setPageToken(pageToken).execute();
            } catch (IOException e) {
                throw new CalendarAccessException("Error iterating through events", e.getMessage());
            }
            allEvents.addAll(pageEvents.getItems());
            pageToken = pageEvents.getNextPageToken();
        } while (pageToken != null);
        return allEvents;
    }

    @Override
    public void createEvent(String accessToken, String calendarId, Event event) throws CalendarAccessException {
        service = initializeService(accessToken);
        try {
            service.events().insert(calendarId, event).execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CalendarAccessException("Error inserting event", e.getMessage());
        }
    }

    @Override
    public Event retrieveEvent(String accessToken, String calendarId, String eventId) throws CalendarAccessException {
        service = initializeService(accessToken);
        Event event;
        try {
            event = service.events().get(calendarId, eventId).execute();
        } catch (IOException e) {
            throw new CalendarAccessException("Error retrieving event", e.getMessage());
        }
        return event;
    }

    @Override
    public void updateEvent(String accessToken, String calendarId, String eventId, Event event)
        throws CalendarAccessException {
        service = initializeService(accessToken);
        try {
            service.events().update(calendarId, eventId, event).execute();
        } catch (IOException e) {
            throw new CalendarAccessException("Error updating event", e.getMessage());
        }
    }

    @Override
    public void deleteEvent(String accessToken, String calendarId, String eventId) throws CalendarAccessException {
        service = initializeService(accessToken);
        try {
            service.events().delete(calendarId, eventId).execute();
        } catch (IOException e) {
            throw new CalendarAccessException("Error deleting event", e.getMessage());
        }
    }

    public com.google.api.services.calendar.Calendar initializeService(String accessToken) {
        Credential credential =
            new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
        return new com.google.api.services.calendar.Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY,
            credential).setApplicationName("My Pet Care").build();
    }
}
