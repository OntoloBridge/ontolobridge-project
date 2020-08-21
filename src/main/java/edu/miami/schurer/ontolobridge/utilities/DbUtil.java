package edu.miami.schurer.ontolobridge.utilities;

import ch.qos.logback.core.db.dialect.DBUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

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

}
