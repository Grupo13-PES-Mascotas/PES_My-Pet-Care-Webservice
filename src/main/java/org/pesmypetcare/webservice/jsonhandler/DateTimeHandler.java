package org.pesmypetcare.webservice.jsonhandler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Marc Simó
 */
public class DateTimeHandler extends StdDeserializer<DateTime> {

    public DateTimeHandler() {
        this(null);
    }

    public DateTimeHandler(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String date = jsonParser.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("es", "ES"));
        try {
            return new DateTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
