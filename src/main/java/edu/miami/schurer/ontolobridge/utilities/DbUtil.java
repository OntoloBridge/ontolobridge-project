package edu.miami.schurer.ontolobridge.utilities;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

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
    public static String genRandomString(int length){
        Random random = new Random();
        String strAllowedCharacters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sbRandomString = new StringBuilder(length);

        for(int i = 0 ; i < length; i++){

            //get random integer between 0 and string length
            int randomInt = random.nextInt(strAllowedCharacters.length());

            //get char from randomInt index from string and append in StringBuilder
            sbRandomString.append( strAllowedCharacters.charAt(randomInt) );
        }
        return sbRandomString.toString();
    }

}
