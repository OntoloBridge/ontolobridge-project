package edu.miami.schurer.ontolobridge.library;

import edu.miami.schurer.ontolobridge.Responses.DebugStatusResponse;
import edu.miami.schurer.ontolobridge.Responses.OperationResponse;
import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import edu.miami.schurer.ontolobridge.Responses.UserResponse;
import edu.miami.schurer.ontolobridge.utilities.DbUtil;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuthLibrary {

    JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    String connectionURL;

    public AuthLibrary(JdbcTemplate template){
        this.jdbcTemplate = template;
    }

    public Integer VerifyEmail(String verify) throws EmptyResultDataAccessException {
        List<Object> args = new ArrayList<>();
        args.add(verify);
        Integer userID = jdbcTemplate.queryForObject(
                "select user_id from user_details where value = ? and field = 'verification'",
                args.toArray(),(rs, rowNum) ->
                rs.getInt(1));
        return userID;
    }

}
