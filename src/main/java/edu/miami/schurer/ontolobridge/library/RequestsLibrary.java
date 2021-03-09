package edu.miami.schurer.ontolobridge.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.miami.schurer.ontolobridge.NotifierService;
import edu.miami.schurer.ontolobridge.OntologyManagerService;
import edu.miami.schurer.ontolobridge.Responses.FullStatusResponse;
import edu.miami.schurer.ontolobridge.Responses.MaintainersObject;
import edu.miami.schurer.ontolobridge.Responses.OperationResponse;
import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import edu.miami.schurer.ontolobridge.models.Ontology;
import edu.miami.schurer.ontolobridge.utilities.AppProperties;
import edu.miami.schurer.ontolobridge.utilities.DbUtil;
import edu.miami.schurer.ontolobridge.utilities.UserPrinciple;
import io.sentry.Sentry;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

import static edu.miami.schurer.ontolobridge.utilities.DbUtil.genRandomString;

public class RequestsLibrary {

    Random random = new Random();

    @Value("${api.cpanel.apitoken}")
    String cpanelApiKey;

    JdbcTemplate jdbcTemplate;

    private OntologyManagerService Manager;

    private NotifierService notifier;

    private AppProperties appProp;


    NotificationLibrary notLib ;


    public RequestsLibrary(JdbcTemplate template,
                           String cpanelApiKey,
                           NotifierService notifier,
                           OntologyManagerService Manager,
                           NotificationLibrary notLib,
                           AppProperties appProp){
        this.jdbcTemplate = template;
        this.cpanelApiKey = cpanelApiKey;
        this.appProp = appProp;
        this.notifier = notifier;
        this.Manager = Manager;
        this.notLib = notLib;
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
        Long userID = ((UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId(); //cache using user ID
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
                    "assigned_ontology," +
                    "user_id"+
                    ") VALUES (" +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
            args.add(userID);

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
            HashMap<String,Object> stringReplace = new HashMap();
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

    public List<StatusResponse> TermStatus( Long id,String include){
        String sql ="";
        //For deubgging
        if(include.equals("all") || include.equals("user")| include.equals("maintainer")) //if we have a debug requests or a user requesting their own requests
            sql = "select * from requests";
        else
            sql = "select id,submission_status,uri_ontology,uri_identifier,current_message,updated_date,request_type,full_uri,assigned_ontology from requests";
        List<Object> args = new ArrayList<>();
        if(id> 0 ){
            if(include.equals("user")) //if its a user replace single id with user_id
                sql+=" where user_id = ?";
            else
                sql+=" where id = ?";
            args.add(id);
        }


        if(include.equals("all") | include.equals("user")| include.equals("maintainer")) //if we are in debug mode or a user is requesting then get all
            return jdbcTemplate.query(sql,
                    args.toArray(),
                    (rs, rowNum) -> {
                        String curie = "ONTB_"+rs.getLong("id");
                        String uri = "http://ontolobridge.org/"+curie;
                        String newURI = rs.getString("full_uri");
                        if(newURI == null){
                            newURI = "";
                        }
                        return new FullStatusResponse(
                                rs.getString("submission_status"),
                                rs.getLong("id"),
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
                                rs.getString("submitter_email"),
                                rs.getInt("notify"),
                                rs.getString("assigned_ontology"),
                                rs.getLong("user_id"));
                    });
        else
            return jdbcTemplate.query(sql,
                    args.toArray(),
                    (rs, rowNum) -> {
                        String newURI = rs.getString("full_uri");
                        if(newURI == null){
                            newURI = "";
                        }
                        String curie = "ONTB_"+rs.getLong("id");
                        String uri = "http://ontolobridge.org/"+curie;
                        return new StatusResponse(rs.getString("submission_status"),rs.getLong("id"),uri,curie,rs.getString("current_message"),newURI,"",rs.getString("request_type"),rs.getTimestamp("updated_date").getTime(),rs.getDate("updated_date").toString(),rs.getString("assigned_ontology"));
                    });
    }
    public OperationResponse TermUpdateStatus(Integer id, String status,String message){
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
            args.add(id);
            args.add(status);
            args.add(message);
            jdbcTemplate.update("insert into request_status (request_id,current_status,message) VALUES(?,?,?)",args.toArray());
        }catch(Exception e){
            return new OperationResponse("failure",false,id);
        }


        return new OperationResponse("success",true,id);
    }
    public List<Map<String,Object>> TermHistory(Long id){
        String sql = "select * from request_status where request_id = ?";

        List<Object> args = new ArrayList<>();
        args.add(id);
        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql, args.toArray());
        return result;
    }

    public List<Map<String, Object>> GetAllOntologies(){
        String sql = "select id,name,url,ontology_short,seperator,padding from ontologies where id > 0";
        return jdbcTemplate.queryForList(sql);
    }
    public Object TermUpdate(String id, Map<String, String> parameters) {
        long long_id = Long.parseLong(id);
        try {
            List<Object> args = new ArrayList<>();
            args = new ArrayList<>();
            args.add(long_id);
            StringBuilder SQL;
            List<String> columns= new ArrayList<>();
            StringBuilder message = new StringBuilder();
            List<String> current_status = jdbcTemplate.<String>query("SELECT * from requests where id = ?",args.toArray(), (rs, rowNum) -> {
                for(int i = 1; i<=rs.getMetaData().getColumnCount(); i++) {
                    columns.add(rs.getMetaData().getColumnName(i));
                }
                return rs.getString("submission_status");
            });
            args.clear();
            SQL = new StringBuilder("UPDATE requests SET ");
            for(Map.Entry<String,String> entry:parameters.entrySet()){
                String key = entry.getKey();
                if(key.equals("id")){
                    continue;
                }
                if(key.equals("current_status")){
                    key = "submission_status";
                }
                if(columns.contains(entry.getKey())){
                    SQL.append(columns.get(columns.indexOf(entry.getKey().toLowerCase()))).append(" = ?, ");

                    //if the current status is comment then use the status in the database
                    if(!key.equals("submission_status") || entry.getValue().equals("comment")) {
                        args.add(entry.getValue());
                    }else{
                        args.add(current_status.get(0));
                    }
                    message.append("updated ").append(columns.get(columns.indexOf(key))).append(" to ").append(entry.getValue()).append("\n");
                }
            }
            args.add(long_id);
            SQL.delete(SQL.length()-2,SQL.length()); //remove extra comma
            SQL.append(" WHERE id = ?");
            jdbcTemplate.update(SQL.toString(),args.toArray());
            args.clear();
            args.add(long_id);
            args.add(parameters.get("current_status"));
            args.add(message);
            jdbcTemplate.update("insert into request_status (request_id,current_status,message) VALUES(?,?,?)",args.toArray());
        }catch(Exception e){
            System.out.println(e);
            return new OperationResponse("failure",false,long_id);
        }
        return new OperationResponse("success",true,long_id);
    }
}
