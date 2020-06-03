package org.pesmypetcare.webservice.service.appmanager;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import org.pesmypetcare.webservice.dao.appmanager.GoogleCalendarDao;
import org.pesmypetcare.webservice.dao.petmanager.PetDao;
import org.pesmypetcare.webservice.entity.appmanager.EventEntity;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.error.DocumentException;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserToken;
import org.pesmypetcare.webservice.thirdpartyservices.adapters.UserTokenImpl;
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

    public UserToken makeUserToken(String token) {
        return new UserTokenImpl(token);
    }

    @Override
    public void createSecondaryCalendar(String googleToken, String accessToken, String petName)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        if (petDao.getSimpleField(makeUserToken(accessToken).getUid(), petName, CALENDAR_ID) != null) {
            throw new CalendarAccessException("409", "This pet already has a secondary calendar");
        }
        Calendar calendar = new Calendar();
        calendar.setSummary("My Pet Care: " + petName);
        calendar.setTimeZone("Europe/Madrid");
        String calendarId = googleCalendarDao.createSecondaryCalendar(googleToken, calendar);
        petDao.updateSimpleField(makeUserToken(accessToken).getUid(), petName, CALENDAR_ID, calendarId);
    }

    @Override
    public void deleteSecondaryCalendar(String googleToken, String accessToken, String petName)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        if (petDao.getSimpleField(makeUserToken(accessToken).getUid(), petName, CALENDAR_ID) == null) {
            throw new CalendarAccessException("409", "This pet does not have a secondary calendar");
        }
        String calendarId = (String) petDao.getSimpleField(makeUserToken(accessToken).getUid(), petName, CALENDAR_ID);
        googleCalendarDao.deleteSecondaryCalendar(googleToken, calendarId);
        petDao.updateSimpleField(makeUserToken(accessToken).getUid(), petName, CALENDAR_ID, null);
    }

    @Override
    public List<EventEntity> getAllEventsFromCalendar(String googleToken, String accessToken, String petName)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        String calendarId = (String) petDao.getSimpleField(makeUserToken(accessToken).getUid(), petName, CALENDAR_ID);
        List<Event> eventList = googleCalendarDao.getAllEventsFromCalendar(googleToken, calendarId);
        List<EventEntity> eventEntityList = new ArrayList<>();
        for (Event event : eventList) {
            eventEntityList.add(new EventEntity(event));
        }
        return eventEntityList;
    }

    @Override
    public void createEvent(String googleToken, String accessToken, String petName, EventEntity eventEntity)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        String calendarId = (String) petDao.getSimpleField(makeUserToken(accessToken).getUid(), petName, CALENDAR_ID);
        googleCalendarDao.createEvent(googleToken, calendarId, eventEntity.convertToEvent());
    }

    @Override
    public EventEntity retrieveEvent(String googleToken, String accessToken, String petName, String eventId)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        String calendarId = (String) petDao.getSimpleField(makeUserToken(accessToken).getUid(), petName, CALENDAR_ID);
        Event event = googleCalendarDao.retrieveEvent(googleToken, calendarId, eventId);
        return new EventEntity(event);
    }

    @Override
    public void updateEvent(String googleToken, String accessToken, String petName, EventEntity eventEntity)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        String calendarId = (String) petDao.getSimpleField(makeUserToken(accessToken).getUid(), petName, CALENDAR_ID);
        googleCalendarDao.updateEvent(googleToken, calendarId, eventEntity.getId(), eventEntity.convertToEvent());
    }

    @Override
    public void deleteEvent(String googleToken, String accessToken, String petName, String eventId)
        throws CalendarAccessException, DatabaseAccessException, DocumentException {
        String calendarId = (String) petDao.getSimpleField(makeUserToken(accessToken).getUid(), petName, CALENDAR_ID);
        googleCalendarDao.deleteEvent(googleToken, calendarId, eventId);
    }
}
