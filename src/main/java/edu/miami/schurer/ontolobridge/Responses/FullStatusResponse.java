package edu.miami.schurer.ontolobridge.Responses;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

@ApiModel(description = "Response returned for all term status requests")
public class FullStatusResponse extends StatusResponse{
    @ApiModelProperty( required = true, example = "submitted")
    public String label;

    @ApiModelProperty( required = true, example = "submitted")
    public String description;

    @ApiModelProperty( required = true, example = "submitted")
    public String superclass_ontology;

    @ApiModelProperty( required = true, example = "submitted")
    public String superclass_id;

    @ApiModelProperty( required = true, example = "submitted")
    public String reference;

    @ApiModelProperty( required = true, example = "submitted")
    public String justification;

    @ApiModelProperty( required = true, example = "submitted")
    public String submitter_email;

    @ApiModelProperty( required = true, example = "submitted")
    public String submitter;

    @ApiModelProperty( required = true, example = "submitted")
    int notify;

    @ApiModelProperty( required = true, example = "submitted")
    List<Map<String,Object>> history;

    @ApiModelProperty( required = true, example = "submitted")
    int user_id;

    public void setHistory(List<Map<String, Object>> history) {
        this.history = history;
    }

    public FullStatusResponse(String status,
                              Long request_id,
                              String provisional_uri,
                              String provisional_curie,
                              String message,
                              String uri,
                              String curie,
                              String type,
                              long timestamp,
                              String datetime,
                              String label,
                              String description,
                              String superclass_ontology,
                              String superclass_id,
                              String reference,
                              String justification,
                              String submitter,
                              String submitter_email,
                              int notify,
                              String ontology,
                              long user_id) {
        this.status = status;
        this.request_id = request_id;
        this.provisional_uri = provisional_uri;
        this.provisional_curie = provisional_curie;
        this.message = message;
        this.uri = uri;
        this.curie = curie;
        this.datetime = datetime;
        this.timestamp = timestamp;
        this.type = type;
        this.notify = notify;
        this.submitter = submitter;
        this.submitter_email = submitter_email;
        this.justification = justification;
        this.reference = reference;
        this.superclass_id = superclass_id;
        this.superclass_ontology = superclass_ontology;
        this.label = label;
        this.description = description;
        this.ontology = ontology;
        this.user_id = (int)user_id;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getSuperclass_ontology() {
        return superclass_ontology;
    }

    public String getSuperclass_id() {
        return superclass_id;
    }

    public String getReference() {
        return reference;
    }

    public String getJustification() {
        return justification;
    }

    public String getSubmitter_email() {
        return submitter_email;
    }

    public String getSubmitter() {
        return submitter;
    }

    public int getNotify() {
        return notify;
    }

    public List<Map<String, Object>> getHistory() {
        return history;
    }

    public int getUser_id() {
        return user_id;
    }
}
