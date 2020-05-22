package org.pesmypetcare.webservice.dao.appmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.io.IOException;

/**
 * @author Marc Simó
 */
@Repository
public class GoogleMapsDaoImpl implements GoogleMapsDao, EnvironmentAware {
    private static Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        GoogleMapsDaoImpl.env = environment;
    }

    @Override
    public void test() throws InterruptedException, ApiException, IOException {
        GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(env.getProperty("google.maps.api.key"))
            .build();
        GeocodingResult[] results =  GeocodingApi.geocode(context,
            "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(results[0].addressComponents));
    }
}
