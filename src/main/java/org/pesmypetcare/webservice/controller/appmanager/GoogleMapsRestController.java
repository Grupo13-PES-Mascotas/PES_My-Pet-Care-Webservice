package org.pesmypetcare.webservice.controller.appmanager;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import org.pesmypetcare.webservice.dao.appmanager.GoogleMapsDaoImpl;
import org.pesmypetcare.webservice.error.MapsAccessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Marc Simó
 */
@RestController
@RequestMapping("/maps")
public class GoogleMapsRestController {


    /**
     * Function done only to do tests, it should be deleted before release
     */
    @GetMapping("/test")
    public void testDaos() throws MapsAccessException {
        GoogleMapsDaoImpl prova = new GoogleMapsDaoImpl();
        System.out.println(prova.searchNearby(new LatLng(40.979898,-2.104020), null, null, 50000));
    }
}
