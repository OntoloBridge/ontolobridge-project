package edu.miami.schurer.ontolobridge.utilities;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Component
public class JWTRefreshInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtProvider tokenProvider;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpiration}")
    private int jwtExpiration;

    //cannot modify in post handle so do it here.
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String jwt = getJwt(request);
        if (jwt!=null && tokenProvider.validateJwtToken(jwt)) {

            //TODO: Add in code to save to database to allow revokation
            Date Expiration = tokenProvider.getExpirationFromJWTToken(jwt);
            if((Expiration.getTime() - new Date().getTime()) < 87000){
                String token =  Jwts.builder()
                        .setSubject((tokenProvider.getUserNameFromJwtToken(jwt)))
                        .setIssuedAt(new Date())
                        .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                        .signWith(SignatureAlgorithm.HS512, jwtSecret)
                        .compact();
                response.addHeader("jwtToken",token);
                System.out.println("Refreshing Token");
            }
        }
        return  true;
    }

    private String getJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ","");
        }

        return null;
    }
}
