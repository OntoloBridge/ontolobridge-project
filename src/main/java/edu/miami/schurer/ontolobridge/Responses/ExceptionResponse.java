package edu.miami.schurer.ontolobridge.Responses;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Response returned for all errors")
public class ExceptionResponse {


    public int error;


    @ApiModelProperty( required = true, example = "An Internal Error Has Occured")
    public String message;
}
