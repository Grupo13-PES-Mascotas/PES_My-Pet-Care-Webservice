package org.pesmypetcare.webservice.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import lombok.Data;
import com.google.api.services.calendar.model.Event;
import org.pesmypetcare.webservice.jsonhandler.DateTimeHandler;

import java.util.Arrays;

/**
 * @author Marc Simó
 */
@Data
public class EventEntity {
    private String id;
    private String summary;
    private String location;
    private String description;
    private String color;
    private Integer emailReminderMinutes;
    private Integer repetitionInterval;
    @JsonDeserialize(using = DateTimeHandler.class)
    private DateTime startDate;
    @JsonDeserialize(using = DateTimeHandler.class)
    private DateTime endDate;


    public EventEntity (Event event) {
        id = event.getId();
        summary = event.getSummary();
        location = event.getLocation();
        description = event.getDescription();
        color = event.getColorId();
        emailReminderMinutes = 0;
        repetitionInterval = 0;
        startDate = event.getStart().getDate();
        endDate = event.getEnd().getDate();
    }

    public EventEntity(String id, String summary, String description, String location, String color,
                         Integer emailReminderMinutes,
                       Integer repetitionInterval, DateTime startDate, DateTime endDate) {
        this.id = id;
        this.summary = summary;
        this.location = location;
        this.description = description;
        this.color = color;
        this.emailReminderMinutes = emailReminderMinutes;
        this.repetitionInterval = repetitionInterval;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Event convertToEvent() {
        Event event = new Event()
            .setId(id)
            .setSummary(summary)
            .setLocation(location)
            .setDescription(description)
            .setColorId(color);

        EventDateTime start = new EventDateTime()
            .setDateTime(startDate)
            .setTimeZone("Europe/Madrid");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
            .setDateTime(endDate)
            .setTimeZone("Europe/Madrid");
        event.setEnd(end);

        String[] recurrence = new String[] {"RRULE:FREQ=DAILY;INTERVAL="+repetitionInterval};
        event.setRecurrence(Arrays.asList(recurrence));

        EventReminder[] reminderOverrides = new EventReminder[] {
            new EventReminder().setMethod("email").setMinutes(emailReminderMinutes),
        };
        Event.Reminders reminders = new Event.Reminders()
            .setUseDefault(false)
            .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        return event;
    }
}
