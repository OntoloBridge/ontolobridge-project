package edu.miami.schurer.ontolobridge.library;

import edu.miami.schurer.ontolobridge.Responses.DebugStatusResponse;
import edu.miami.schurer.ontolobridge.Responses.OperationResponse;
import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import edu.miami.schurer.ontolobridge.Responses.UserResponse;
import edu.miami.schurer.ontolobridge.utilities.DbUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuthLibrary {

    JdbcTemplate jdbcTemplate;

    public AuthLibrary(JdbcTemplate template){
        this.jdbcTemplate = template;
    }

    public UserResponse CurrentUser(int id){
        List<Object> args = new ArrayList<>();
        args.add(id);
        return jdbcTemplate.queryForObject("select * from auth where id = ?",args.toArray(),(rs, rowNum) ->
                new UserResponse(rs));
    }

/*    public boolean CheckPassword(int id,String Password){
        List<Object> args = new ArrayList<>();
        args.add(id);
        return jdbcTemplate.queryForObject("select * from auth where id = ?",args.toArray(),(rs, rowNum) ->
                new UserResponse(rs););
    }*/

/*
    public List<StatusResponse> Register(String Password, String email){

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
*/

}
