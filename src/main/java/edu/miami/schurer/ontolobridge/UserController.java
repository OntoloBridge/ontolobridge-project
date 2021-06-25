package edu.miami.schurer.ontolobridge;

import edu.miami.schurer.ontolobridge.Responses.*;
import edu.miami.schurer.ontolobridge.models.Role;
import edu.miami.schurer.ontolobridge.utilities.OntoloException;
import edu.miami.schurer.ontolobridge.utilities.OntoloUserDetailsService;
import edu.miami.schurer.ontolobridge.models.Detail;
import edu.miami.schurer.ontolobridge.models.User;
import edu.miami.schurer.ontolobridge.utilities.UserPrinciple;
import io.sentry.Sentry;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static edu.miami.schurer.ontolobridge.utilities.DbUtil.genRandomString;


@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/user")
public class UserController extends BaseController {


    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;

    @Autowired
    private OntoloUserDetailsService userService;

    @Autowired
    PasswordEncoder encoder;

    @PreAuthorize("permitAll()")
    @RequestMapping(path="/request_reset_password", method= RequestMethod.POST, produces={"application/json"})
    public Object ResetPassword(@ApiParam(value = "User Email") @RequestParam(value="email", defaultValue = "") @NotBlank String email){
        User user = userService.findByUserEmail(email);
        if(user != null) {
            String key = genRandomString(20);
            user.addDetail(new Detail("reset_key", key));
            HashMap<String,Object> stringReplace = new HashMap();
            try{
                email = IOUtils.toString(new ClassPathResource("/emails/passwordReset.email").getInputStream(), "UTF-8");
            }catch(IOException e){
                System.out.println("Email Exception");
                Sentry.capture(e);
            }
            stringReplace.put("user_name",user.getName());
            stringReplace.put("ontEmail",this.appProp.getsupportEmail());
            stringReplace.put("reset_url",this.appProp.getSiteURL()+"/reset_password?token="+key);
            email = notLib.formatMessage(email,stringReplace);
            notLib.InsertNotification(JDBCTemplate, "email", user.getEmail(), email, "Ontolobridge - Password Reset Requests");
            userService.saveUser(user);
        }
        return new OperationResponse("success",true,0);
    }

    @RequestMapping(path="/reset_password", method= RequestMethod.GET, produces={"application/json"})
    @PreAuthorize("permitAll()")
    public OperationResponse resetPassword(HttpServletRequest r,
                                           @ApiParam(value = "token") @RequestParam(value="token", defaultValue = "") @NotBlank String token,
                                           @ApiParam(value = "password") @RequestParam(value="password", defaultValue = "") @NotBlank String password) {
        Long userID;
        try {
           userID = userService.verifyPasswordReset(token);
        }catch (EmptyResultDataAccessException e){
           return new OperationResponse("token not found",false,0);
        }
        if(userID != null){
           User user = userService.findByUserId(userID).get();
           user.setPassword(encoder.encode(password));
           Set<Detail> details = user.getDetails();
           //remove the reset_key to prevent reuse
           details.removeIf(m -> m.getField().equals("reset_key"));
           user.setDetails(details);
           userService.saveUser(user);
        }else{
           return new OperationResponse("incorrect token",false,0);
        }
        return new OperationResponse("success",true,0);
    }

    @RequestMapping(path="/password", method= RequestMethod.POST, produces={"application/json"})
    @PreAuthorize("isAuthenticated() and @OntoloSecurityService.isRegistered(authentication) and @OntoloSecurityService.isNotToken(authentication)")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public OperationResponse updatePassword(HttpServletRequest r,
                                            @ApiParam(value = "User Password") @RequestParam(value="password", defaultValue = "") @NotBlank String password) {
        List<Map<String,Object>> allDetails = auth.GetAllDetails();
        List<String> allowedDetails = new ArrayList<>();
        for(Map<String,Object> m: allDetails){ //create whitelist of possible details that can be stored and saved
            allowedDetails.add(m.get("field").toString());
        }

        User user =  userService.findByUserId(((UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();
        user.setPassword(encoder.encode(password));
        userService.saveUser(user);
        return new OperationResponse("success",true,user.getId());
    }

    @RequestMapping(path="/details", method= RequestMethod.POST, produces={"application/json"})
    @PreAuthorize("isAuthenticated() and @OntoloSecurityService.isRegistered(authentication) and @OntoloSecurityService.isNotToken(authentication)")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object updateDetails(HttpServletRequest r,
                                           @ApiParam(value = "Field being Changed") @RequestParam(value="fields",defaultValue = "")@NotBlank List<String> Fields,
                                           @ApiParam(value = "Data being Updated") @RequestParam(value="data", defaultValue = "") @NotBlank List<String> Data) {
        List<Map<String,Object>> allDetails = auth.GetAllDetails();
        List<String> allowedDetails = new ArrayList<>();
        for(Map<String,Object> m: allDetails){ //create whitelist of possible details that can be stored and saved
            allowedDetails.add(m.get("field").toString());
        }


        User user =  userService.findByUserId(((UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();

        //Set privileged fields

        //set email, which requires verification again
        if(Fields.contains("email")) {
            if(userService.emailExists(Data.get(Fields.indexOf("email")))) {
                return new ResponseEntity<>(formatResultsWithoutCount("Email is already in use!"),
                        HttpStatus.BAD_REQUEST);
            }
            user.setEmail(Data.get(Fields.indexOf("email")));
            String vCode =genRandomString(10);
            HashSet<Detail> details =  new HashSet<>();
            details.add(new Detail("verification",vCode));
            user.setDetails(details);//add an email verification field

            HashMap<String,Object> emailVariables = new HashMap<>();
            emailVariables.put("verification",vCode);
            notLib.InsertEmail(JDBCTemplate,"/email/verificationTemplate.email",Data.get(Fields.indexOf("email")),"Verification Email",emailVariables);

        }
        if(Fields.contains("pass1") && Fields.contains("pass2")) {
            String pass1 = Data.get(Fields.indexOf("pass1"));
            String pass2 = Data.get(Fields.indexOf("pass2"));
            if(!pass1.equals(pass2)){
                return new ResponseEntity<>(formatResultsWithoutCount("Both passwords should be equal"),
                        HttpStatus.BAD_REQUEST);
            }
            user.setPassword(encoder.encode(Data.get(Fields.indexOf("pass1"))));
            String vCode =genRandomString(10);
            HashMap<String,Object> emailVariables = new HashMap<>();
            notLib.InsertEmail(JDBCTemplate,"/email/passwordChange.email",Data.get(Fields.indexOf("email")),"Your Password Has Changed",emailVariables);

        }

        if(Fields.contains("name"))
            user.setName(Data.get(Fields.indexOf("name")));

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
        userService.saveUser(user);
        return new OperationResponse("success",true,user.getId());
    }

    @RequestMapping(path="/details", method= RequestMethod.GET, produces={"application/json"})
    @PreAuthorize("isAuthenticated() and @OntoloSecurityService.isRegistered(authentication) and @OntoloSecurityService.isNotToken(authentication)")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public UserResponse GetDetails(){
        User user =  userService.findByUserId(((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();
        return new UserResponse(user);
    }

    @RequestMapping(path="/roles", method= RequestMethod.GET, produces={"application/json"})
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object roles(){
        List<String> roles = new ArrayList<>();
        User user =  userService.findByUserId(((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();
        for(Role r:user.getRoles()){
            roles.add(r.getName().toString());
        }
        return roles;
    }

    @RequestMapping(path="/requests", method= RequestMethod.GET, produces={"application/json"})
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object GetRequests(){
        Long id =((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return req.TermStatus(id,"user");
    }

    @PreAuthorize("isAuthenticated() and @OntoloSecurityService.isRegistered(authentication) and @OntoloSecurityService.isNotToken(authentication)")
    @RequestMapping(path="/app_token", method= RequestMethod.GET, produces={"application/json"})
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken")})
    public Object GetAppPasswords(){
        Long id =((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return formatResults(userService.getAppPass(id),"passwords");
    }

    @PreAuthorize("isAuthenticated() and @OntoloSecurityService.isRegistered(authentication) and @OntoloSecurityService.isNotToken(authentication)")
    @RequestMapping(path="/app_token", method= RequestMethod.POST, produces={"application/json"})
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object PutAppPasswords(String app, String comment){
        Long id =((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return formatResults(userService.addAppPass(id,app,comment),"password");
    }


    @PreAuthorize("isAuthenticated() and @OntoloSecurityService.isRegistered(authentication) and @OntoloSecurityService.isNotToken(authentication)")
    @RequestMapping(path="/app_token", method= RequestMethod.DELETE, produces={"application/json"})
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object DeleteAppPasswords(Long id){
        Long user_id =((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        userService.deleteAppPass(id,user_id);
    }

    @PreAuthorize("isAuthenticated() and @OntoloSecurityService.isRegistered(authentication) and hasRole(\"ROLE_CURATOR\")")
    @RequestMapping(path="/maintainer_requests", method= RequestMethod.GET, produces={"application/json"})
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object GetMaintainerRequests(){
        Long id =((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        return userService.getMaintainerRequests(id);
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = StatusResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
    }
    )
    @PreAuthorize("isAuthenticated() and @OntoloSecurityService.isRegistered(authentication)")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @RequestMapping(path="/RequestStatus", method= RequestMethod.GET)
    public FullStatusResponse termStatus(@ApiParam(value = "ID of requests",example = "0") @RequestParam(value="requestID") Integer id){
        List<StatusResponse> result = req.TermStatus(new Long(id),"maintainer");
        Long userID =((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if(result.size() == 1) {
            StatusResponse requests = result.get(0);
            if(userService.isOwnerOfRequests(userID,new Long(id)) || userService.isMaintainerOfRequests(userID,new Long(id))){
                return  (FullStatusResponse)requests;
            }
        }
        return null;

    }

    @PreAuthorize("isAuthenticated() and @OntoloSecurityService.isRegistered(authentication)")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @RequestMapping(path="/RequestHistory", method= RequestMethod.GET)
    public List<Map<String,Object>> termHistory(@ApiParam(value = "ID of requests",example = "0") @RequestParam(value="requestID") Integer id){
        List<StatusResponse> result = req.TermStatus(new Long(id),"maintainer");
        Long userID =((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        if(result.size() == 1) {
            StatusResponse requests = result.get(0);
            if(userService.isOwnerOfRequests(userID,new Long(id)) || userService.isMaintainerOfRequests(userID,new Long(id))){
                return  req.TermHistory(requests.request_id);
            }
        }
        return new ArrayList<>();

    }
}
