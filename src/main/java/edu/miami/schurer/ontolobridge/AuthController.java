package edu.miami.schurer.ontolobridge;

import edu.miami.schurer.ontolobridge.Responses.JwtResponse;
import edu.miami.schurer.ontolobridge.Responses.OperationResponse;
import edu.miami.schurer.ontolobridge.Responses.UserResponse;
import edu.miami.schurer.ontolobridge.library.UserRepository;
import edu.miami.schurer.ontolobridge.models.Detail;
import edu.miami.schurer.ontolobridge.models.Role;
import edu.miami.schurer.ontolobridge.models.RoleName;
import edu.miami.schurer.ontolobridge.models.User;
import edu.miami.schurer.ontolobridge.utilities.JwtProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import edu.miami.schurer.ontolobridge.utilities.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.*;

import static edu.miami.schurer.ontolobridge.utilities.DbUtil.genRandomString;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OntoloSecurityService securityService;


    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> duplicateEmailException(HttpServletRequest req, ConstraintViolationException e) {
        return RestResponseExceptionHandler.generateResponse(400,"Error Processing Requests:"+e.getMessage(),HttpStatus.UNAUTHORIZED);
    }

    //Redirect the root request to swagger page
    @RequestMapping(path="/register", method= RequestMethod.POST, produces={"application/json"})
    public Object register(@ApiParam(value = "Email for user") @RequestParam(value="email",defaultValue = "")@NotBlank String email,
                           @ApiParam(value = "Email for user") @RequestParam(value="name",defaultValue = "")@NotBlank String name,
                       @ApiParam(value = "User Password") @RequestParam(value="user_password",defaultValue = "") @NotBlank String password,
                       @ApiParam(value = "Anonymize Email") @RequestParam(value="anon",defaultValue = "false") boolean anonymize) throws OntoloException {


        if(userRepository.existsByEmail(email)) {
            StringBuilder output = new StringBuilder();
            output.append("Email already in use\r\n");
            return RestResponseExceptionHandler.generateResponse(400,output.toString(),HttpStatus.UNAUTHORIZED);
        }

        // Creating user's account
        User user = new User(name,
                email, encoder.encode(password)); //encode their password

        String vCode =genRandomString(10);
        HashSet<Detail> details =  new HashSet<>();
        details.add(new Detail("verification",vCode));
        user.setDetails(details);//add an email verification field

        HashMap<String,Object> emailVariables = new HashMap<>();
        emailVariables.put("verification",vCode);
        emailVariables.put("linkVerification",this.frontendURL+"/verify?verify="+vCode);
        emailVariables.put("user_name",name);
        notLib.InsertEmail(JDBCTemplate,"/emails/verificationTemplate.email","Verification Email",email,emailVariables);

        try {
            userRepository.save(user);
        }catch(javax.validation.ConstraintViolationException e){
            StringBuilder output = new StringBuilder();
            output.append("Error Processing Requests:\r\n");
            for(ConstraintViolation c: e.getConstraintViolations()){
                output.append(c.getPropertyPath());
                output.append(c.getMessage());
                output.append("\r\n");
            }
            return RestResponseExceptionHandler.generateResponse(400,output.toString(),HttpStatus.UNAUTHORIZED);
        }catch(javax.validation.UnexpectedTypeException e){
            StringBuilder output = new StringBuilder();
            output.append("Error Processing Requests:\r\n");
            return RestResponseExceptionHandler.generateResponse(400,output.toString(),HttpStatus.UNAUTHORIZED);
        }

        return formatResultsWithoutCount("User registered successfully!");
    }
    @RequestMapping(path="/verify", method= RequestMethod.GET, produces={"application/json"})
    public Object verify(@ApiParam(value = "Value used for Email Verification") @RequestParam(value="verify",defaultValue = "")@NotBlank String verify ) throws OntoloException {
        Integer id = 0;
        try {
            id = auth.VerifyEmail(verify); //verify the verification code
        }catch(EmptyResultDataAccessException e){
            throw new OntoloException("Code not Found",HttpStatus.BAD_REQUEST); //code not found throw error
        }
        Optional<User> u = userRepository.findById(new Long(id));
        if(!u.isPresent()){
            throw new OntoloException("User not found",HttpStatus.BAD_REQUEST); //user not found, throw error. SHOULD NEVER HAPPEN
        }
        User user = u.get();
        Set<Detail> details = user.getDetails();
        details.removeIf(d -> d.getField().equals("verification")); //remove the verification from the user
        user.setDetails(details); //save details

        HashSet<Role> roles = new HashSet<>(); //add user role now that user is verified
        Role verifiedRole = roleRepository.findByName(RoleName.ROLE_VERIFIED)
                .orElseThrow(() -> new OntoloException(" User Role not found."));
        roles.add(verifiedRole);
        user.setRoles(roles); //give the new user the role of user

        userRepository.save(user); //save user
        return formatResultsWithoutCount("Email Verified");
    }

    @RequestMapping(path="/login", method= RequestMethod.POST, produces={"application/json"})
    public ResponseEntity<?> authenticateUser(@ApiParam(value = "Email for user") @RequestParam(value="email",defaultValue = "")@NotBlank String email,
                                              @ApiParam(value = "User Password") @RequestParam(value="password",defaultValue = "") @NotBlank String password)
            throws OntoloException{

        Authentication authentication = authenticationManager.authenticate( //request to test login against login table
                new UsernamePasswordAuthenticationToken(email,
                        password
                )
        );
        if(!hasRole(authentication,RoleName.ROLE_VERIFIED))
            throw new OntoloException("Email not verified",6,HttpStatus.BAD_REQUEST).DoNotLog(); //code not found throw error
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt, authentication.getName()));
    }

    @RequestMapping(path="/checkToken", method= RequestMethod.GET, produces={"application/json"})
    @PreAuthorize("isAuthenticated()")
    public Object checkToken(){
        if(!securityService.isRegistered(SecurityContextHolder.getContext().getAuthentication())){
            Map<String,Object> responseBody = new HashMap<>();
            responseBody.put("error",5);
            responseBody.put("message","Registration not complete");
            responseBody.put("timestamp", Instant.now());
            //return a server error
            return new ResponseEntity<Object>(responseBody,HttpStatus.OK);
        }
        return true;
    }


    @RequestMapping(path="/getAllDetails", method= RequestMethod.GET, produces={"application/json"})
    public Object AllDetails(){
        return formatResults(auth.GetAllDetails());
    }
    @RequestMapping(path="/retrieveMissingDetails", method= RequestMethod.GET, produces={"application/json"})
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public Object CheckUserDetails(){
        User user =  userRepository.findById(((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();
        Set<Detail> details = user.getDetails();
        List<Map<String,Object>> requiredDetails = auth.GetAllDetails();
        List<Map<String,Object>> missingDetails = new ArrayList<>(requiredDetails);
        for(Map<String,Object> m: requiredDetails){
            for(Detail d:details){
                if(m.get("field").equals(d.getField()) && Integer.parseInt(m.get("required").toString()) == 1){
                    missingDetails.remove(m);
                }
            }
            if(Integer.parseInt(m.get("required").toString()) != 1){
                missingDetails.remove(m);
            }
        }
        return formatResults(missingDetails);
    }
}