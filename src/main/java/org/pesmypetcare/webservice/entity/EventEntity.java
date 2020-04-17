package org.pesmypetcare.webservice.entity;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import lombok.Data;
import com.google.api.services.calendar.model.Event;

import java.util.Arrays;

/**
 * @author Marc Simó
 */
@Data
public class EventEntity {
    private String summary;
    private String description;
    private String id;
    private String color;
    private Integer emailReminderMinutes;
    private Integer repetitionInterval;
    private DateTime startDate;

    public Event convertToEvent() {
        // Basic attributes
        Event event = new Event()
            .setId(id)
            .setSummary(summary)
            .setDescription(description)
            .setColorId(color);
        // Initial Date
        EventDateTime start = new EventDateTime()
            .setDateTime(startDate)
            .setTimeZone("Europe/Madrid");
        event.setStart(start);
        // Recurrence
        String[] recurrence = new String[] {"RRULE:FREQ=DAILY;INTERVAL="+repetitionInterval};
        event.setRecurrence(Arrays.asList(recurrence));
        // Reminder
        EventReminder[] reminderOverrides = new EventReminder[] {
            new EventReminder().setMethod("email").setMinutes(emailReminderMinutes)
        };
        Event.Reminders reminders = new Event.Reminders()
            .setUseDefault(false)
            .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);
        return event;
    }
}
