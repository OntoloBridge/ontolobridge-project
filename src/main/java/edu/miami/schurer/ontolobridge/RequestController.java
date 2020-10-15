package edu.miami.schurer.ontolobridge;

import edu.miami.schurer.ontolobridge.Responses.*;
import edu.miami.schurer.ontolobridge.library.NotificationLibrary;
import edu.miami.schurer.ontolobridge.utilities.OntoloException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;


@RestController
@PreAuthorize("isAuthenticated() and @OntoloSecurityService.isRegistered(authentication)")
@RequestMapping("/requests")
public class RequestController extends BaseController {

    @Autowired
    public NotifierService notifier;

    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = RequestResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
        }
    )
    @RequestMapping(path="/RequestTerm", method= RequestMethod.POST)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object requestTerm(@ApiParam(value = "Label of suggested term" ,required = true) @RequestParam(value="label") @NotBlank String label,
                              @ApiParam(value = "Description of suggested term",required = true) @RequestParam(value="description") @NotBlank String description,
                              @ApiParam(value = "Parent URI of suggested term",required = true) @RequestParam(value="superclass") @NotBlank String uri_superclass,
                              @ApiParam(value = "Parent URI ontology of suggested term") @RequestParam(value="superclass_ontology", defaultValue = "") String superclass_ontology,
                              @ApiParam(value = "Any references for this requests") @RequestParam(value="reference",defaultValue = "") @NotBlank String reference,
                              @ApiParam(value = "Justification if any for adding this term") @RequestParam(value="justification",defaultValue = "") String justification,
                              @ApiParam(value = "Name of the submitter if provided") @RequestParam(value="submitter",defaultValue = "") String submitter,
                              @ApiParam(value = "Email of the submitter") @RequestParam(value="email",defaultValue = "") String submitter_email,
                              @ApiParam(value = "Anonymize Email") @RequestParam(value="anon",defaultValue = "false") boolean anonymize,
                              @ApiParam(value = "Ontology Request ") @RequestParam(value="ontology",defaultValue = "") String ontology,
                              @ApiParam(value = "Should submitter be notified of changes ") @RequestParam(value="notify",defaultValue = "false") boolean notify) throws OntoloException {

        Integer id =req.RequestsTerm(label,
                description,
                uri_superclass,
                superclass_ontology,
                reference,
                justification,
                submitter,
                submitter_email,
                anonymize,
                notify,
                ontology,
                "term");
        if(id < 0)
            throw new OntoloException("Error Making Requests");

        return new RequestResponse(id,
                "http://ontolobridge.ccs.miami.edu/ONTB_"+String.format("%9d",id).replace(' ','0'),
                "ONTB_"+String.format("%9d",id).replace(' ','0'));
    }

    @RequestMapping(path="/RequestDataProperty", method= RequestMethod.POST)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object requestDataProperty(@ApiParam(value = "Label of suggested term" ,required = true) @RequestParam(value="label") String label,
                              @ApiParam(value = "Description of suggested term",required = true) @RequestParam(value="description") String description,
                              @ApiParam(value = "Superclass of suggested term",required = true) @RequestParam(value="parent_uri") String uri_superclass,
                              @ApiParam(value = "Superclass ontology of suggested term") @RequestParam(value="superclass_ontology", defaultValue = "") String superclass_ontology,
                              @ApiParam(value = "Any references for this requests") @RequestParam(value="reference",defaultValue = "") String reference,
                              @ApiParam(value = "Justification if any for adding this term") @RequestParam(value="justification",defaultValue = "") String justification,
                              @ApiParam(value = "Name of the submitter if provided") @RequestParam(value="submitter",defaultValue = "") String submitter,
                              @ApiParam(value = "Email of the submitter") @RequestParam(value="email",defaultValue = "") String submitter_email,
                              @ApiParam(value = "Anonymize Email") @RequestParam(value="anon",defaultValue = "false") boolean anonymize,
                              @ApiParam(value = "Ontology Request ") @RequestParam(value="ontology",defaultValue = "") String ontology,
                              @ApiParam(value = "Should submitter be notified of changes ") @RequestParam(value="notify",defaultValue = "false") boolean notify) {

        if(label.length() == 0)
           return new ExceptionResponse("Label is required");
        if(description.length() == 0)
            return new ExceptionResponse("Description is required");
        Integer id = req.RequestsTerm( label,
                description,
                uri_superclass,
                superclass_ontology,
                reference,
                justification,
                submitter,
                submitter_email,
                anonymize,
                notify,
                ontology,
                "Data");
        return new RequestResponse(id,
                "http://ontolobridge.ccs.miami.edu/ONTB_"+String.format("%9d",id).replace(' ','0'),
                "ONTB_"+String.format("%9d",id).replace(' ','0'));
    }

    @RequestMapping(path="/RequestObjectProperty", method= RequestMethod.POST)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object requestObjectProperty(@ApiParam(value = "Label of suggested term" ,required = true) @RequestParam(value="label") String label,
                                      @ApiParam(value = "Description of suggested term",required = true) @RequestParam(value="description") String description,
                                      @ApiParam(value = "Superclass of suggested term",required = true) @RequestParam(value="parent_uri") String uri_superclass,
                                      @ApiParam(value = "Superclass ontology of suggested term") @RequestParam(value="superclass_ontology", defaultValue = "") String superclass_ontology,
                                      @ApiParam(value = "Any references for this requests") @RequestParam(value="reference",defaultValue = "") String reference,
                                      @ApiParam(value = "Justification if any for adding this term") @RequestParam(value="justification",defaultValue = "") String justification,
                                      @ApiParam(value = "Name of the submitter if provided") @RequestParam(value="submitter",defaultValue = "") String submitter,
                                      @ApiParam(value = "Email of the submitter") @RequestParam(value="email",defaultValue = "") String submitter_email,
                                      @ApiParam(value = "Anonymize Email") @RequestParam(value="anon",defaultValue = "false") boolean anonymize,
                                      @ApiParam(value = "Ontology Request ") @RequestParam(value="ontology",defaultValue = "") String ontology,
                                      @ApiParam(value = "Should submitter be notified of changes ") @RequestParam(value="notify",defaultValue = "false") boolean notify) {

        Integer id = req.RequestsTerm(label,
                description,
                uri_superclass,
                superclass_ontology,
                reference,
                justification,
                submitter,
                submitter_email,
                anonymize,
                notify,
                ontology,
                "Object");


        return new RequestResponse(id,
                "http://ontolobridge.ccs.miami.edu/ONTB_"+String.format("%9d",id).replace(' ','0'),
                "ONTB_"+String.format("%9d",id).replace(' ','0'));
    }

    @RequestMapping(path="/RequestAnnotationProperty", method= RequestMethod.POST)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object requestAnnotationProperty(@ApiParam(value = "Label of suggested term" ,required = true) @RequestParam(value="label") String label,
                                        @ApiParam(value = "Description of suggested term",required = true) @RequestParam(value="description") String description,
                                        @ApiParam(value = "Superclass of suggested term",required = true) @RequestParam(value="parent_uri") String uri_superclass,
                                        @ApiParam(value = "Superclass ontology of suggested term") @RequestParam(value="superclass_ontology", defaultValue = "") String superclass_ontology,
                                        @ApiParam(value = "Any references for this requests") @RequestParam(value="reference",defaultValue = "") String reference,
                                        @ApiParam(value = "Justification if any for adding this term") @RequestParam(value="justification",defaultValue = "") String justification,
                                        @ApiParam(value = "Name of the submitter if provided") @RequestParam(value="submitter",defaultValue = "") String submitter,
                                        @ApiParam(value = "Email of the submitter") @RequestParam(value="email",defaultValue = "") String submitter_email,
                                        @ApiParam(value = "Anonymize Email") @RequestParam(value="anon",defaultValue = "false") boolean anonymize,
                                        @ApiParam(value = "Ontology Request ") @RequestParam(value="ontology",defaultValue = "") String ontology,
                                        @ApiParam(value = "Should submitter be notified of changes ") @RequestParam(value="notify",defaultValue = "false") boolean notify) {

        Integer id = req.RequestsTerm(label,
                description,
                uri_superclass,
                superclass_ontology,
                reference,
                justification,
                submitter,
                submitter_email,
                anonymize,
                notify,
                ontology,
                "Annotation");

        return new RequestResponse(id,
                "http://ontolobridge.ccs.miami.edu/ONTB_"+String.format("%9d",id).replace(' ','0'),
                "ONTB_"+String.format("%9d",id).replace(' ','0'));
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = StatusResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
    }
    )
    @PreAuthorize("permitAll()")
    //@ApiOperation(value = "", authorizations = { })
    @RequestMapping(path="/RequestStatus", method= RequestMethod.GET)
    public Object termStatus(@ApiParam(value = "ID of requests",example = "0") @RequestParam(value="requestID",defaultValue = "0") Integer id,
                             @ApiParam(hidden = true) @RequestParam(value="include",defaultValue = "0") String include){
        if(activeProfile.equals("prod")){
            include="";
        }
        List<StatusResponse> result = req.TermStatus(new Long(id),include);
        if(result.size() == 1)
            return result.get(0);
        return result;

    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = OperationResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
    }
    )
    @RequestMapping(path="/RequestsSetStatus", method= RequestMethod.POST)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object termStatus(@ApiParam(value = "ID of Forms" ,required = true,example = "0") @RequestParam(value="requestID") Integer id,
                             @ApiParam(value = "New Status" ,required = true,allowableValues = "submitted,accepted,requires-response,rejected") @RequestParam(value="status")String status,
                             @ApiParam(value = "Message of status" ) @RequestParam(value="message",defaultValue = "")String message){
        return req.TermUpdateStatus(id,status,message);

    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = OperationResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
    }
    )
    @RequestMapping(path="/UpdateRequests", method= RequestMethod.POST)
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object updateTerm(@ApiParam(value = "ID of requests" ,required = true,example = "0") @RequestParam(value="requestID") Integer id,
                             @ApiParam(value = "New Status" ,required = true,allowableValues = "submitted,accepted,requires-response,rejected") @RequestParam(value="status")String status,
                             @ApiParam(value = "Message of status" ) @RequestParam(value="message",defaultValue = "")String message){
        return req.TermUpdateStatus(id,status,message);

    }
}
