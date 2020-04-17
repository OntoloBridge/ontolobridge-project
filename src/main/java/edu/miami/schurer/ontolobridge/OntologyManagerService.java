package edu.miami.schurer.ontolobridge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.miami.schurer.ontolobridge.Responses.MaintainersObject;
import edu.miami.schurer.ontolobridge.library.NotificationLibrary;
import edu.miami.schurer.ontolobridge.utilities.AppProperties;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class OntologyManagerService {

    @Autowired
    public EmailService emailService;

    public NotificationLibrary notLib;

    //we want to know where we are sending from
    @Value("${spring.mail.username}")
    private String emailHost;

    @Value("${app.bioportal.apikey}")
    private String apiKey;

    //Named template to allow insertion of array into query in SigCSmallMoleculeLibrary
    @Autowired
    protected JdbcTemplate JDBCTemplate;

    @Autowired
    private AppProperties appProp;

    @Autowired
    public OntologyManagerService(){

    }

    @PostConstruct
    void Init(){
        notLib = new NotificationLibrary(appProp);
    }

    public List<MaintainersObject> GetMaintainers(String superclass){
        // for each request attempt to find maintainers and ontology by the short term field
        String sql1 = "select o.name as ontology_name," +
                "o.ontology_short," +
                "m.name as maintainer_name," +
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

    @Scheduled(cron="*/5 * * * * ?")
    public void checkNoParentLabel() throws IOException {
        if(apiKey.isEmpty())
            return;
        String sql = "select url,ontology_short,seperator,padding from ontologies where url IS NOT NULL";
        HashMap<String,HashMap<String,Object>> ontologies = new HashMap<>();
        JDBCTemplate.query(sql,
                (rs, rowNum) -> {
                    HashMap<String,Object> h = new HashMap<>();
                    h.put("url",rs.getString("url"));
                    h.put("ontology_short",rs.getString("ontology_short"));
                    h.put("seperator",rs.getString("seperator"));
                    h.put("padding",rs.getInt("padding"));
                    ontologies.put(rs.getString("ontology_short"),h);
                    return h;
                });
        String sql1 = "select id,superclass_id,superclass_ontology from requests where superclass_label is NULL";
        List<HashMap<String,Object>> missingParents = JDBCTemplate.query(sql1,
                (rs, rowNum) -> {
                    HashMap<String,Object> h = new HashMap<>();
                    h.put("id",rs.getString("id"));
                    h.put("superclass_ontology",rs.getString("superclass_ontology"));
                    h.put("superclass_id",rs.getInt("superclass_id"));
                    return h;
                });
        for(HashMap<String,Object> item: missingParents) {
            if(!ontologies.containsKey(item.get("superclass_ontology")))
                continue;
            String bioportalAPI = "http://data.bioontology.org/ontologies/";
            String paddingFormat= "%"+  ontologies.get(item.get("superclass_ontology").toString()).get("padding").toString()+"d";
            bioportalAPI += item.get("superclass_ontology");
            bioportalAPI += "/classes/";
            bioportalAPI += URLEncoder.encode(ontologies.get(item.get("superclass_ontology").toString()).get("url").toString()+
                    ontologies.get(item.get("superclass_ontology").toString()).get("seperator").toString()+
                    item.get("superclass_ontology").toString()+
                    "_"+String.format(paddingFormat,
                    (int)item.get("superclass_id")).replace(' ', '0'));
            bioportalAPI += "?apikey="+apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            headers.set("User-Agent", "Ontolobridge; Contact jpt55@med.miami.edu");

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity entity = new HttpEntity(headers);
            HttpEntity<String> response = restTemplate.exchange(bioportalAPI, HttpMethod.GET, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String label = root.get("prefLabel").asText();

            String sql2 = "update requests set superclass_label = ? where id = ?";
            List<Object> args = new ArrayList<>();
            args.add(label);
            args.add(item.get("id"));
            JDBCTemplate.update(sql2,args.toArray());
        }
    }

    @Scheduled(cron="0 * * * * ?")
    public void checkNewNotifications()
    {
        //SQL statement to get the relevent information of id of request, assumed ontology and type of request but only if no ontology has been assigned
        String sql = "select * from requests where (uri_ontology = '') IS NOT FALSE";

        //retrieve results and store in simple hashmap
        List<HashMap<String,Object>> unassignedRequests = JDBCTemplate.query(sql,
                (rs, rowNum) -> {
            HashMap<String,Object> h = new HashMap<>();

            h.put("id",rs.getInt("id"));
            h.put("label",rs.getString("label"));
            h.put("description",rs.getString("description"));
            h.put("superclass_ontology",rs.getString("superclass_ontology"));
            h.put("superclass_id",rs.getInt("superclass_id"));
            h.put("references",rs.getString("references"));
            h.put("justification",rs.getString("justification"));
            h.put("submitter",rs.getString("submitter"));
            h.put("uri_ontology",rs.getString("uri_ontology"));
            h.put("uri_identifier",rs.getString("uri_identifier"));
            h.put("submission_status",rs.getString("submission_status"));
            h.put("full_uri",rs.getString("full_uri"));
            h.put("uri_superclass",rs.getString("uri_superclass"));
            h.put("request_type",rs.getString("request_type"));
            return h;
        });

        //loop through hashmap of unassigned requests
        for(HashMap<String,Object> E: unassignedRequests){

            // for each request attempt to find maintainers and ontology by the short term field
            String sql1 = "select o.name as ontology_name," +
                    "o.ontology_short," +
                    "m.name as maintainer_name," +
                    "m.contact_location," +
                    "m.contact_method " +
                    "from ontologies o " +
                    "inner join ontology_to_maintainer om on o.id = om.ontology_id " +
                    "inner join maintainers m on om.maintainer_id = m.id " +
                    "where o.ontology_short = ?";

            //add the assumed superclass from the requests to parameters, will later be used to set the ontology of the requests
            List<Object> args = new ArrayList<>();
            List<MaintainersObject> maintainers = new ArrayList<>();

            //set default values
            args.add("???");
            args.add(E.get("id"));

            if(E.get("superclass") != null) {
                args.set(1,E.get("superclass").toString().toUpperCase());
                //attempt to get the list of maintainers
                maintainers = GetMaintainers(E.get("superclass").toString().toUpperCase());
            }
            //if we get maintainers notify them, otherwise assume we have no idea who this belongs to and notify the sys admins
            if(maintainers.size() == 0) {
                maintainers = GetMaintainers("???");
            }

            //update the requests to the approriate ontology
            String updateSQL = "update requests set uri_ontology = ? where id = ?";
            JDBCTemplate.update(updateSQL,args.toArray());
            System.out.println("Setting term "+E.get("id")+" to "+args.get(0));

            //queue notifications
            for (MaintainersObject m : maintainers) {
                E.put("submitter_email",m.getContact_location());
                E.put("submitter","Maintainer");
                notLib.InsertEmail(JDBCTemplate,"/emails/termSubmission.email",E);
                //NotificationLibrary.InsertNotification(JDBCTemplate, m.getContact_method(), m.getContact_location(), "A new " + E.get("type") + " has been submitted", "New " + E.get("type") + " Forms");
            }
        }
    }
}
