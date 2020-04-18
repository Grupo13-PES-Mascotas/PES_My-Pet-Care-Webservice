package org.pesmypetcare.webservice.dao;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.EventEntity;
import org.pesmypetcare.webservice.error.CalendarAccessException;

import java.io.IOException;

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
    private static EventEntity eventEntity;
    private static Calendar calendar;
    private static Event event;

    @Mock
    private com.google.api.services.calendar.Calendar service;
    @Mock
    private com.google.api.services.calendar.Calendar.Calendars calendars;
    @Mock
    private com.google.api.services.calendar.Calendar.Calendars.Insert insertCalendar;
    @Mock
    private com.google.api.services.calendar.Calendar.Events events;
    @Mock
    private com.google.api.services.calendar.Calendar.Events.Insert insertEvent;

    @InjectMocks
    private GoogleCalendarDao googleCalendarDao = new GoogleCalendarDaoImpl();

    @BeforeAll
    public static void setUp() {
        accessToken = "tokenparatests";
        calendarId = "calendarId";
        eventId = "eventId";
        eventEntity = new EventEntity();
        calendar = new Calendar();
        event = new Event();
    }

    @Test
    public void shouldCreateSecondaryCalendarOnGoogleCalendarWhenRequested() throws IOException,
        CalendarAccessException {
        given(service.calendars()).willReturn(calendars);
        given(calendars.insert(isA(Calendar.class))).willReturn(insertCalendar);
        given(insertCalendar.execute()).willReturn(null);

        googleCalendarDao.createSecondaryCalendar(accessToken, calendar);

        verify(calendars).insert(same(calendar));
    }

    @Test
    public void shouldCreateEventOnGoogleCalendarWhenRequested() throws IOException,
        CalendarAccessException {
        given(service.events()).willReturn(events);
        given(events.insert(calendarId, event)).willReturn(insertEvent);
        given(insertEvent.execute()).willReturn(null);

        googleCalendarDao.createSecondaryCalendar(accessToken, calendar);
    }
}
