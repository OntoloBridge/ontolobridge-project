package edu.miami.schurer.ontologbridge.Responses;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Response returned for all term status requests")
public class StatusResponse {

    @ApiModelProperty( required = true, example = "submitted")
    public String status;

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

    public StatusResponse(String status, String provisional_uri, String provisional_curie, String message, String uri, String curie) {
        this.status = status;
        this.provisional_uri = provisional_uri;
        this.provisional_curie = provisional_curie;
        this.message = message;
        this.uri = uri;
        this.curie = curie;
    }
}
