package org.pesmypetcare.webservice.dao.appmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import java.io.IOException;

/**
 * @author Marc Simó
 */
public class GoogleMapsDaoImpl implements GoogleMapsDao {

    public void test() throws InterruptedException, ApiException, IOException {
        GeoApiContext context = new GeoApiContext.Builder()
            .apiKey("AIzaSyBJhfUFYUlDv_sY_fTD9r6e1W7zkpqka0M")
            .build();
        GeocodingResult[] results =  GeocodingApi.geocode(context,
            "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(results[0].addressComponents));
    }
}
