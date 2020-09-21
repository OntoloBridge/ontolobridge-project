package edu.miami.schurer.ontolobridge.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.miami.schurer.ontolobridge.NotifierService;
import edu.miami.schurer.ontolobridge.OntologyManagerService;
import edu.miami.schurer.ontolobridge.Responses.DebugStatusResponse;
import edu.miami.schurer.ontolobridge.Responses.MaintainersObject;
import edu.miami.schurer.ontolobridge.Responses.OperationResponse;
import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import edu.miami.schurer.ontolobridge.utilities.AppProperties;
import edu.miami.schurer.ontolobridge.utilities.DbUtil;
import io.sentry.Sentry;
import javassist.bytecode.stackmap.BasicBlock;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.miami.schurer.ontolobridge.utilities.DbUtil.genRandomString;

public class RequestsLibrary {

    Random random = new Random();

    @Value("${api.cpanel.apitoken}")
    String cpanelApiKey;

    JdbcTemplate jdbcTemplate;

    @Autowired
    public OntologyManagerService Manager;

    @Autowired
    public NotifierService notifier;

    @Autowired
    private AppProperties appProp;


    NotificationLibrary notLib ;

    @PostConstruct
    void Init(){
        notLib = new NotificationLibrary(appProp);
    }

    public RequestsLibrary(JdbcTemplate template,String cpanelApiKey){
        this.jdbcTemplate = template;
        this.cpanelApiKey = cpanelApiKey;
    }
    private String genRandomEmail() {
        return genRandomString(10)+"@ontolobridge.org";
    }

    public int RequestsTerm(String label,
                                    String description,
                                    String uri_superclass,
                                    String superclass_ontology,
                                    String reference,
                                    String justification,
                                    String submitter,
                                    String submitter_email,
                                    boolean anonymize,
                                    boolean notify,
                                    String ontology,
                                    String requestType){
        Integer id = null;
        boolean addedEmail = false;
        String emailDeleteTemp = "";
        try {
            if (anonymize) {
                String randomEmail = genRandomEmail(); //generate a random anonymous email
                try {
                    String cpanelEmailRequests = "https://server201.web-hosting.com:2083/execute/Email/add_forwarder?domain=" +
                            "ontolobridge.org&email=" +
                            randomEmail +
                            "&fwdopt=fwd&fwdemail=" +
                            submitter_email;

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    httpHeaders.set("Authorization", "cpanel " + cpanelApiKey);//authenticate with cpanel token

                    HttpEntity entity = new HttpEntity(httpHeaders);

                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<String> response =
                            restTemplate.exchange(cpanelEmailRequests, HttpMethod.GET, entity, String.class); //request forwarder
                    String Response = response.getBody();
                    ObjectMapper objectMapper = new ObjectMapper();
                    HashMap myMap = objectMapper.readValue(Response, HashMap.class);
                    if (myMap.get("errors") != null) { //check if there is an error
                        return -1;
                    }
                    addedEmail = true;
                    emailDeleteTemp  =submitter_email;
                    submitter_email = randomEmail; //discard previous email

                    //JsonNode jsonMap = response.getBody();

                } catch (Exception e) {
                    System.out.println(e);
                    return 0;
                }
            }
            boolean isMySQL = DbUtil.isMySQL();
            String sql = "INSERT INTO requests (" +
                    "label," +
                    "description," +
                    "uri_superclass," +
                    "superclass_id," +
                    "superclass_ontology," +
                    "reference," +
                    "justification," +
                    "submitter," +
                    "submitter_email," +
                    "notify," +
                    "request_type," +
                    "uri_ontology" +
                    ") VALUES (" +
                    "?,?,?,?,?,?,?,?,?,?,?,?)";

            if (!isMySQL) {
                sql += " RETURNING id;";
            }

            List<Object> args = new ArrayList<>();
            args.add(label);
            args.add(description);

            // uri_superclass
            if (uri_superclass.contains("://")) {
                args.add(uri_superclass);
                if (uri_superclass.contains("#"))
                    uri_superclass = uri_superclass.substring(uri_superclass.lastIndexOf("#") + 1);
                else
                    uri_superclass = uri_superclass.substring(uri_superclass.lastIndexOf("/") + 1);
            } else {
                args.add("");
            }

            // superclass_id
            if (uri_superclass.contains("ONTB")) {
                args.add(Integer.parseInt(uri_superclass.substring(uri_superclass.indexOf("ONTB") + 5)));
            } else if (uri_superclass.contains("_")) {
                args.add(Integer.parseInt(uri_superclass.substring(uri_superclass.indexOf("_") + 1)));
            } else if (uri_superclass.contains(":")) {
                args.add(Integer.parseInt(uri_superclass.substring(uri_superclass.indexOf(":") + 1)));
            } else {
                args.add(0);
            }

            //superclass_ontology
            if (superclass_ontology.isEmpty()) {
                if (uri_superclass.contains("ONTB")) {
                    args.add(null);
                } else if (uri_superclass.contains("_")) {
                    args.add(uri_superclass.substring(0, uri_superclass.indexOf("_")));
                } else if (uri_superclass.contains(":")) {
                    args.add(uri_superclass.substring(0, uri_superclass.indexOf(":")));
                } else {
                    args.add(null);
                }
            } else {
                args.add(superclass_ontology);
            }

            args.add(reference);
            args.add(justification);
            args.add(submitter);
            args.add(submitter_email);
            args.add(notify ? 1 : 0);
            args.add(requestType);
            args.add(ontology.isEmpty() ? "" : ontology);

            if (isMySQL) {
                jdbcTemplate.update(sql, args.toArray());
                id = jdbcTemplate.queryForObject("select last_insert_id()", Integer.class);
            } else {
                id = jdbcTemplate.queryForObject(sql, args.toArray(), Integer.class);
            }

            jdbcTemplate.execute("insert into request_status (request_id,current_status) VALUES(" + id + ",'submitted')");
        }catch (Exception e){
            if(addedEmail){
                String cpanelEmailRequests = "https://server201.web-hosting.com:2083/execute/Email/delete_forwarder?" +
                        "address=" +
                        submitter_email +
                        "&forwarder=" +
                        emailDeleteTemp;

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                httpHeaders.set("Authorization", "cpanel " + cpanelApiKey);//authenticate with cpanel token

                HttpEntity entity = new HttpEntity(httpHeaders);

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response =
                        restTemplate.exchange(cpanelEmailRequests, HttpMethod.GET, entity, String.class); //request forwarder
                String Response = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    HashMap myMap = objectMapper.readValue(Response, HashMap.class);
                    if (myMap.get("errors") != null) { //check if there is an error
                        return -1;
                    }
                }catch (Exception mapException){
                    System.out.println(mapException);
                }
            }
        }

        if(ontology != null && !ontology.isEmpty()){
            List<MaintainersObject> maintainers = Manager.GetMaintainers(ontology);
            //queue notifications
            String email = "New Ontolobridge Requests Submitted";
            HashMap<String,String> stringReplace = new HashMap();
            try{
                email = IOUtils.toString(new ClassPathResource("/emails/termSubmission-Maintainer.email").getInputStream(), "UTF-8");
                stringReplace.put("__label__",label);
                stringReplace.put("__description__",description);
                stringReplace.put("__uri_superclass__",uri_superclass);
                stringReplace.put("__reference__",reference);
                stringReplace.put("__justification__",justification);
                stringReplace.put("__request_type__",requestType);
                stringReplace.put("__statusapi__",appProp.getApiURL());
                stringReplace.put("__site__",appProp.getSiteURL());
                stringReplace.put("__ticketID__",id.toString());
            }catch(IOException e){
                System.out.println("Email Exception");
                Sentry.capture(e);

            }
            for (MaintainersObject m : maintainers) {
                stringReplace.put("__user_name__",m.getContact_location());
                email = notLib.formatMessage(email,stringReplace);
                notLib.InsertNotification(jdbcTemplate, m.getContact_method(), m.getContact_location(), email, "New Ontolobridge term");
            }
            if(submitter_email != null && !submitter.isEmpty() && notify){
                try{
                    email = IOUtils.toString(new ClassPathResource("/emails/termSubmission.email").getInputStream(), "UTF-8");
                }catch(IOException e){
                    System.out.println("Email Exception");
                    Sentry.capture(e);
                }
                stringReplace.put("__user_name__",submitter_email);
                email = notLib.formatMessage(email,stringReplace);
                notLib.InsertNotification(jdbcTemplate, "email", submitter_email, email, "New Ontolobridge term");
            }
        }
        return id;
    }

    public List<StatusResponse> TermStatus(JdbcTemplate jdbcTemplate, Integer id,String include){
        String sql ="";
        //For deubgging
        if(include.equals("all"))
            sql = "select * from requests";
        else
            sql = "select id,submission_status,uri_ontology,uri_identifier,current_message,updated_date,request_type,full_uri from requests";
        List<Object> args = new ArrayList<>();
        if(id> 0 ){
            sql+=" where id = ?";
            args.add(id);
        }


        if(include.equals("all"))
            return jdbcTemplate.query(sql,
                    args.toArray(),
                    (rs, rowNum) -> {
                        String curie = "ONTB_"+id;
                        String uri = "http://ontolobridge.org/"+curie;
                        String newURI = rs.getString("full_uri");
                        if(newURI == null){
                            newURI = "";
                        }
                        return new DebugStatusResponse(
                                rs.getString("submission_status"),
                                id,
                                uri,
                                curie,
                                rs.getString("current_message"),
                                newURI,
                                "",
                                rs.getString("request_type"),
                                rs.getTimestamp("updated_date").getTime(),
                                rs.getDate("updated_date").toString(),
                                rs.getString("label"),
                                rs.getString("description"),
                                rs.getString("superclass_ontology"),
                                rs.getString("superclass_id"),
                                rs.getString("reference"),
                                rs.getString("justification"),
                                rs.getString("submitter"),
                                rs.getInt("notify"));
                    });
        else
            return jdbcTemplate.query(sql,
                    args.toArray(),
                    (rs, rowNum) -> {
                        String newURI = rs.getString("full_uri");
                        if(newURI == null){
                            newURI = "";
                        }
                        String curie = "ONTB_"+id;
                        String uri = "http://ontolobridge.org/"+curie;
                        return new StatusResponse(rs.getString("submission_status"),id,uri,curie,rs.getString("current_message"),newURI,"",rs.getString("request_type"),rs.getTimestamp("updated_date").getTime(),rs.getDate("updated_date").toString());
                    });
    }
    public OperationResponse TermUpdateStatus(JdbcTemplate jdbcTemplate, Integer id, String status,String message){
        String sql = "UPDATE requests  SET  submission_status = ?::status, current_message = ? WHERE id = ?";
        List<Object> args = new ArrayList<>();
        args.add(status);
        args.add(message);
        args.add(id);
        try {
            jdbcTemplate.update(sql, args.toArray());
        }catch(Exception e){
            return new OperationResponse("failure",false,id);
        }
        try {
            args = new ArrayList<>();
            args.add(status);
            jdbcTemplate.update("insert into request_status (request_id,current_status) VALUES("+id+",?)",args.toArray());
        }catch(Exception e){
            return new OperationResponse("failure",false,id);
        }


        return new OperationResponse("success",true,id);
    }

}
