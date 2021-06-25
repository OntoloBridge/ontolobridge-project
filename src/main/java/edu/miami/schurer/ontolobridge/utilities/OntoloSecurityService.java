package edu.miami.schurer.ontolobridge.utilities;


import edu.miami.schurer.ontolobridge.library.AuthLibrary;
import edu.miami.schurer.ontolobridge.library.NotificationLibrary;
import edu.miami.schurer.ontolobridge.library.RequestsLibrary;
import edu.miami.schurer.ontolobridge.library.UserRepository;
import edu.miami.schurer.ontolobridge.models.Detail;
import edu.miami.schurer.ontolobridge.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component("OntoloSecurityService")
public class OntoloSecurityService {

    AuthLibrary auth;

    @Autowired
    protected JdbcTemplate JDBCTemplate;

    @Autowired
    UserRepository userRepository;

    @PostConstruct
    void Init(){
        auth = new AuthLibrary(JDBCTemplate);
    }

    public boolean isRegistered(Authentication authentication) {
        Long userID = ((UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId(); //cache using user ID
        return isRegisteredCheck(userID);
    }

    public boolean isNotToken(Authentication authentication) {
        return !((UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isTokenLogin(); //cache using user ID
    }

    //Function split to allow caching based on user id
    @Cacheable(value = "userRegistered")
    public boolean isRegisteredCheck(Long userID) {
        User user =  userRepository.findById(userID).get();
        Set<Detail> details = user.getDetails();
        List<Map<String,Object>> requiredDetails = auth.GetAllDetails();
        List<Map<String,Object>> missingDetails = new ArrayList<>(requiredDetails);
        for(Map<String,Object> m: requiredDetails) {
            for (Detail d : details) {
                if (m.get("field").equals(d.getField()) && Integer.parseInt(m.get("required").toString()) == 1) {
                    missingDetails.remove(m);
                }
            }
            if (Integer.parseInt(m.get("required").toString()) != 1) {
                missingDetails.remove(m);
            }
        }
        return missingDetails.size() == 0;
    }
}