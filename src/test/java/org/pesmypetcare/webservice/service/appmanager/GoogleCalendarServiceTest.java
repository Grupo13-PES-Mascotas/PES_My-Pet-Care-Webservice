package org.pesmypetcare.webservice.service.appmanager;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.dao.appmanager.GoogleCalendarDao;
import org.pesmypetcare.webservice.dao.petmanager.PetDao;
import org.pesmypetcare.webservice.entity.appmanager.EventEntity;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
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
    private static String googleToken;
    private static String accessToken;
    private static String ownerId;
    private static String petName;
    private static String date;
    private static String date2;
    private static String eventId;
    private static String calendarId;

    @Mock
    private GoogleCalendarDao googleCalendarDao;
    @Mock
    private PetDao petDao;
    @Mock
    private UserToken userToken;

    @InjectMocks
    private GoogleCalendarService service = spy(new GoogleCalendarServiceImpl());

    @BeforeAll
    public static void setUp() {
        petName = "My petName";
        ownerId = "ownerId";
        date = "2020-02-13T10:30:00.000+01:00";
        date2 = "2020-02-13T12:30:00.000+01:00";
        eventId = "My eventId";
        calendarId = "My calendarId";
        accessToken = "token";
        googleToken = "some-token";
        eventList = new ArrayList<>();
        eventEntity = new EventEntity();
        eventEntity.setId(eventId);
        eventEntity.setStartDate(date);
        eventEntity.setEndDate(date2);
        event = new Event();
        event.setId(eventId);
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
    public void shouldReturnNothingWhenSecondaryCalendarCreated()
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        doReturn(userToken).when((GoogleCalendarServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(ownerId);
        given(googleCalendarDao.createSecondaryCalendar(anyString(), isA(Calendar.class))).willReturn(calendarId);
        service.createSecondaryCalendar(googleToken, accessToken, petName);
        verify(googleCalendarDao).createSecondaryCalendar(isA(String.class), isA(Calendar.class));
        verify(petDao).updateSimpleField(isA(String.class), isA(String.class), isA(String.class), isA(String.class));
    }

    @Test
    public void shouldReturnNothingWhenSecondaryCalendarDeleted() throws CalendarAccessException,
        DatabaseAccessException, DocumentException {
        doReturn(userToken).when((GoogleCalendarServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(ownerId);
        given(petDao.getSimpleField(ownerId, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        service.deleteSecondaryCalendar(googleToken, accessToken, petName);
        verify(googleCalendarDao).deleteSecondaryCalendar(isA(String.class), isA(String.class));
        verify(petDao).updateSimpleField(isA(String.class), isA(String.class), isA(String.class), isNull());
    }

    @Test
    public void shouldReturnAListOfEventsWhenAllEventsFromCalendarRetrieved() throws CalendarAccessException,
        DatabaseAccessException, DocumentException {
        doReturn(userToken).when((GoogleCalendarServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(ownerId);
        given(petDao.getSimpleField(ownerId, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        List<EventEntity> response = service.getAllEventsFromCalendar(googleToken, accessToken, petName);
        assertEquals(eventList, response, "Should return an array of Event");
    }

    @Test
    public void shouldReturnNothingWhenEventCreated() throws CalendarAccessException,
        DatabaseAccessException, DocumentException {
        doReturn(userToken).when((GoogleCalendarServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(ownerId);
        given(petDao.getSimpleField(ownerId, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        service.createEvent(googleToken, accessToken, petName, eventEntity);
        verify(googleCalendarDao).createEvent(isA(String.class), isA(String.class), isA(Event.class));
    }

    @Test
    public void shouldReturnEventWhenEventRetrieved() throws CalendarAccessException,
        DatabaseAccessException, DocumentException {
        doReturn(userToken).when((GoogleCalendarServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(ownerId);
        given(petDao.getSimpleField(ownerId, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        given(googleCalendarDao.retrieveEvent(anyString(), anyString(), anyString())).willReturn(event);
        service.retrieveEvent(googleToken, accessToken, petName, eventId);
        EventEntity response = service.retrieveEvent(googleToken, accessToken, petName, eventId);
        assertEquals(eventEntity, response, "Should return an Event Entity");
    }

    @Test
    public void shouldReturnNothingWhenEventUpdated() throws CalendarAccessException,
        DatabaseAccessException, DocumentException {
        doReturn(userToken).when((GoogleCalendarServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(ownerId);
        given(petDao.getSimpleField(ownerId, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        service.updateEvent(googleToken, accessToken, petName, eventEntity);
        verify(googleCalendarDao).updateEvent(isA(String.class), isA(String.class), isA(String.class),
            isA(Event.class));
    }

    @Test
    public void shouldReturnNothingWhenEventDeleted() throws CalendarAccessException,
        DatabaseAccessException, DocumentException {
        doReturn(userToken).when((GoogleCalendarServiceImpl) service).makeUserToken(anyString());
        given(userToken.getUid()).willReturn(ownerId);
        given(petDao.getSimpleField(ownerId, petName, CALENDAR_ID_FIELD)).willReturn(calendarId);
        service.deleteEvent(googleToken, accessToken, petName, eventId);
        verify(googleCalendarDao).deleteEvent(isA(String.class), isA(String.class), isA(String.class));
    }
}
