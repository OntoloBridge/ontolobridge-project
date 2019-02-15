package edu.miami.schurer.ontolobridge.utilities;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.mail")
@Configuration
public class EmailProperties {

    /**
     * HostName of email service
     */
    private String host = "";



    /**
     * Username of email service
     */
    private String username = "";


    /**
     * Password of email service
     */
    private String password = "";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}