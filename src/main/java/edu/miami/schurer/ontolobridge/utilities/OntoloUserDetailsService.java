package edu.miami.schurer.ontolobridge.utilities;

import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import edu.miami.schurer.ontolobridge.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface OntoloUserDetailsService extends UserDetailsService {
    User saveUser(User user);
    Optional<User> findByUserId(long id);
    User findByUserEmail(String email);
    boolean emailExists(String email);
    boolean isOwnerOfRequests(Long requestsID,Long userID);
    boolean isMaintainerOfRequests(Long requestsID,Long userID);
    List<StatusResponse> getMaintainerRequests(Long userid);
    public Long verifyPasswordReset(String token);
}
