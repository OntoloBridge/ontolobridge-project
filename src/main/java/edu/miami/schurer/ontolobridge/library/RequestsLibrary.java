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
                                       String superclass_id,
                                       String references,
                                       String justification,
                                       String submitter,
                                       String submitter_email,
                                       boolean notify,
                                       String type){
        return RequestsTerm(jdbcTemplate.getJdbcTemplate(),label,description,superclass_uri,references,justification,submitter,submitter_email,notify,"",type);
    }
    static public int RequestsTerm(JdbcTemplate jdbcTemplate,
                                    String label,
                                    String description,
                                    String superclass_uri,
                                    String references,
                                    String justification,
                                    String submitter,
                                    String submitter_email,
                                    boolean notify,
                                    String ontology,
                                    String type){
        String sql = "INSERT INTO requests (" +
                "\"label\"," +
                "\"description\"," +
                "\"superclass_uri\"," +
                "\"superclass_ontology\"," +
                "\"superclass_id\"," +
                "\"references\"," +
                "\"justification\"," +
                "\"submitter\"," +
                "\"submitter_email\"," +
                "\"notify\"," +
                "\"type\"," +
                "\"uri_ontology\"" +
                ")VALUES("+
                "?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id;";
        List<Object> args = new ArrayList<>();
        args.add(label);
        args.add(description);
        if(superclass_uri.contains("://")){
            args.add(superclass_uri);
            if(superclass_uri.contains("#"))
                superclass_uri = superclass_uri.substring(superclass_uri.lastIndexOf("#")+1);
            else
                superclass_uri = superclass_uri.substring(superclass_uri.lastIndexOf("/")+1);
        }else{
            args.add("");
        }
        if(superclass_uri.contains("ONTB")){
            args.add(null);
            args.add(Integer.parseInt(superclass_uri.substring(superclass_uri.indexOf("ONTB")+5)));
        }else if(superclass_uri.contains("_")){
            args.add(superclass_uri.substring(0,superclass_uri.indexOf("_")));
            args.add(Integer.parseInt(superclass_uri.substring(superclass_uri.indexOf("_")+1)));
        }else if(superclass_uri.contains(":")){
            args.add(superclass_uri.substring(0,superclass_uri.indexOf(":")));
            args.add(Integer.parseInt(superclass_uri.substring(superclass_uri.indexOf(":")+1)));
        }else {
            args.add(null);
            args.add(0);
        }
        args.add(references);
        args.add(justification);
        args.add(submitter);
        args.add(submitter_email);
        args.add(notify?1:0);
        args.add(type);
        args.add(ontology.isEmpty()?"":ontology);
        Integer id = jdbcTemplate.queryForObject(sql,args.toArray(),Integer.class);

        jdbcTemplate.execute("insert into \"requestsStatus\" (\"requestID\",\"status\") VALUES("+id+",'submitted')");
        return id;
    }

    static public List<StatusResponse> TermStatus(JdbcTemplate jdbcTemplate, Integer id,String include){
        String sql ="";
        //For deubgging
        if(include.equals("all"))
            sql = "select * from requests";
        else
            sql = "select id,status,uri_ontology,uri_identifier,current_message,last_updated,type from requests";
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
                        return new DebugStatusResponse(
                                rs.getString("status"),
                                id,
                                uri,
                                curie,
                                rs.getString("current_message"),
                                "",
                                "",
                                rs.getString("type"),
                                rs.getTimestamp("last_updated").getTime(),
                                rs.getDate("last_updated").toString(),
                                rs.getString("label"),
                                rs.getString("description"),
                                rs.getString("superclass_ontology"),
                                rs.getString("superclass_id"),
                                rs.getString("references"),
                                rs.getString("justification"),
                                rs.getString("submitter"),
                                rs.getInt("notify"));
                    });
        else
            return jdbcTemplate.query(sql,
                    args.toArray(),
                    (rs, rowNum) -> {
                        String curie = "ONTB_"+id;
                        String uri = "http://ontolobridge.org/"+curie;
                        return new StatusResponse(rs.getString("status"),id,uri,curie,rs.getString("current_message"),"","",rs.getString("type"),rs.getTimestamp("last_updated").getTime(),rs.getDate("last_updated").toString());
                    });
    }
    static public OperationResponse TermUpdateStatus(JdbcTemplate jdbcTemplate, Integer id, String status,String message){
        String sql = "UPDATE requests  SET  status = ?::status, current_message = ? WHERE id = ?";
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
            jdbcTemplate.update("insert into \"requestsStatus\" (\"requestID\",\"status\") VALUES("+id+",?)",args);
        }catch(Exception e){
            return new OperationResponse("failure",false,id);
        }


        return new OperationResponse("success",true,id);
    }

}
