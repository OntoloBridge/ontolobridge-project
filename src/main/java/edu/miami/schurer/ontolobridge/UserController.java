package edu.miami.schurer.ontolobridge;

import edu.miami.schurer.ontolobridge.Responses.*;
import edu.miami.schurer.ontolobridge.library.UserRepository;
import edu.miami.schurer.ontolobridge.models.Detail;
import edu.miami.schurer.ontolobridge.models.User;
import edu.miami.schurer.ontolobridge.utilities.OntoloException;
import edu.miami.schurer.ontolobridge.utilities.UserPrinciple;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/user")
public class UserController extends BaseController {


    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;


    //TODO: allow password reset
    @RequestMapping(path="/resetPassword", method= RequestMethod.GET, produces={"application/json"})
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PreAuthorize("permitAll()")
    public OperationResponse resetPassword(HttpServletRequest r,
                                           @ApiParam(value = "User Email") @RequestParam(value="email", defaultValue = "") @NotBlank String email) {
        return new OperationResponse("success",true,0);
    }

    @RequestMapping(path="/password", method= RequestMethod.POST, produces={"application/json"})
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public OperationResponse updatePassword(HttpServletRequest r,
                                            @ApiParam(value = "User Password") @RequestParam(value="password", defaultValue = "") @NotBlank String password) {
        List<Map<String,Object>> allDetails = auth.GetAllDetails();
        List<String> allowedDetails = new ArrayList<>();
        for(Map<String,Object> m: allDetails){ //create whitelist of possible details that can be stored and saved
            allowedDetails.add(m.get("field").toString());
        }

        User user =  userRepository.findById(((UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        return new OperationResponse("success",true,user.getId());
    }

    @RequestMapping(path="/details", method= RequestMethod.POST, produces={"application/json"})
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public OperationResponse updateDetails(HttpServletRequest r,
                                           @ApiParam(value = "Field being Changed") @RequestParam(value="fields",defaultValue = "")@NotBlank List<String> Fields,
                                           @ApiParam(value = "Data being Updated") @RequestParam(value="data", defaultValue = "") @NotBlank List<String> Data) {
        List<Map<String,Object>> allDetails = auth.GetAllDetails();
        List<String> allowedDetails = new ArrayList<>();
        for(Map<String,Object> m: allDetails){ //create whitelist of possible details that can be stored and saved
            allowedDetails.add(m.get("field").toString());
        }

        User user =  userRepository.findById(((UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();
        ArrayList<Detail> details =new ArrayList<>(user.getDetails()); //get details about the user
        for(int i =0;i<Fields.size();i++) { //loop through updates and update whatever is needed.
            boolean fieldSet = false;
            if(!allowedDetails.contains(Fields.get(i))) //if detail not in whitelist, discard
                continue;
            for (Detail d : details) {
                if(d.getField().equals(Fields.get(i))) {
                    if(Data.get(i).isEmpty()) //if empty remove the data from the table
                        details.remove(d);
                    d.setValue(Data.get(i));
                    fieldSet = true; //if we get a hit go to the next
                    break;
                }
            }
            if(!fieldSet && !Data.get(i).isEmpty()) //if field has not been set that means its a new detail, add detail
                details.add(new Detail(Fields.get(i),Data.get(i)));
        }
        user.setDetails(new HashSet<>(details));
        userRepository.save(user);
        return new OperationResponse("success",true,user.getId());
    }

    @RequestMapping(path="/details", method= RequestMethod.GET, produces={"application/json"})
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public UserResponse GetDetails(){
        User user =  userRepository.findById(((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();
        return new UserResponse(user);
    }

    @PreAuthorize("isAuthenticated() and @OntoloSecurityService.isRegistered(authentication)")
    @RequestMapping(path="/requests", method= RequestMethod.GET, produces={"application/json"})
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object GetRequests(){
        Long id =((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return req.TermStatus(id,"user");
    }
}
