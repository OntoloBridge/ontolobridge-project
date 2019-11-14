package edu.miami.schurer.ontolobridge.library;

import edu.miami.schurer.ontolobridge.utilities.AppProperties;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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

    static public int InsertNotification(JdbcTemplate jdbcTemplate,
                                         String type,
                                         String address,
                                         String message,
                                         String title){
        List<Object> args = new ArrayList<>();
        String sql = "insert into notifications (\"type\",\"address\",\"message\",\"title\",\"createDate\") values(?,?,?,?,current_date) RETURNING id";
        args.add(type);
        args.add(address);
        args.add(message);
        args.add(title);
        Integer id = jdbcTemplate.queryForObject(sql,args.toArray(),Integer.class);
        return id;
    }
    public int InsertEmail(JdbcTemplate jdbcTemplate,String emailTemplate, HashMap<String,Object> values){
        return InsertEmail(jdbcTemplate,emailTemplate,
                values.get("label").toString(),
                values.get("description").toString(),
                values.get("superclass_uri").toString(),
                values.get("references").toString(),
                values.get("justification").toString(),
                values.get("submitter").toString(),
                values.get("submitter_email").toString(),
                values.get("type").toString(),
                values.get("id").toString()
        );
    }

    public int InsertEmail(JdbcTemplate jdbcTemplate,
                                            String emailTemplate,
                                            String label,
                                            String description,
                                            String superclass_uri,
                                            String references,
                                            String justification,
                                            String submitter,
                                            String submitter_email,
                                            String type,
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
            stringReplace.put("__superclass_uri__",superclass_uri);
            stringReplace.put("__references__",references);
            stringReplace.put("__justification__",justification);
            stringReplace.put("__type__",type);
            stringReplace.put("__statusapi__",appProp.getApiURL());
            stringReplace.put("__site__",appProp.getSiteURL());
            stringReplace.put("__ticketID__",ID);

            email = formatEmail(email,stringReplace);
        }catch (Exception e){
            System.out.println(e);
        }
        return InsertNotification(jdbcTemplate,"email",submitter_email,email,"New Requests");
    }
    private String formatEmail(String email, HashMap<String,String> keys){
        for (Map.Entry<String, String> entry : keys.entrySet()
             ) {
            email = email.replace(entry.getKey(), entry.getValue());

        }
        return email;
    }
}
