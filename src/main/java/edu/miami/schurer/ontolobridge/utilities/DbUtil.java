package edu.miami.schurer.ontolobridge.utilities;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class DbUtil {

    public static boolean isMySQL(JdbcTemplate tpl) {
        boolean isMySQL = false;

        Connection conn = null;
        try {
            conn = Objects.requireNonNull(tpl.getDataSource()).getConnection();
            String dbName = conn.getMetaData().getDatabaseProductName();
            isMySQL = StringUtils.containsIgnoreCase(dbName, "mysql");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isMySQL;
    }

}
