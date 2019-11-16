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

    public List<MaintainersObject> GetMaintainers(String superclass){
        // for each request attempt to find maintainers and ontology by the short term field
        String sql1 = "select o.`name` as ontology_name," +
                "o.ontology_short," +
                "m.`name` as maintainer_name," +
                "m.contact_location," +
                "m.contact_method " +
                "from ontologies o " +
                "inner join ontology_to_maintainer om on o.id = om.ontology_id " +
                "inner join maintainers m on om.maintainer_id = m.id " +
                "where o.ontology_short = ?";

        //add the assumed superclass from the requests to parameters, will later be used to set the ontology of the requests
        List<Object> args = new ArrayList<>();
        if(superclass != null && !superclass.isEmpty()){
            args.add(superclass.toUpperCase());
        }else{
            return new ArrayList<MaintainersObject>();
        }

        //attempt to get the list of maintainers
        List<MaintainersObject> maintainers = JDBCTemplate.query(sql1,args.toArray(),
                (rs, rowNum) -> new MaintainersObject(rs));
        return maintainers;
    }

    @Scheduled(cron="0 */1 * * * ?")
    public void checkNewNotifications()
    {
        //SQL statement to get the relevent information of id of request, assumed ontology and type of request but only if no ontology has been assigned
        String sql = "select id,superclass_ontology,request_type from requests where (uri_ontology = '') IS NOT FALSE";

        //retrieve results and store in simple hashmap
        List<HashMap<String,Object>> unassignedRequests = JDBCTemplate.query(sql,
                (rs, rowNum) -> {
            HashMap<String,Object> h = new HashMap<>();
            h.put("id",rs.getInt(1));
            h.put("superclass",rs.getString(2));
            h.put("request_type",rs.getString(3));
            return h;
        });

        //loop through hashmap of unassigned requests
        for(HashMap<String,Object> E: unassignedRequests){

            // for each request attempt to find maintainers and ontology by the short term field
            String sql1 = "select o.`name` as ontology_name," +
                    "o.ontology_short," +
                    "m.`name` as maintainer_name," +
                    "m.contact_location," +
                    "m.contact_method " +
                    "from ontologies o " +
                    "inner join ontology_to_maintainer om on o.id = om.ontology_id " +
                    "inner join maintainers m on om.maintainer_id = m.id " +
                    "where o.ontology_short = ?";

            //add the assumed superclass from the requests to parameters, will later be used to set the ontology of the requests
            List<Object> args = new ArrayList<>();

            if(E.get("superclass") == null)
                continue;
            args.add(E.get("superclass").toString().toUpperCase());
            //attempt to get the list of maintainers
            List<MaintainersObject> maintainers = GetMaintainers(E.get("superclass").toString().toUpperCase());

            //For the next part of assigning an onology assign the ID of the requests to parameters
            args.add(E.get("id"));

            //if we get maintainers notify them, otherwise assume we have no idea who this belongs to and notify the sys admins
            if(maintainers.size() == 0) {
                sql1 = "select o.`name` as ontology_name," +
                        "o.ontology_short," +
                        "m.`name` as maintainer_name," +
                        "m.contact_location," +
                        "m.contact_method " +
                        "from ontologies o " +
                        "inner join ontology_to_maintainer om on o.id = om.ontology_id " +
                        "inner join maintainers m on om.maintainer_id = m.id " +
                        "where o.id = 0";

                maintainers = GetMaintainers("???");
                args.set(0,"???");
            }

            //update the requests to the approriate ontology
            String updateSQL = "update requests set uri_ontology = ? where id = ?";
            JDBCTemplate.update(updateSQL,args.toArray());
            System.out.println("Setting term "+E.get("id")+" to "+args.get(0));

            //queue notifications
            for (MaintainersObject m : maintainers) {
                NotificationLibrary.InsertNotification(JDBCTemplate, m.getContact_method(), m.getContact_location(), "A new " + E.get("type") + " has been submitted", "New " + E.get("type") + " Forms");
            }
        }
    }
}
