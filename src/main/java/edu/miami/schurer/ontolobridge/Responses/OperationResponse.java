package edu.miami.schurer.ontolobridge.Responses;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Response returned for all operations on a requests")
public class OperationResponse {

    @ApiModelProperty( required = true, example = "success")
    public String status;

    @ApiModelProperty( required = true, example = "true")
    public boolean success;

    @ApiModelProperty( required = true, example = "ONTB_00025")
    public String requestsID;

    public OperationResponse(String status, boolean success, String requestsID) {
        this.status = status;
        this.success = success;
        this.requestsID = requestsID;
    }
}
