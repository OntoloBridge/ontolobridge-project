package edu.miami.schurer.ontologbridge.utilities;

public class OntoloException extends Exception {


    private Integer statusCode = 500;
    // Parameterless Constructor
    public OntoloException() {}

    // Constructor that accepts a message
    public OntoloException(String message)
    {
        super(message);
    }
    // Constructor that accepts a message
    public OntoloException(String message, Integer code)
    {
        super(message);
        this.statusCode=code;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
