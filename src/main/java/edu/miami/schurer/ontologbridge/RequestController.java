package edu.miami.schurer.ontologbridge;

import edu.miami.schurer.ontologbridge.Responses.ExceptionResponse;
import edu.miami.schurer.ontologbridge.Responses.OperationResponse;
import edu.miami.schurer.ontologbridge.Responses.RequestResponse;
import edu.miami.schurer.ontologbridge.Responses.StatusResponse;
import io.swagger.annotations.ApiParam;
import edu.miami.schurer.ontologbridge.library.RequestsLibrary;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/REST")
public class RequestController extends BaseController {

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = RequestResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
    }
    )
    @RequestMapping(path="/RequestTerm", method= RequestMethod.POST)
    public Object requestTerm(@ApiParam(value = "Label of suggested term" ,required = true) @RequestParam(value="label") String label,
                              @ApiParam(value = "Description of suggested term",required = true) @RequestParam(value="description") String description,
                              @ApiParam(value = "Superclass of suggested term",required = true) @RequestParam(value="superclasss") String superclass_uri,
                              @ApiParam(value = "Any references for this requests") @RequestParam(value="references",defaultValue = "") String references,
                              @ApiParam(value = "Justification if any for adding this term") @RequestParam(value="justification",defaultValue = "") String justification,
                              @ApiParam(value = "Name of the submitter if provided") @RequestParam(value="submitter",defaultValue = "") String submitter,
                              @ApiParam(value = "Email of the submitter") @RequestParam(value="email",defaultValue = "") String submitter_email,
                              @ApiParam(value = "Should submitter be notified of changes ") @RequestParam(value="notify",defaultValue = "false") boolean notify,
                              @ApiParam(value = "What type of Requests is this") @RequestParam(value="request_type",defaultValue = "term") String type) {

        Integer id = RequestsLibrary.RequestsTerm(JDBCTemplate, label,
                description,
                superclass_uri,
                references,
                justification,
                submitter,
                submitter_email,
                notify,
                type);
        return new RequestResponse(id,
                "http://dev3.ccs.miami.edu:8080/ontolobridge/ONTB_"+String.format("%9d",id).replace(' ','0'),
                "ONTB_"+String.format("%9d",id).replace(' ','0'));
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = StatusResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
    }
    )
    @RequestMapping(path="/TermStatus", method= RequestMethod.GET)
    public Object termStatus(@ApiParam(value = "ID of term") @RequestParam(value="id",defaultValue = "0") Integer id){
        List<StatusResponse> result = RequestsLibrary.TermStatus(JDBCTemplate, id);
        if(result.size() == 1)
            return result.get(0);
        return result;

    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = OperationResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
    }
    )
    @RequestMapping(path="/TermSetStatus", method= RequestMethod.POST)
    public Object termStatus(@ApiParam(value = "ID of term" ,required = true) @RequestParam(value="id") Integer id,
                             @ApiParam(value = "New Status" ,required = true,allowableValues = "submitted,accepted,requires-response,rejected") @RequestParam(value="status")String status,
                             @ApiParam(value = "Message of status" ) @RequestParam(value="message",defaultValue = "")String message){
        return RequestsLibrary.TermStatus(JDBCTemplate, id);

    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = OperationResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
    }
    )
    @RequestMapping(path="/UpdateTerm", method= RequestMethod.POST)
    public Object updateTerm(@ApiParam(value = "ID of term" ,required = true) @RequestParam(value="id") Integer id,
                             @ApiParam(value = "New Status" ,required = true,allowableValues = "submitted,accepted,requires-response,rejected") @RequestParam(value="status")String status,
                             @ApiParam(value = "Message of status" ) @RequestParam(value="message",defaultValue = "")String message){
        return RequestsLibrary.TermStatus(JDBCTemplate, id);

    }
}
