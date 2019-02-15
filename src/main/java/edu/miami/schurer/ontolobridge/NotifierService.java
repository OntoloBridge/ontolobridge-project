package edu.miami.schurer.ontolobridge;

import edu.miami.schurer.ontolobridge.utilities.EmailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Properties;


import com.sun.mail.smtp.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class NotifierService {

    private EmailProperties emailProperties;


    @Autowired
    public NotifierService(EmailProperties emailProperties) {
        emailProperties = emailProperties;
    }

    @Scheduled(cron="0 */5 * * * ?")
    public void checkNewNotifications()
    {
        System.out.println("Method executed at every 5 minutes. Current time is :: "+ new Date());
    }

    public void sendEmailNotification(String email, String subject, String message) throws Exception{
        Properties props = System.getProperties();
        props.put("mail.smtps.host",emailProperties.getHost());
        props.put("mail.smtps.auth","true");
        Session session = Session.getInstance(props, null);
        javax.mail.Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(emailProperties.getUsername()));;
        msg.setRecipients(javax.mail.Message.RecipientType.TO,
                InternetAddress.parse(email, false));
        msg.setSubject(subject);
        msg.setText(message);
        msg.setHeader("X-Mailer", "ontolobridge");
        msg.setSentDate(new Date());
        SMTPTransport t =
                (SMTPTransport)session.getTransport("smtps");
        t.connect(emailProperties.getHost(), emailProperties.getUsername(), emailProperties.getPassword());
        t.sendMessage(msg, msg.getAllRecipients());
        System.out.println("Response: " + t.getLastServerResponse());
        t.close();
    }
    public void sendGithubNotification(String repo, String title, String Message){

    }
    public void sendRESTNotification(String url, String arguments){

    }
}
