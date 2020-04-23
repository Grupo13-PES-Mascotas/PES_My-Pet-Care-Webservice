package org.pesmypetcare.webservice.entity.appmanager;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import lombok.Data;

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
    private String startDate;
    private String endDate;

    public EventEntity() { }

    public EventEntity(Event event) {
        id = event.getId();
        summary = event.getSummary();
        location = event.getLocation();
        description = event.getDescription();
        color = event.getColorId();
        startDate = event.getStart().getDateTime().toString();
        endDate = event.getEnd().getDateTime().toString();
    }

    public EventEntity(String id, String summary, String description, String location, String color,
                         Integer emailReminderMinutes,
                       Integer repetitionInterval, String startDate, String endDate) {
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
            .setDateTime(new DateTime(startDate))
            .setTimeZone("Europe/Madrid");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
            .setDateTime(new DateTime(endDate))
            .setTimeZone("Europe/Madrid");
        event.setEnd(end);

        String[] recurrence = {"RRULE:FREQ=DAILY;INTERVAL=" + repetitionInterval};
        event.setRecurrence(Arrays.asList(recurrence));

        EventReminder[] reminderOverrides = {
            new EventReminder().setMethod("email").setMinutes(emailReminderMinutes)
        };
        Event.Reminders reminders = new Event.Reminders()
            .setUseDefault(false)
            .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        return event;
    }
}
