package org.pesmypetcare.webservice.dao.appmanager;

import com.google.maps.errors.ApiException;

import java.io.IOException;

/**
 * @author Marc Simó
 */
public interface GoogleMapsDao {
    void test() throws InterruptedException, ApiException, IOException;
}
