package edu.miami.schurer.ontolobridge;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableEmailTools
public class OntolobridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OntolobridgeApplication.class, args);
    }
}
