package edu.miami.schurer.ontolobridge;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {

    //Redirect the root request to swagger page
    @RequestMapping("/register")
    public void method(HttpServletResponse httpServletResponse,
                     @ApiParam(value = "Email for user") @RequestParam(value="email",defaultValue = "")@NotBlank String email,
                       @ApiParam(value = "User Password") @RequestParam(value="password",defaultValue = "") @NotBlank String password,
                       @ApiParam(value = "Anonymize Email") @RequestParam(value="anon",defaultValue = "false") boolean anonymize) {
        httpServletResponse.setHeader("Location", "swagger-ui.html");
        httpServletResponse.setStatus(302);
    }

}