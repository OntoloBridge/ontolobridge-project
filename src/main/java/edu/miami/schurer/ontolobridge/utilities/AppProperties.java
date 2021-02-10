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

    public void setSiteURL(String siteURL) {
        this.siteURL = siteURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    /**
     * apikey of api services for website service
     */
    private String apiURL = "";

    /**
     * the support email address people can contact
     */
    private String supportEmail = "";

    public String getSiteURL() {
        return siteURL;
    }


    public String getApiURL() {
        return apiURL;
    }


    public String getsupportEmail() {
        return supportEmail;
    }

}