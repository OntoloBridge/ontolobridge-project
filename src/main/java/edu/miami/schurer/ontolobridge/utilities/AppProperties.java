package edu.miami.schurer.ontolobridge.utilities;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.general")
@Configuration
public class AppProperties {

    /**
     * HostName of email service
     */
    private String siteURL = "";



    /**
     * apikey of github service
     */
    private String apiURL = "";

    public String getSiteURL() {
        return siteURL;
    }

    public void setSiteURL(String siteURL) {
        this.siteURL = siteURL;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }
}