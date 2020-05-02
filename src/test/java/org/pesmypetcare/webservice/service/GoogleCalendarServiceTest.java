package org.pesmypetcare.webservice.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.google.api.services.calendar.model.Calendar;
import org.pesmypetcare.webservice.dao.GoogleCalendarDao;
import org.pesmypetcare.webservice.dao.PetDao;
import org.pesmypetcare.webservice.entity.EventEntity;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import org.pesmypetcare.webservice.error.DatabaseAccessException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author Marc Simó
 */
@ExtendWith(MockitoExtension.class)
public class GoogleCalendarServiceTest {
    private static final String CALENDAR_ID_FIELD = "calendarId";
    private static List<EventEntity> eventList;
    private static EventEntity eventEntity;
    private static Event event;
    private static String accessToken;
    private static String owner;
    private static String petName;
    private static String date;
    private static String date2;
    private static String eventId;
    private static String calendarId;

    @Mock
    private GoogleCalendarDao googleCalendarDao;
    @Mock
    private PetDao petDao;

    @InjectMocks
    private GoogleCalendarService service = new GoogleCalendarServiceImpl();

    @BeforeAll
    public static void setUp() {
        owner = "My owner";
        petName = "My petName";
        date = "2020-02-13T10:30:00.000+01:00";
        date2 = "2020-02-13T12:30:00.000+01:00";
        eventId = "My eventId";
        calendarId = "My calendarId";
        accessToken = "tokem";
        eventList = new ArrayList<>();
        eventEntity = new EventEntity();
        eventEntity.setId("My eventId");
        eventEntity.setStartDate(date);
        eventEntity.setEndDate(date2);
        event = new Event();
        event.setId("My eventId");
        EventDateTime start = new EventDateTime()
            .setDateTime(new DateTime(date))
            .setTimeZone("Europe/Madrid");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
            .setDateTime(new DateTime(date2))
            .setTimeZone("Europe/Madrid");
        event.setEnd(end);
    }

    @Test
    public void shouldReturnNothingWhenSecondaryCalendarCreated() throws CalendarAccessException {
        given(googleCalendarDao.createSecondaryCalendar(anyString(), isA(Calendar.class))).willReturn(calendarId);
        service.createSecondaryCalendar(accessToken, owner, petName);
        verify(googleCalendarDao).createSecondaryCalendar(isA(String.class), isA(Calendar.class));
        verify(petDao).updateSimpleField(isA(String.class), isA(String.class), isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenSecondaryCalendarDeleted() throws CalendarAccessException,
        DatabaseAccessException {
        given(petDao.getSimpleField(owner, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        service.deleteSecondaryCalendar(accessToken, owner, petName);
        verify(googleCalendarDao).deleteSecondaryCalendar(isA(String.class), isA(String.class));
        verify(petDao).updateSimpleField(isA(String.class), isA(String.class), isA(String.class), isNull());
    }

    @Test
    public void shouldReturnAListOfEventsWhenAllEventsFromCalendarRetrieved() throws CalendarAccessException,
        DatabaseAccessException {
        given(petDao.getSimpleField(owner, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        List<EventEntity> response = service.getAllEventsFromCalendar(accessToken, owner, petName);
        assertEquals(eventList, response, "Should return an array of Event");
    }

    @Test
    public void shouldReturnNothingWhenEventCreated() throws CalendarAccessException,
        DatabaseAccessException {
        given(petDao.getSimpleField(owner, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        service.createEvent(accessToken, owner, petName, eventEntity);
        verify(googleCalendarDao).createEvent(isA(String.class), isA(String.class), isA(Event.class));
    }

    @Test
    public void shouldReturnEventWhenEventRetrieved() throws CalendarAccessException,
        DatabaseAccessException {
        given(petDao.getSimpleField(owner, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        given(googleCalendarDao.retrieveEvent(anyString(), anyString(), anyString())).willReturn(event);
        service.retrieveEvent(accessToken, owner, petName, eventId);
        EventEntity response = service.retrieveEvent(accessToken, owner, petName, eventId);
        assertEquals(eventEntity, response, "Should return an Event Entity");
    }

    @Test
    public void shouldReturnNothingWhenEventUpdated() throws CalendarAccessException,
        DatabaseAccessException {
        given(petDao.getSimpleField(owner, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        service.updateEvent(accessToken, owner, petName, eventEntity);
        verify(googleCalendarDao).updateEvent(isA(String.class), isA(String.class), isA(String.class),
            isA(Event.class));
    }

    @Test
    public void shouldReturnNothingWhenEventDeleted() throws CalendarAccessException,
        DatabaseAccessException {
        given(petDao.getSimpleField(owner, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        service.deleteEvent(accessToken, owner, petName, eventId);
        verify(googleCalendarDao).deleteEvent(isA(String.class), isA(String.class), isA(String.class));
    }
}
