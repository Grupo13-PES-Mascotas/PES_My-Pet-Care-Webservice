package org.pesmypetcare.webservice.service;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import org.pesmypetcare.webservice.dao.GoogleCalendarDao;
import org.pesmypetcare.webservice.entity.EventEntity;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Marc Simó
 */
@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService{
    @Autowired
    private GoogleCalendarDao googleCalendarDao;

    @Override
    public void createSecondaryCalendar(String accessToken, String owner, String petName) throws CalendarAccessException {
        Calendar calendar = new Calendar();
        calendar.setSummary(petName);
        calendar.setTimeZone("Europe/Madrid");
        String calendarId = googleCalendarDao.createSecondaryCalendar(accessToken, calendar);
    }

    @Override
    public void deleteSecondaryCalendar(String accessToken, String petName) throws CalendarAccessException {

    }

    @Override
    public List<Event> getAllEventsFromCalendar(String accessToken, String petName) throws CalendarAccessException {
        return null;
    }

    @Override
    public void createEvent(String accessToken, String calendarId, EventEntity event) throws CalendarAccessException {

    }

    @Override
    public Event retrieveEvent(String accessToken, String calendarId, String eventId) throws CalendarAccessException {
        return null;
    }

    @Override
    public void updateEvent(String accessToken, String calendarId, String eventId, EventEntity event) throws CalendarAccessException {

    }

    @Override
    public void deleteEvent(String accessToken, String calendarId, String eventId) throws CalendarAccessException {

    }
}
