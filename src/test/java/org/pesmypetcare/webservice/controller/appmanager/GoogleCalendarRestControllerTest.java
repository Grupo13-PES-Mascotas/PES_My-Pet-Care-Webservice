package org.pesmypetcare.webservice.controller.appmanager;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pesmypetcare.webservice.entity.EventEntity;
import org.pesmypetcare.webservice.error.CalendarAccessException;
import org.pesmypetcare.webservice.error.DatabaseAccessException;
import org.pesmypetcare.webservice.service.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Marc Simó
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class GoogleCalendarRestControllerTest {
    private static final String TOKEN = "token";
    private static String jsonEventEntity;
    private static String jsonEventId;
    private static List<Event> eventList;
    private static EventEntity eventEntity;
    private static String accessToken;
    private static String owner;
    private static String petName;
    private static String urlBase;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoogleCalendarService service;

    @BeforeAll
    public static void setUp() {
        owner = "My owner";
        petName = "My petName";
        accessToken = "tokem";
        eventList = new ArrayList<>();
        eventEntity = new EventEntity();
        urlBase = "/calendar";
        jsonEventEntity = "{\n"
            + "  \"id\": \"idtest\",\n"
            + "  \"summary\":\"Event for tests\",\n"
            + "  \"location\": \"Wherever you wanna be\",\n"
            + "  \"description\": \"This is an event I created just to do tests\",\n"
            + "  \"color\": \"11\",\n"
            + "  \"emailReminderMinutes\": 10,\n"
            + "  \"repetitionInterval\": 1,\n"
            + "  \"startDate\": \"2020-04-26T18:20:38\",\n"
            + "  \"endDate\": \"2020-04-26T23:20:38\"\n"
            + "}";
        jsonEventId = "{\n"
            + "  \"eventId\": \"idtest\"\n"
            + "}";
    }

    @Test
    public void createSecondaryCalendarShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).createSecondaryCalendar(anyString(), anyString(), anyString());
        mockMvc.perform(post(urlBase + "/" + owner + "/" + petName)
            .header(TOKEN, accessToken))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteSecondaryCalendarShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteSecondaryCalendar(anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + "/" + owner + "/" + petName)
            .header(TOKEN, accessToken))
            .andExpect(status().isOk());
    }

    @Test
    public void getAllEventsFromCalendarShouldReturnAnEventListAndStatusOk() throws Exception {
        willReturn(eventList).given(service).getAllEventsFromCalendar(anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + "/" + owner + "/" + petName)
            .header(TOKEN, accessToken))
            .andExpect(status().isOk());
    }

    @Test
    public void createEventShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).createEvent(anyString(), anyString(), anyString(), isA(EventEntity.class));
        mockMvc.perform(post(urlBase + "/event/" + owner + "/" + petName)
            .header(TOKEN, accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonEventEntity))
            .andExpect(status().isOk());
    }

    @Test
    public void retrieveEventShouldReturnStatusOk() throws Exception {
        willReturn(eventEntity).given(service).retrieveEvent(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(get(urlBase + "/event/" + owner + "/" + petName)
            .header(TOKEN, accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonEventId))
            .andExpect(status().isOk());
    }

    @Test
    public void updateEventShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).updateEvent(anyString(), anyString(), anyString(), isA(EventEntity.class));
        mockMvc.perform(put(urlBase + "/event/" + owner + "/" + petName)
            .header(TOKEN, accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonEventEntity))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteEventShouldReturnStatusOk() throws Exception {
        willDoNothing().given(service).deleteEvent(anyString(), anyString(), anyString(), anyString());
        mockMvc.perform(delete(urlBase + "/event/" + owner + "/" + petName)
            .header(TOKEN, accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonEventId))
            .andExpect(status().isOk());
    }
}
