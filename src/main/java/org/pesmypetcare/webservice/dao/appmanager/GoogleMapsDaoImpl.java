package org.pesmypetcare.webservice.dao.appmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import org.pesmypetcare.webservice.entity.appmanager.PlaceDetailsEntity;
import org.pesmypetcare.webservice.error.MapsAccessException;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Marc Simó
 */
@Repository
public class GoogleMapsDaoImpl implements GoogleMapsDao, EnvironmentAware {
    private static Environment env;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private GeoApiContext context;

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    public GoogleMapsDaoImpl() {
        context = new GeoApiContext.Builder()
            .apiKey(env.getProperty("google.maps.api.key"))
            .build();
    }

    // Les valoracions fetes des de l'aplicació s'hauràn de guardar i accedir a firebase ja que no
    // es pot fer a través de Google Maps

    public void test() throws InterruptedException, ApiException, IOException {
        GeocodingResult[] results =  GeocodingApi.geocode(context,
            "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
        System.out.println(gson.toJson(results[0].addressComponents));
    }

    public List<PlaceDetailsEntity> searchNearby(LatLng location, String keyword, String type, int radius) throws MapsAccessException {
        NearbySearchRequest req = PlacesApi.nearbySearchQuery(context, location);
        List<PlaceDetailsEntity> result = new ArrayList<>();
        try {
            if (keyword != null) {
                 req = req.keyword(keyword);
            }
            if (type != null) {
                req = req.type(PlaceType.valueOf(type));
            }
            PlacesSearchResponse resp = req
                .radius(radius)
                .await();
            if (resp.results != null && resp.results.length > 0) {
                for (PlacesSearchResult r : resp.results) {
                    PlaceDetails details = PlacesApi.placeDetails(context,r.placeId).await();
                    PlaceDetailsEntity place = new PlaceDetailsEntity(details.name, details.formattedAddress,
                        details.geometry.location, details.rating, details.url);
                    result.add(place);
                }
            }
        } catch(Exception e) {
           throw new MapsAccessException("500", "Maps Access threw an exception: " + e.getMessage());
        }
        return result;
    }
}
