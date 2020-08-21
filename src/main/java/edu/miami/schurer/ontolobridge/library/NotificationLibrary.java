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

    public int InsertEmail(JdbcTemplate jdbcTemplate,String emailTemplate, HashMap<String,Object> values){




        return InsertEmail(jdbcTemplate,emailTemplate,
                values.get("label").toString(),
                values.get("description").toString(),
                values.get("uri_superclass").toString(),
                values.get("references").toString(),
                values.get("justification").toString(),
                values.get("submitter").toString(),
                values.get("submitter_email").toString(),
                values.get("request_type").toString(),
                values.get("id").toString()
        );
    }

    public int InsertEmail(JdbcTemplate jdbcTemplate,
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
        String email = "";
        try{
            email = IOUtils.toString(new ClassPathResource(emailTemplate).getInputStream(), "UTF-8");
        }catch(IOException e){
            System.out.println("Email Exception");
        }
        try {
            HashMap<String,String> stringReplace = new HashMap();
            stringReplace.put("__user_name__",submitter);
            stringReplace.put("__label__",label);
            stringReplace.put("__description__",description);
            stringReplace.put("__uri_superclass__",uri_superclass);
            stringReplace.put("__reference__",reference);
            stringReplace.put("__justification__",justification);
            stringReplace.put("__request_type__",request_type);
            stringReplace.put("__statusapi__",appProp.getApiURL());
            stringReplace.put("__site__",appProp.getSiteURL());
            stringReplace.put("__ticketID__",ID);

            email = formatMessage(email,stringReplace);
        }catch (Exception e){
            System.out.println(e);
        }
        return InsertNotification(jdbcTemplate,"email",submitter_email,email,"New Requests");
    }
    public String formatMessage(String message, HashMap<String,String> keys){
        for (Map.Entry<String, String> entry : keys.entrySet()
             ) {
            message = message.replace(entry.getKey(), entry.getValue());

        }
        return message;
    }
}
