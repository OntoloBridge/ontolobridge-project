package edu.miami.schurer.ontolobridge.utilities;

import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import edu.miami.schurer.ontolobridge.models.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OntoloUserDetailsService extends UserDetailsService {
    User saveUser(User user);
    Optional<User> findByUserId(long id);
    User findByUserEmail(String email);
    boolean emailExists(String email);
    boolean isOwnerOfRequests(Long requestsID,Long userID);
    boolean isMaintainerOfRequests(Long requestsID,Long userID);
    List<StatusResponse> getMaintainerRequests(Long userid);
    Long verifyPasswordReset(String token);

    List<Map<String,Object>> getAppPass(Long userid);
    Long checkAppPass(String token) throws NoSuchAlgorithmException;
    String addAppPass(Long userid,String App,String comment) throws NoSuchAlgorithmException;
    void deleteAppPass(Long id, Long user_id);
}
