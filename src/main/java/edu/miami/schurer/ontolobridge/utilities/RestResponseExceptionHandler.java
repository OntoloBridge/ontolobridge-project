package edu.miami.schurer.ontolobridge.utilities;

import io.sentry.Sentry;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Throwable.class)
    ResponseEntity<Object> handleControllerException(HttpServletRequest req, Throwable ex) {
        Sentry.capture(ex);
        logger.info("Exception Caught",ex);
        return generateResponse(500,"An Internal Error Has Occurred",HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(OntoloException.class)
    ResponseEntity<Object> handleOntoloException(HttpServletRequest req, OntoloException ex) {
        Sentry.capture(ex);
        logger.info("Exception Caught",ex);
        return generateResponse(ex.getStatusCode().value(),ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<Object> handleAccessException(HttpServletRequest req, AccessDeniedException ex) {
        //return a server error
        return generateResponse(403,ex.getMessage(),HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<Object> handleAccessException(HttpServletRequest req, BadCredentialsException ex) {
        //return a server error
        return generateResponse(403,"Invalid email or password",HttpStatus.UNAUTHORIZED);
    }

    //code to generate common message for all exceptions
    ResponseEntity<Object> generateResponse(int errorCode,String message,HttpStatus statusCode){
        Map<String,Object> responseBody = new HashMap<>();
        responseBody.put("error",errorCode);
        responseBody.put("message",message);
        responseBody.put("timestamp",Instant.now());
        //return a server error
        return new ResponseEntity<>(responseBody,statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String,String> responseBody = new HashMap<>();
        responseBody.put("path",request.getContextPath());
        responseBody.put("message","The URL you have reached is not in service at this time (404).");
        return new ResponseEntity<>(responseBody,HttpStatus.NOT_FOUND);
    }
}
