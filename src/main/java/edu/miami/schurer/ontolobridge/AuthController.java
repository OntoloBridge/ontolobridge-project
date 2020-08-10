package edu.miami.schurer.ontolobridge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@RestController
//@RequestMapping("/auth")
public class AuthController extends BaseController {

    //Redirect the root request to swagger page
   // @RequestMapping("/register")
    public void method(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Location", "swagger-ui.html");
        httpServletResponse.setStatus(302);
    }

}