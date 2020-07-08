package edu.miami.schurer.ontolobridge;

import edu.miami.schurer.ontolobridge.library.RoleRepository;
import edu.miami.schurer.ontolobridge.library.UserRepository;
import edu.miami.schurer.ontolobridge.models.Role;
import edu.miami.schurer.ontolobridge.models.User;
import edu.miami.schurer.ontolobridge.utilities.JwtProvider;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

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
                       @ApiParam(value = "Anonymize Email") @RequestParam(value="anon",defaultValue = "false") boolean anonymize) {


        if(userRepository.existsByEmail(email)) {
            return new ResponseEntity<>(formatResultsWithoutCount("Email is already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(name,
                email, encoder.encode(password));

        userRepository.save(user);

        return formatResultsWithoutCount("User registered successfully!");
    }

}