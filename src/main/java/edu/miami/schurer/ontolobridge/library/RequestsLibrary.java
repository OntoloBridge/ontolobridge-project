package edu.miami.schurer.ontolobridge.library;

import edu.miami.schurer.ontolobridge.NotifierService;
import edu.miami.schurer.ontolobridge.Responses.DebugStatusResponse;
import edu.miami.schurer.ontolobridge.Responses.OperationResponse;
import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RequestsLibrary {

    static public int RequestsTerm(NamedParameterJdbcTemplate jdbcTemplate,
                                       String label,
                                       String description,
                                       String superclass_uri,
                                       String superclass_ontology,
                                       String reference,
                                       String justification,
                                       String submitter,
                                       String submitter_email,
                                       boolean notify,
                                       String requestType){
        return RequestsTerm(jdbcTemplate.getJdbcTemplate(),label,description,superclass_uri,superclass_ontology,reference,justification,submitter,submitter_email,notify,"",requestType);
    }
    static public int RequestsTerm(JdbcTemplate jdbcTemplate,
                                    String label,
                                    String description,
                                    String superclass_uri,
                                    String superclass_ontology,
                                    String reference,
                                    String justification,
                                    String submitter,
                                    String submitter_email,
                                    boolean notify,
                                    String ontology,
                                    String requestType){
        String sql = "INSERT INTO requests (" +
                "`label`," +
                "`description`," +
                "`uri_superclass`," +
                "`superclass_id`," +
                "`superclass_ontology`," +
                "`reference`," +
                "`justification`," +
                "`submitter`," +
                "`submitter_email`," +
                "`notify`," +
                "`request_type`," +
                "`uri_ontology`" +
                ") VALUES ("+
                "?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> args = new ArrayList<>();
        args.add(label);
        args.add(description);



        // uri_superclass
        if(superclass_uri.contains("://")){
            args.add(superclass_uri);
            if(superclass_uri.contains("#"))
                superclass_uri = superclass_uri.substring(superclass_uri.lastIndexOf("#")+1);
            else
                superclass_uri = superclass_uri.substring(superclass_uri.lastIndexOf("/")+1);
        }else{
            args.add("");
        }





        // superclass_id
        if (superclass_uri.contains("ONTB")) {
            args.add(Integer.parseInt(superclass_uri.substring(superclass_uri.indexOf("ONTB")+5)));
        }else if(superclass_uri.contains("_")){
            args.add(Integer.parseInt(superclass_uri.substring(superclass_uri.indexOf("_")+1)));
        }else if(superclass_uri.contains(":")){
            args.add(Integer.parseInt(superclass_uri.substring(superclass_uri.indexOf(":")+1)));
        }else {
            args.add(0);
        }




        //superclass_ontology
        if (superclass_ontology.isBlank()) {
            if (superclass_uri.contains("ONTB")) {
                args.add(null);
            }else if(superclass_uri.contains("_")){
                args.add(superclass_uri.substring(0,superclass_uri.indexOf("_")));
            }else if(superclass_uri.contains(":")){
                args.add(superclass_uri.substring(0,superclass_uri.indexOf(":")));
            }else {
                args.add(null);
            }
        } else {
            args.add(superclass_ontology);
        }










        args.add(reference);
        args.add(justification);
        args.add(submitter);
        args.add(submitter_email);
        args.add(notify?1:0);
        args.add(requestType);
        args.add(ontology.isEmpty()?"":ontology);



//        Integer id = jdbcTemplate.queryForObject(sql, args.toArray(),Integer.class);

        jdbcTemplate.update(sql, args.toArray());
        Integer id = jdbcTemplate.queryForObject("select last_insert_id()", Integer.class);



        jdbcTemplate.execute("insert into request_status (request_id,current_status) VALUES("+id+",'submitted')");
        return id;
    }

    static public List<StatusResponse> TermStatus(JdbcTemplate jdbcTemplate, Integer id,String include){
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
    static public OperationResponse TermUpdateStatus(JdbcTemplate jdbcTemplate, Integer id, String status,String message){
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
