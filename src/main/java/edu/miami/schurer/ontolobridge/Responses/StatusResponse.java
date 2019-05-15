package edu.miami.schurer.ontolobridge.Responses;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Response returned for all term status requests")
public class StatusResponse {

    @ApiModelProperty( required = true, example = "submitted")
    public String status;

    @ApiModelProperty( required = true, example = "submitted")
    public int request_id;

    @ApiModelProperty( required = true, example = "http://dev3.ccs.miami.edu:8080/ontolobridge/ONTB_25")
    public String provisional_uri;

    @ApiModelProperty( required = true, example = "ONTB_25")
    public String provisional_curie;

    @ApiModelProperty( required = true, example = "Accepted without changes")
    public String message;

    @ApiModelProperty( required = false, example = "BAO_25")
    public String uri;

    @ApiModelProperty( required = false, example = "BAO_25")
    public String curie;

    @ApiModelProperty( required = false, example = "2018-11-05 16:32:13.442207")
    public long timestamp;

    @ApiModelProperty( required = false, example = "16748786")
    public String datetime;

    @ApiModelProperty( required = false, example = "Term")
    public String type;

    public StatusResponse(){

    }

    public StatusResponse(String status,int request_id, String provisional_uri, String provisional_curie, String message, String uri, String curie,String type, long timestamp,String datetime) {
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
    }
}
