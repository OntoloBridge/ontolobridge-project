package edu.miami.schurer.ontolobridge.utilities;


import edu.miami.schurer.ontolobridge.library.AuthLibrary;
import edu.miami.schurer.ontolobridge.library.UserRepository;
import edu.miami.schurer.ontolobridge.models.Detail;
import edu.miami.schurer.ontolobridge.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;


@Component("OntoloSecurityService")
public class OntoloSecurityService {

    AuthLibrary auth;

    private Set<String> roles = null;
    private String defaultRolePrefix = "ROLE_";
    private RoleHierarchy roleHierarchy;

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
        return isRegisteredCheck(userID) || hasRole("ROLE_SYSTEM");
    }

    public boolean isNotToken(Authentication authentication) {
        return !((UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isTokenLogin(); //cache using user ID
    }

    public final boolean hasRole(String... roles) {
        Set<String> roleSet = getAuthoritySet();

         for (String role : roles) {
            String defaultedRole = getRoleWithDefaultPrefix(role);
            if (roleSet.contains(defaultedRole)) {
                return true;
            }
        }

        return false;
    }

    private static String getRoleWithDefaultPrefix(String role) {
        if (role == null) {
            return role;
        }
        if (role.startsWith("ROLE_")) {
            return role;
        }
        return "ROLE_" + role;
    }

    private Set<String> getAuthoritySet() {
        roles = new HashSet<>();
        Collection<? extends GrantedAuthority> userAuthorities = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities();

        if (roleHierarchy != null) {
            userAuthorities = roleHierarchy
                    .getReachableGrantedAuthorities(userAuthorities);
        }

        roles = AuthorityUtils.authorityListToSet(userAuthorities);
        return roles;
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