package org.pesmypetcare.webservice.dao.appmanager;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import org.pesmypetcare.webservice.firebaseservice.CalendarServiceFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author Marc Simó
 */
@ExtendWith(MockitoExtension.class)
public class GoogleCalendarDaoTest {
    private static String accessToken;
    private static String calendarId;
    private static String eventId;
    private static Calendar calendar;
    private static Event event;
    private static Events eventsModel;
    private static List<Event> eventList;

    @Mock
    private CalendarServiceFactory factory;
    @Mock
    private com.google.api.services.calendar.Calendar service;
    @Mock
    private com.google.api.services.calendar.Calendar.Calendars calendars;
    @Mock
    private com.google.api.services.calendar.Calendar.Calendars.Insert insertCalendar;
    @Mock
    private com.google.api.services.calendar.Calendar.Calendars.Delete deleteCalendar;
    @Mock
    private com.google.api.services.calendar.Calendar.Events events;
    @Mock
    private com.google.api.services.calendar.Calendar.Events.List eventsList;
    @Mock
    private com.google.api.services.calendar.Calendar.Events.Delete deleteEvent;
    @Mock
    private com.google.api.services.calendar.Calendar.Events.Update updateEvent;
    @Mock
    private com.google.api.services.calendar.Calendar.Events.Get getEvent;
    @Mock
    private com.google.api.services.calendar.Calendar.Events.Insert insertEvent;

    @InjectMocks
    private GoogleCalendarDao googleCalendarDao = new GoogleCalendarDaoImpl();

    @BeforeAll
    public static void setUp() {
        accessToken = "tokenparatests";
        calendarId = "calendarId";
        eventId = "eventId";
        calendar = new Calendar();
        calendar.setId("calendarId");
        event = new Event();
        eventList = new ArrayList<>();
        eventList.add(event);
        eventsModel = new Events();
        eventsModel.setItems(eventList);
    }

    @Test
    public void shouldCreateSecondaryCalendarOnGoogleCalendarWhenRequested() throws IOException,
        CalendarAccessException {
        given(factory.initializeService(anyString())).willReturn(service);
        given(service.calendars()).willReturn(calendars);
        given(calendars.insert(isA(Calendar.class))).willReturn(insertCalendar);
        given(insertCalendar.execute()).willReturn(calendar);

        String response = googleCalendarDao.createSecondaryCalendar(accessToken, calendar);

        assertSame(calendarId, response, "Should return calendarId Entity");
    }

    @Test
    public void shouldDeleteSecondaryCalendarOnGoogleCalendarWhenRequested() throws IOException,
        CalendarAccessException {
        given(factory.initializeService(anyString())).willReturn(service);
        given(service.calendars()).willReturn(calendars);
        given(calendars.delete(anyString())).willReturn(deleteCalendar);

        googleCalendarDao.deleteSecondaryCalendar(accessToken, calendarId);

        verify(factory).initializeService(same(accessToken));
        verify(calendars).delete(same(calendarId));
    }

    @Test
    public void shouldGetAllEventsFromCalendarOnGoogleCalendarWhenRequested() throws IOException,
        CalendarAccessException {
        given(factory.initializeService(anyString())).willReturn(service);
        given(service.events()).willReturn(events);
        given(events.list(anyString())).willReturn(eventsList);
        given(eventsList.setPageToken(null)).willReturn(eventsList);
        given(eventsList.execute()).willReturn(eventsModel);

        List<Event> response = googleCalendarDao.getAllEventsFromCalendar(accessToken, calendarId);

        assertEquals(eventList, response, "Should return event list");
    }

    @Test
    public void shouldCreateEventOnGoogleCalendarWhenRequested() throws IOException,
        CalendarAccessException {
        given(factory.initializeService(anyString())).willReturn(service);
        given(service.events()).willReturn(events);
        given(events.insert(anyString(), isA(Event.class))).willReturn(insertEvent);

        googleCalendarDao.createEvent(accessToken, calendarId, event);

        verify(factory).initializeService(same(accessToken));
        verify(events).insert(same(calendarId), same(event));
    }

    @Test
    public void shouldRetrieveEventOnGoogleCalendarWhenRequested() throws IOException,
        CalendarAccessException {
        given(factory.initializeService(anyString())).willReturn(service);
        given(service.events()).willReturn(events);
        given(events.get(anyString(), anyString())).willReturn(getEvent);
        given(getEvent.execute()).willReturn(event);

        Event response = googleCalendarDao.retrieveEvent(accessToken, calendarId, eventId);

        assertSame(event, response, "Should return event");
    }

    @Test
    public void shouldUpdateEventOnGoogleCalendarWhenRequested() throws IOException,
        CalendarAccessException {
        given(factory.initializeService(anyString())).willReturn(service);
        given(service.events()).willReturn(events);
        given(events.update(anyString(), anyString(), isA(Event.class))).willReturn(updateEvent);

        googleCalendarDao.updateEvent(accessToken, calendarId, eventId, event);

        verify(factory).initializeService(same(accessToken));
        verify(events).update(same(calendarId), same(eventId), same(event));
    }

    @Test
    public void shouldDeleteEventOnGoogleCalendarWhenRequested() throws IOException,
        CalendarAccessException {
        given(factory.initializeService(anyString())).willReturn(service);
        given(service.events()).willReturn(events);
        given(events.delete(anyString(), anyString())).willReturn(deleteEvent);

        googleCalendarDao.deleteEvent(accessToken, calendarId, eventId);

        verify(factory).initializeService(same(accessToken));
        verify(events).delete(same(calendarId), same(eventId));
    }
}
