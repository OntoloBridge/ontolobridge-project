package edu.miami.schurer.ontolobridge;

import edu.miami.schurer.ontolobridge.Responses.JwtResponse;
import edu.miami.schurer.ontolobridge.Responses.OperationResponse;
import edu.miami.schurer.ontolobridge.Responses.UserResponse;
import edu.miami.schurer.ontolobridge.library.NotificationLibrary;
import edu.miami.schurer.ontolobridge.library.RoleRepository;
import edu.miami.schurer.ontolobridge.library.UserRepository;
import edu.miami.schurer.ontolobridge.models.Detail;
import edu.miami.schurer.ontolobridge.models.Role;
import edu.miami.schurer.ontolobridge.models.RoleName;
import edu.miami.schurer.ontolobridge.models.User;
import edu.miami.schurer.ontolobridge.utilities.JwtProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import edu.miami.schurer.ontolobridge.utilities.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.*;

import static edu.miami.schurer.ontolobridge.utilities.DbUtil.genRandomString;

@RestController
public class AuthController extends BaseController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;

    //Redirect the root request to swagger page
    @RequestMapping(path="/register", method= RequestMethod.POST, produces={"application/json"})
    public Object register(@ApiParam(value = "Email for user") @RequestParam(value="email",defaultValue = "")@NotBlank String email,
                           @ApiParam(value = "Email for user") @RequestParam(value="name",defaultValue = "")@NotBlank String name,
                       @ApiParam(value = "User Password") @RequestParam(value="password",defaultValue = "") @NotBlank String password,
                       @ApiParam(value = "Anonymize Email") @RequestParam(value="anon",defaultValue = "false") boolean anonymize) throws OntoloException {


        if(userRepository.existsByEmail(email)) {
            return new ResponseEntity<>(formatResultsWithoutCount("Email is already in use!"),
                    HttpStatus.BAD_REQUEST);
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
        notLib.InsertEmail(JDBCTemplate,"/email/verificationTemplate.email",email,"Verification Email",emailVariables);

        userRepository.save(user);

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
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new OntoloException(" User Role not found."));
        roles.add(userRole);
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
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new OntoloException(" User Role not found."));
        if(!authentication.getAuthorities().contains(userRole))
            throw new OntoloException("Email not verified",HttpStatus.BAD_REQUEST); //code not found throw error
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @RequestMapping(path="/updateUser", method= RequestMethod.POST, produces={"application/json"})
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public OperationResponse updateDetails(@ApiParam(value = "Field being Changed") @RequestParam(value="fields",defaultValue = "")@NotBlank List<String> Fields,
                                              @ApiParam(value = "User Password") @RequestParam(value="data", defaultValue = "") @NotBlank List<String> Data) {

        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //get current user
        ArrayList<Detail> details =new ArrayList<>(user.getDetails()); //get details about the user
        for(int i =0;i<Fields.size();i++) { //loop through updates and update whatever is needed.
            boolean fieldSet = false;
            for (Detail d : details) {
                if(d.getField().equals(Fields.get(i))) {
                    d.setValue(Data.get(i));
                    fieldSet = true; //if we get a hit go to the next
                    break;
                }
            }
            if(!fieldSet)
                details.add(new Detail(Fields.get(i),Data.get(i)));
        }
        return new OperationResponse("success",true,user.getId());
    }

    @RequestMapping(path="/getDetails", method= RequestMethod.POST, produces={"application/json"})
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    public UserResponse UserDetails(){
        User user =  userRepository.findById(((UserPrinciple)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()).get();
        return new UserResponse(user);
    }
}