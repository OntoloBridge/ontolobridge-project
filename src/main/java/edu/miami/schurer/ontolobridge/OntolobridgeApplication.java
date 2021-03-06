package edu.miami.schurer.ontolobridge;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.servlet.HandlerExceptionResolver;

@SpringBootApplication
@EnableScheduling
@EnableEmailTools
public class OntolobridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OntolobridgeApplication.class, args);
    }


    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }
}
