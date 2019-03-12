package edu.miami.schurer.ontolobridge.library;

import edu.miami.schurer.ontolobridge.Responses.OperationResponse;
import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationLibrary {

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
}
