package edu.miami.schurer.ontolobridge.Responses;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Date;
import java.sql.ResultSet;

@ApiModel(description = "Response returned for all term requests")
public class MaintainersObject {

    private String ontology_name;
    private String ontology_short;
    private String maintainer_name;
    private String contact_location;
    private String contact_method;

    public MaintainersObject(String ontology_name, String ontology_short, String maintainer_name, String contact_location, String contact_method) {
        this.ontology_name = ontology_name;
        this.ontology_short = ontology_short;
        this.maintainer_name = maintainer_name;
        this.contact_location = contact_location;
        this.contact_method = contact_method;
    }

    public MaintainersObject(ResultSet rs) {
        try {
            this.ontology_name = rs.getString(1);
            this.ontology_short = rs.getString(2);
            this.maintainer_name = rs.getString(3);
            this.contact_location = rs.getString(4);
            this.contact_method = rs.getString(5);
        }catch(Exception e){
        }
    }

    public String getOntology_name() {
        return ontology_name;
    }

    public String getOntology_short() {
        return ontology_short;
    }

    public String getMaintainer_name() {
        return maintainer_name;
    }

    public String getContact_location() {
        return contact_location;
    }

    public String getContact_method() {
        return contact_method;
    }
}