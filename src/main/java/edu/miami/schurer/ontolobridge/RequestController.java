package edu.miami.schurer.ontolobridge;

import edu.miami.schurer.ontolobridge.Responses.*;
import edu.miami.schurer.ontolobridge.library.NotificationLibrary;
import edu.miami.schurer.ontolobridge.utilities.AppProperties;
import io.swagger.annotations.ApiParam;
import edu.miami.schurer.ontolobridge.library.RequestsLibrary;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;
import java.awt.image.ImagingOpException;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/requests")
public class RequestController extends BaseController {

    @Autowired
    public NotifierService notifier;

    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;

    @Autowired
    public OntologyManagerService Manager;

    @Autowired
    private AppProperties appProp;

    public NotificationLibrary notLib ;

    @PostConstruct
    void Init(){
        notLib = new NotificationLibrary(appProp);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = RequestResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
        }
    )
    @RequestMapping(path="/RequestTerm", method= RequestMethod.POST)
    public Object requestTerm(@ApiParam(value = "Label of suggested term" ,required = true) @RequestParam(value="label") @NotBlank String label,
                              @ApiParam(value = "Description of suggested term",required = true) @RequestParam(value="description") @NotBlank String description,
                              @ApiParam(value = "Superclass of suggested term",required = true) @RequestParam(value="superclass") @NotBlank String uri_superclass,
                              @ApiParam(value = "Superclass ontology of suggested term") @RequestParam(value="superclass_ontology", defaultValue = "") String superclass_ontology,
                              @ApiParam(value = "Any references for this requests") @RequestParam(value="reference",defaultValue = "") @NotBlank String reference,
                              @ApiParam(value = "Justification if any for adding this term") @RequestParam(value="justification",defaultValue = "") String justification,
                              @ApiParam(value = "Name of the submitter if provided") @RequestParam(value="submitter",defaultValue = "") String submitter,
                              @ApiParam(value = "Email of the submitter") @RequestParam(value="email",defaultValue = "") String submitter_email,
                              @ApiParam(value = "Ontology Request ") @RequestParam(value="ontology",defaultValue = "") String ontology,
                              @ApiParam(value = "Should submitter be notified of changes ") @RequestParam(value="notify",defaultValue = "false") boolean notify) {

        Integer id = RequestsLibrary.RequestsTerm(JDBCTemplate, label,
                description,
                uri_superclass,
                superclass_ontology,
                reference,
                justification,
                submitter,
                submitter_email,
                notify,
                ontology,
                "term");
        if(ontology != null && !ontology.isEmpty()){
            List<MaintainersObject> maintainers = Manager.GetMaintainers(ontology);
            //queue notifications
            for (MaintainersObject m : maintainers) {
                NotificationLibrary.InsertNotification(JDBCTemplate, m.getContact_method(), m.getContact_location(), "A new term has been submitted", "New term Forms");
            }
        }


        if(submitter_email != null && notify){
            notLib.InsertEmail(JDBCTemplate,
                    "/emails/termSubmission.email",
                    label,
                    description,
                    uri_superclass,
                    reference,
                    justification,
                    submitter,
                    submitter_email,
                    "term",
                    id.toString());
            notifier.sendEmailNotification(submitter_email,"Received Term Request","Hello "+submitter+"\n\n We have received your request for "+label+" and appropriate maintainers notified");
        }


        return new RequestResponse(id,
                "http://dev3.ccs.miami.edu:8080/ontolobridge/ONTB_"+String.format("%9d",id).replace(' ','0'),
                "ONTB_"+String.format("%9d",id).replace(' ','0'));
    }

    @RequestMapping(path="/RequestDataProperty", method= RequestMethod.POST)
    public Object requestDataProperty(@ApiParam(value = "Label of suggested term" ,required = true) @RequestParam(value="label") String label,
                              @ApiParam(value = "Description of suggested term",required = true) @RequestParam(value="description") String description,
                              @ApiParam(value = "Superclass of suggested term",required = true) @RequestParam(value="superclass") String uri_superclass,
                              @ApiParam(value = "Superclass ontology of suggested term") @RequestParam(value="superclass_ontology", defaultValue = "") String superclass_ontology,
                              @ApiParam(value = "Any references for this requests") @RequestParam(value="reference",defaultValue = "") String reference,
                              @ApiParam(value = "Justification if any for adding this term") @RequestParam(value="justification",defaultValue = "") String justification,
                              @ApiParam(value = "Name of the submitter if provided") @RequestParam(value="submitter",defaultValue = "") String submitter,
                              @ApiParam(value = "Email of the submitter") @RequestParam(value="email",defaultValue = "") String submitter_email,
                              @ApiParam(value = "Ontology Request ") @RequestParam(value="ontology",defaultValue = "") String ontology,
                              @ApiParam(value = "Should submitter be notified of changes ") @RequestParam(value="notify",defaultValue = "false") boolean notify) {

        if(label.length() == 0)
           return new ExceptionResponse("Label is required");
        if(description.length() == 0)
            return new ExceptionResponse("Description is required");
        Integer id = RequestsLibrary.RequestsTerm(JDBCTemplate, label,
                description,
                uri_superclass,
                superclass_ontology,
                reference,
                justification,
                submitter,
                submitter_email,
                notify,
                ontology,
                "Data");

        if(ontology != null && !ontology.isEmpty()){
            List<MaintainersObject> maintainers = Manager.GetMaintainers(ontology);
            //queue notifications
            for (MaintainersObject m : maintainers) {
                NotificationLibrary.InsertNotification(JDBCTemplate, m.getContact_method(), m.getContact_location(), "A new data property has been submitted", "New data property Forms");
            }
        }
        return new RequestResponse(id,
                "http://dev3.ccs.miami.edu:8080/ontolobridge/ONTB_"+String.format("%9d",id).replace(' ','0'),
                "ONTB_"+String.format("%9d",id).replace(' ','0'));
    }

    @RequestMapping(path="/RequestObjectProperty", method= RequestMethod.POST)
    public Object requestObjectProperty(@ApiParam(value = "Label of suggested term" ,required = true) @RequestParam(value="label") String label,
                                      @ApiParam(value = "Description of suggested term",required = true) @RequestParam(value="description") String description,
                                      @ApiParam(value = "Superclass of suggested term",required = true) @RequestParam(value="superclass") String uri_superclass,
                                      @ApiParam(value = "Superclass ontology of suggested term") @RequestParam(value="superclass_ontology", defaultValue = "") String superclass_ontology,
                                      @ApiParam(value = "Any references for this requests") @RequestParam(value="reference",defaultValue = "") String reference,
                                      @ApiParam(value = "Justification if any for adding this term") @RequestParam(value="justification",defaultValue = "") String justification,
                                      @ApiParam(value = "Name of the submitter if provided") @RequestParam(value="submitter",defaultValue = "") String submitter,
                                      @ApiParam(value = "Email of the submitter") @RequestParam(value="email",defaultValue = "") String submitter_email,
                                      @ApiParam(value = "Ontology Request ") @RequestParam(value="ontology",defaultValue = "") String ontology,
                                      @ApiParam(value = "Should submitter be notified of changes ") @RequestParam(value="notify",defaultValue = "false") boolean notify) {

        Integer id = RequestsLibrary.RequestsTerm(JDBCTemplate, label,
                description,
                uri_superclass,
                superclass_ontology,
                reference,
                justification,
                submitter,
                submitter_email,
                notify,
                ontology,
                "Object");

        if(ontology != null && !ontology.isEmpty()){
            List<MaintainersObject> maintainers = Manager.GetMaintainers(ontology);
            //queue notifications
            for (MaintainersObject m : maintainers) {
                NotificationLibrary.InsertNotification(JDBCTemplate, m.getContact_method(), m.getContact_location(), "A new object property has been submitted", "New object property Forms");
            }
        }

        return new RequestResponse(id,
                "http://dev3.ccs.miami.edu:8080/ontolobridge/ONTB_"+String.format("%9d",id).replace(' ','0'),
                "ONTB_"+String.format("%9d",id).replace(' ','0'));
    }

    @RequestMapping(path="/RequestAnnotationProperty", method= RequestMethod.POST)
    public Object requestAnnotationProperty(@ApiParam(value = "Label of suggested term" ,required = true) @RequestParam(value="label") String label,
                                        @ApiParam(value = "Description of suggested term",required = true) @RequestParam(value="description") String description,
                                        @ApiParam(value = "Superclass of suggested term",required = true) @RequestParam(value="superclass") String uri_superclass,
                                        @ApiParam(value = "Superclass ontology of suggested term") @RequestParam(value="superclass_ontology", defaultValue = "") String superclass_ontology,
                                        @ApiParam(value = "Any references for this requests") @RequestParam(value="reference",defaultValue = "") String reference,
                                        @ApiParam(value = "Justification if any for adding this term") @RequestParam(value="justification",defaultValue = "") String justification,
                                        @ApiParam(value = "Name of the submitter if provided") @RequestParam(value="submitter",defaultValue = "") String submitter,
                                        @ApiParam(value = "Email of the submitter") @RequestParam(value="email",defaultValue = "") String submitter_email,
                                        @ApiParam(value = "Ontology Request ") @RequestParam(value="ontology",defaultValue = "") String ontology,
                                        @ApiParam(value = "Should submitter be notified of changes ") @RequestParam(value="notify",defaultValue = "false") boolean notify) {

        Integer id = RequestsLibrary.RequestsTerm(JDBCTemplate, label,
                description,
                uri_superclass,
                superclass_ontology,
                reference,
                justification,
                submitter,
                submitter_email,
                notify,
                ontology,
                "Annotation");

        if(ontology != null && !ontology.isEmpty()){
            List<MaintainersObject> maintainers = Manager.GetMaintainers(ontology);
            //queue notifications
            for (MaintainersObject m : maintainers) {
                NotificationLibrary.InsertNotification(JDBCTemplate, m.getContact_method(), m.getContact_location(), "A new annotation property has been submitted", "New annotation property Forms");
            }
        }
        return new RequestResponse(id,
                "http://dev3.ccs.miami.edu:8080/ontolobridge/ONTB_"+String.format("%9d",id).replace(' ','0'),
                "ONTB_"+String.format("%9d",id).replace(' ','0'));
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = StatusResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
    }
    )
    @RequestMapping(path="/RequestStatus", method= RequestMethod.GET)
    public Object termStatus(@ApiParam(value = "ID of requests") @RequestParam(value="requestID",defaultValue = "0") Integer id,
                             @ApiParam(hidden = true) @RequestParam(value="include",defaultValue = "0") String include){
        if(activeProfile.equals("prod")){
            include="";
        }
        List<StatusResponse> result = RequestsLibrary.TermStatus(JDBCTemplate, id,include);
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
    public Object termStatus(@ApiParam(value = "ID of Forms" ,required = true) @RequestParam(value="requestID") Integer id,
                             @ApiParam(value = "New Status" ,required = true,allowableValues = "submitted,accepted,requires-response,rejected") @RequestParam(value="status")String status,
                             @ApiParam(value = "Message of status" ) @RequestParam(value="message",defaultValue = "")String message){
        return RequestsLibrary.TermUpdateStatus(JDBCTemplate, id,status,message);

    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = OperationResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
    }
    )
    @RequestMapping(path="/UpdateRequests", method= RequestMethod.POST)
    public Object updateTerm(@ApiParam(value = "ID of requests" ,required = true) @RequestParam(value="requestID") Integer id,
                             @ApiParam(value = "New Status" ,required = true,allowableValues = "submitted,accepted,requires-response,rejected") @RequestParam(value="status")String status,
                             @ApiParam(value = "Message of status" ) @RequestParam(value="message",defaultValue = "")String message){
        return RequestsLibrary.TermUpdateStatus(JDBCTemplate, id,status,message);

    }
}
