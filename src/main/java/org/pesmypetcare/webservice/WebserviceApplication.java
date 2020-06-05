package org.pesmypetcare.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Santiago Del Rey
 */
@SpringBootApplication
public class WebserviceApplication {

    /**
     * Starter of the service.
     *
     * @param args Arguments for the service.
     */
    public static void main(String[] args) {
        SpringApplication.run(WebserviceApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:4200", "https://localhost:4200",
                    "https://my-pet-care-web.herokuapp.com", "http://my-pet-care-web.herokuapp.com");
            }
        };
    }

}
