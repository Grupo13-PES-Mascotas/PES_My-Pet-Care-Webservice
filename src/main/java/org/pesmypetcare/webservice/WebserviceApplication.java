package org.pesmypetcare.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Santiago Del Rey
 */
@SpringBootApplication
public class WebserviceApplication {

    /**
     * Starter of the service.
     * @param args Arguments for the service.
     */
    public static void main(String[] args) {
        SpringApplication.run(WebserviceApplication.class, args);
    }

}
