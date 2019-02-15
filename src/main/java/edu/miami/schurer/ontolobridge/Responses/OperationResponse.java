package edu.miami.schurer.ontolobridge.Responses;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Response returned for all operations on a requests")
public class OperationResponse {

    @ApiModelProperty( required = true, example = "success")
    public String status;

    @ApiModelProperty( required = true, example = "true")
    public boolean success;

    @ApiModelProperty( required = true, example = "25")
    public int requests_id;

    public OperationResponse(String status, boolean success, int requestsID) {
        this.status = status;
        this.success = success;
        this.requests_id = requestsID;
    }
}
