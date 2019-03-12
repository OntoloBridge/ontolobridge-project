package edu.miami.schurer.ontolobridge;

import com.google.common.collect.Lists;
import edu.miami.schurer.ontolobridge.Responses.MaintainersObject;
import edu.miami.schurer.ontolobridge.Responses.NotificationObject;
import edu.miami.schurer.ontolobridge.library.NotificationLibrary;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OntologyManagerService {

    @Autowired
    public EmailService emailService;

    //we want to know where we are sending from
    @Value("${spring.mail.username}")
    private String emailHost;

    //Named template to allow insertion of array into query in SigCSmallMoleculeLibrary
    @Autowired
    protected JdbcTemplate JDBCTemplate;

    @Autowired
    public OntologyManagerService(){}

    @Scheduled(cron="0 */1 * * * ?")
    public void checkNewNotifications()
    {
        String sql = "select id,superclass_ontology,type from requests where uri_ontology is NULL";
        List<HashMap<String,Object>> notifications = JDBCTemplate.query(sql,
                (rs, rowNum) -> {
            HashMap<String,Object> h = new HashMap<>();
            h.put("id",rs.getInt(1));
            h.put("superclass",rs.getString(2));
            h.put("type",rs.getString(3));
            return h;
        });
        for(HashMap<String,Object> E: notifications){
            String sql1 = "select o.\"name\" as ontology_name," +
                    "o.ontology_short," +
                    "m.\"name\" as maintainer_name," +
                    "m.contact_location," +
                    "m.contact_method " +
                    "from ontologies o " +
                    "inner join ontology_to_maintainer om on o.id = om.ontology_id " +
                    "inner join maintainers m on om.maintainer_id = m.id " +
                    "where o.ontology_short = ?";

            List<Object> args = new ArrayList<>();
            if(E.get("superclass") != null){
                args.add(E.get("superclass").toString().toUpperCase());
            }else{
                args.add("");
            }
            List<MaintainersObject> maintainers = JDBCTemplate.query(sql1,args.toArray(),
                    (rs, rowNum) -> new MaintainersObject(rs));
            args.add(E.get("id"));
            if(maintainers.size() > 0) {
                String updateSQL = "update requests set uri_ontology = ? where id = ?";
                System.out.println("Setting term "+E.get("id")+" to "+args.get(0));
                JDBCTemplate.update(updateSQL,args.toArray());
                for (MaintainersObject m : maintainers) {
                    NotificationLibrary.InsertNotification(JDBCTemplate, m.getContact_method(), m.getContact_location(), "A new " + E.get("type") + " has been submitted", "New " + E.get("type") + " Requests");
                }
            }else{
                sql1 = "select o.\"name\" as ontology_name," +
                        "o.ontology_short," +
                        "m.\"name\" as maintainer_name," +
                        "m.contact_location," +
                        "m.contact_method " +
                        "from ontologies o " +
                        "inner join ontology_to_maintainer om on o.id = om.ontology_id " +
                        "inner join maintainers m on om.maintainer_id = m.id " +
                        "where o.id = 0";

                maintainers = JDBCTemplate.query(sql1,
                        (rs, rowNum) -> new MaintainersObject(rs));
                args.set(0,"???");
                String updateSQL = "update requests set uri_ontology = ? where id = ?";
                System.out.println("Setting term "+E.get("id")+" to "+args.get(0));
                JDBCTemplate.update(updateSQL,args.toArray());
                for (MaintainersObject m : maintainers) {
                    NotificationLibrary.InsertNotification(JDBCTemplate, m.getContact_method(), m.getContact_location(), "A new " + E.get("type") + " has been submitted without any known ontology", "New " + E.get("type") + " Requests");
                }
            }
        }
    }
}
