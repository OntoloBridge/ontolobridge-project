package edu.miami.schurer.ontolobridge.library;

import com.sun.org.apache.xpath.internal.operations.Bool;
import edu.miami.schurer.ontolobridge.utilities.AppProperties;
import edu.miami.schurer.ontolobridge.utilities.DbUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotificationLibrary {

    private AppProperties appProp;

    public NotificationLibrary(AppProperties appProp) {
        this.appProp = appProp;
    }

    public int InsertNotification(JdbcTemplate jdbcTemplate,
                                  String notificationMethod,
                                  String address,
                                  String message,
                                  String title){
        List<Object> args = new ArrayList<>();
        String sql = "insert into notifications (notification_method,address,message,title,created_date) values (?,?,?,?,current_date)";
        boolean isMySQL = DbUtil.isMySQL();

        if (!isMySQL) {
            sql += " RETURNING id;";
        }

        args.add(notificationMethod);
        args.add(address);
        args.add(message);
        args.add(title);
        Integer id = null;

        if (isMySQL) {
            jdbcTemplate.update(sql, args.toArray());
            id = jdbcTemplate.queryForObject("select last_insert_id()", Integer.class);
        } else {
            id = jdbcTemplate.queryForObject(sql, args.toArray(), Integer.class);
        }

        return id;
    }
    public int InsertTermEmail(JdbcTemplate jdbcTemplate, String emailTemplate, HashMap<String,Object> values){
        return InsertEmail(jdbcTemplate,emailTemplate,"New Requests",values.get("submitter_email").toString(),values);
    }

    public int InsertTermEmail(JdbcTemplate jdbcTemplate,
                               String emailTemplate,
                               String label,
                               String description,
                               String uri_superclass,
                               String reference,
                               String justification,
                               String submitter,
                               String submitter_email,
                               String request_type,
                               String ID){
        HashMap<String,Object> values = new HashMap<>();
        values.put("label",label);
        values.put("description",description);
        values.put("uri_superclass",uri_superclass);
        values.put("references",reference);
        values.put("justification",justification);
        values.put("submitter",submitter);
        values.put("submitter_email",submitter_email);
        values.put("request_type",request_type);
        values.put("id",ID);
        return InsertTermEmail(jdbcTemplate,emailTemplate,values);
    }

    public int InsertEmail(JdbcTemplate jdbcTemplate,

                                String emailTemplate,
                                String title,
                                String address,
                                HashMap<String,Object> values){
        String email = "";
        try{
            email = IOUtils.toString(new ClassPathResource(emailTemplate).getInputStream(), "UTF-8");
        }catch(IOException e){
            System.out.println("Email Exception");
            return 0;
        }
        try {

            email = formatMessage(email,values);
        }catch (Exception e){
            System.out.println(e);
        }
        return InsertNotification(jdbcTemplate,"email",address,email,title);
    }
    public String formatMessage(String email, HashMap<String,Object> keys){
        for (Map.Entry<String, Object> entry : keys.entrySet()
        ) {
            email = email.replace(entry.getKey(), entry.getValue().toString());

        }
        return email;

    }
}
