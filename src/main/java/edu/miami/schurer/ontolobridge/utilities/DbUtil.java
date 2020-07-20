package edu.miami.schurer.ontolobridge.utilities;

import ch.qos.logback.core.db.dialect.DBUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;


@ConfigurationProperties(prefix = "spring.datasource")
@Configuration
public class DbUtil {

    private static String DATASOURCE_URL;

    @Value("${spring.datasource.url}")
    public void setSvnUrl(String datasourceUrl) {
        DATASOURCE_URL = datasourceUrl;
    }
    public static boolean isMySQL() {
        return  DbUtil.DATASOURCE_URL.contains("mysql");
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
