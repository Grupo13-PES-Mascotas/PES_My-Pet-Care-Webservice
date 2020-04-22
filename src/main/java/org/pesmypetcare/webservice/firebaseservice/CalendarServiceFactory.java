package org.pesmypetcare.webservice.firebaseservice;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author Marc Simó
 */
public class CalendarServiceFactory {
    private static CalendarServiceFactory instance;
    private static final HttpTransport HTTP_TRANSPORT;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    static {
        HttpTransport temp = null;
        try {
            temp = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        HTTP_TRANSPORT = temp;
    }

    private CalendarServiceFactory() { }

    public static CalendarServiceFactory getInstance() {
        if (instance == null) {
            instance = new CalendarServiceFactory();
        }
        return instance;
    }

    public com.google.api.services.calendar.Calendar initializeService(String accessToken) {
        Credential credential =
            new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
        return new com.google.api.services.calendar.Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY,
            credential).setApplicationName("My Pet Care").build();
    }
}
