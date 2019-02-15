package edu.miami.schurer.ontolobridge.utilities;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.github")
@Configuration
public class GithubProperties {

    /**
     * HostName of email service
     */
    private String username = "";



    /**
     * apikey of github service
     */
    private String apikey = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }
}