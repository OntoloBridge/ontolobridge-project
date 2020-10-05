package edu.miami.schurer.ontolobridge.utilities;

import org.springframework.http.HttpStatus;

public class OntoloException extends Exception {


    private HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
    private Integer errorCode = 500;
    private boolean logError = true;
    // Parameterless Constructor
    public OntoloException() {}

    // Constructor that accepts a message
    public OntoloException(String message)
    {
        super(message);
    }
    // Constructor that accepts a message
    public OntoloException(String message, HttpStatus code)
    {
        super(message);
        this.statusCode=code;
    }
    public OntoloException(String message, Integer code)
    {
        super(message);
        this.errorCode=code;
    }
    public OntoloException(String message,Integer ecode, HttpStatus code)
    {
        super(message);
        this.statusCode=code;
        this.errorCode=ecode;
    }
    public OntoloException DoNotLog(){
        logError = false;
        return this;
    }
    public Boolean getShouldLog() {
        return logError;
    }
    public Integer getErrorCode() {
        return errorCode;
    }
    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
