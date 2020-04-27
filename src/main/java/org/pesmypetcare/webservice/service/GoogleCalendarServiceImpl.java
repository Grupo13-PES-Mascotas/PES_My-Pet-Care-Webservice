package org.pesmypetcare.webservice.service;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import org.pesmypetcare.webservice.dao.GoogleCalendarDao;
import org.pesmypetcare.webservice.dao.PetDao;
import org.pesmypetcare.webservice.entity.EventEntity;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marc Simó
 */
@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {
    private static final String CALENDAR_ID = "calendarId";
    @Autowired
    private GoogleCalendarDao googleCalendarDao;

    @Autowired
    private PetDao petDao;

    @Override
    public void createSecondaryCalendar(String accessToken, String owner, String petName)
        throws CalendarAccessException {
        Calendar calendar = new Calendar();
        calendar.setSummary("My Pet Care: " + petName);
        calendar.setTimeZone("Europe/Madrid");
        String calendarId = googleCalendarDao.createSecondaryCalendar(accessToken, calendar);
        petDao.updateField(owner, petName, CALENDAR_ID, calendarId);
    }

    @Override
    public void deleteSecondaryCalendar(String accessToken, String owner, String petName)
        throws CalendarAccessException, DatabaseAccessException {
        String calendarId = (String) petDao.getField(owner, petName, CALENDAR_ID);
        googleCalendarDao.deleteSecondaryCalendar(accessToken, calendarId);
        petDao.updateField(owner, petName, CALENDAR_ID, null);
    }

    @Override
    public List<EventEntity> getAllEventsFromCalendar(String accessToken, String owner, String petName)
        throws CalendarAccessException, DatabaseAccessException {
        String calendarId = (String) petDao.getField(owner, petName, CALENDAR_ID);
        List<Event> eventList = googleCalendarDao.getAllEventsFromCalendar(accessToken, calendarId);
        List<EventEntity> eventEntityList = new ArrayList<>();
        for (Event event: eventList) {
            eventEntityList.add(new EventEntity(event));
        }
        return eventEntityList;
    }

    @Override
    public void createEvent(String accessToken, String owner, String petName, EventEntity eventEntity)
        throws CalendarAccessException, DatabaseAccessException {
        String calendarId = (String) petDao.getField(owner, petName, CALENDAR_ID);
        googleCalendarDao.createEvent(accessToken, calendarId, eventEntity.convertToEvent());
    }

    @Override
    public EventEntity retrieveEvent(String accessToken, String owner, String petName, String eventId)
        throws CalendarAccessException, DatabaseAccessException {
        String calendarId = (String) petDao.getField(owner, petName, CALENDAR_ID);
        Event event = googleCalendarDao.retrieveEvent(accessToken, calendarId, eventId);
        return new EventEntity(event);
    }

    @Override
    public void updateEvent(String accessToken, String owner, String petName, EventEntity eventEntity)
        throws CalendarAccessException, DatabaseAccessException {
        String calendarId = (String) petDao.getField(owner, petName, CALENDAR_ID);
        googleCalendarDao.updateEvent(accessToken, calendarId, eventEntity.getId(), eventEntity.convertToEvent());
    }

    @Override
    public void deleteEvent(String accessToken, String owner, String petName, String eventId)
        throws CalendarAccessException, DatabaseAccessException {
        String calendarId = (String) petDao.getField(owner, petName, CALENDAR_ID);
        googleCalendarDao.deleteEvent(accessToken, calendarId, eventId);
    }
}
