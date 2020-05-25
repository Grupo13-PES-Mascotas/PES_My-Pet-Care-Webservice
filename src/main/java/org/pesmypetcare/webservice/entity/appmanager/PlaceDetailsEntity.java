package org.pesmypetcare.webservice.entity.appmanager;

import com.google.maps.model.LatLng;
import lombok.Data;

import java.net.URL;

/**
 * @author Marc Simó
 */
@Data
public class PlaceDetailsEntity {
    String name;
    String address;
    LatLng location;
    float rating;
    URL mapsUrl;

    public PlaceDetailsEntity(String name, String address, LatLng location, float rating, URL mapsUrl) {
        this.name = name;
        this.address = address;
        this.location = location;
        this.rating = rating;
        this.mapsUrl = mapsUrl;
    }
}
