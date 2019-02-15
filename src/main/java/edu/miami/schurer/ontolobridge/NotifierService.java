package edu.miami.schurer.ontolobridge;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class NotifierService {

    @Scheduled(cron="*/5 * * * * ?")
    public void checkNewNotifications()
    {
        System.out.println("Method executed at every 5 seconds. Current time is :: "+ new Date());
    }

    public void sendEmailNotification(String email, String title, String Message){

    }
    public void sendGithubNotification(String repo, String title, String Message){

    }
    public void sendRESTNotification(String url, String arguments){

    }
}
