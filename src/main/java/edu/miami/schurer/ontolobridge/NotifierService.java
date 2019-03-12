package edu.miami.schurer.ontolobridge;

import com.google.common.collect.Lists;
import edu.miami.schurer.ontolobridge.Responses.NotificationObject;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import java.util.HashMap;
import java.util.List;

@Service
public class NotifierService{

    @Autowired
    public EmailService emailService;

    //we want to know where we are sending from
    @Value("${spring.mail.username}")
    private String emailHost;

    //Named template to allow insertion of array into query in SigCSmallMoleculeLibrary
    @Autowired
    protected JdbcTemplate JDBCTemplate;

    @Autowired
    public NotifierService(){}

    @Scheduled(cron="0 */1 * * * ?")
    public void checkNewRequests()
    {
        String sql = "select * from notifications where sent = 0 limit 20";
        List<NotificationObject> notifications = JDBCTemplate.query(sql,
                (rs, rowNum) -> new NotificationObject(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("title"),
                        rs.getBoolean("sent"),
                        rs.getString("address"),
                        rs.getString("message"),
                        rs.getDate("createDate"),
                        rs.getDate("sentDate")));
        for (NotificationObject n: notifications) {
            if(n.getType().equals("email")){
                Object[] args = {n.getId()};
                if(sendEmailNotification(n.getAddress(), n.getTitle(), n.getMessage()))
                    System.out.println("Sending email to "+n.getAddress());
                    JDBCTemplate.update("UPDATE notifications SET sent = 1,\"sentDate\" = current_date WHERE id = ?",args);
            }
        }
    }

    @SuppressWarnings("ThrowablePrintedToSystemOut")
    public boolean sendEmailNotification(String email, String subject, String message){
        HashMap<String,String> headers = new HashMap<>();
        headers.put("X-Mailer","Ontolobridge Mailer"); //let people know what sent this message
        try {
            final Email emailObject = DefaultEmail.builder()
                    .from(new InternetAddress(emailHost, "Ontolobridge"))
                    .to(Lists.newArrayList(new InternetAddress(email)))
                    .subject(subject)
                    .body(message)
                    .customHeaders(headers)
                    .encoding("UTF-8").build();
            emailService.send(emailObject);
        } catch(Exception e){
            System.out.println("Emailer Exception:");
            System.out.println(e);
            return false;
        }
        return true;
    }
    public void sendGithubNotification(String repo, String title, String Message){

    }
    public void sendRESTNotification(String url, String arguments){

    }
}
