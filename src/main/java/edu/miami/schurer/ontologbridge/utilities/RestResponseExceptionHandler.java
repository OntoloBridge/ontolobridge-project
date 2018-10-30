package edu.miami.schurer.ontologbridge.utilities;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Throwable.class)
    ResponseEntity<Object> handleControllerException(HttpServletRequest req, Throwable ex) {

        //create a new Json response body
        Map<String,Object> responseBody = new HashMap<>();
        responseBody.put("error",500);
        responseBody.put("message","An Internal Error Has Occured");

        //return a server error
        return new ResponseEntity<Object>(responseBody,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(OntoloException.class)
    ResponseEntity<Object> handleSigCException(HttpServletRequest req, OntoloException ex) {

        //create a new Json response body
        Map<String,Object> responseBody = new HashMap<>();
        responseBody.put("error",ex.getStatusCode());
        responseBody.put("message",ex.getMessage());

        //return a server error
        return new ResponseEntity<Object>(responseBody,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String,String> responseBody = new HashMap<>();
        responseBody.put("path",request.getContextPath());
        responseBody.put("message","The URL you have reached is not in service at this time (404).");
        return new ResponseEntity<Object>(responseBody,HttpStatus.NOT_FOUND);
    }
}
