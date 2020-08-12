package edu.miami.schurer.ontolobridge;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
@RestController
public class IndexController {

    //Redirect the root request to swagger page
    @RequestMapping("/")
    public void method(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Location", "swagger-ui.html");
        httpServletResponse.setStatus(302);
    }
    @RequestMapping("/csrf")
    public void csrf(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(200);
    }
}