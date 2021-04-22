package edu.miami.schurer.ontolobridge.utilities;
import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import edu.miami.schurer.ontolobridge.library.RequestsLibrary;

import edu.miami.schurer.ontolobridge.models.*;
import edu.miami.schurer.ontolobridge.library.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Session;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("OntoloUserDetailsServiceImpl")
public class OntoloUserDetailsServiceImpl implements OntoloUserDetailsService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    RequestsLibrary req;

    @PostConstruct
    void Init(){
        //set the required libs to null as we only check term status
        req = new RequestsLibrary(jdbcTemplate,"",
                null,
                null,
                null,
                null);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User Not Found with -> username or email : " + email)
                );

        return UserPrinciple.build(user);
    }

    @Transactional
    public Optional<User> findByUserId(long id){
        Session session = (Session)entityManager.unwrap(Session.class);
        Optional<User> u = userRepository.findById(id);
        session.close();
        return u;
    }

    @Transactional @Override
    public User findByUserEmail(String email) {
        Session session = (Session)entityManager.unwrap(Session.class);
        User u = userRepository.findByEmail(email).get();
        session.close();
        return u;
    }

    @Transactional
    public User saveUser(User user) {
        user.setEncPassword(encoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return user;

    }

    public boolean emailExists(String email){
        return userRepository.existsByEmail(email);
    }
    public boolean isMaintainerOfRequests(Long userID,Long requestsID){
        List<Object> args = new ArrayList<>();
        args.add(userID);
        args.add(requestsID);
        List<Long> requestsIDs = jdbcTemplate.queryForList("select r.id from requests r " +
                "inner join ontologies o on r.assigned_ontology = o.ontology_short " +
                "inner join ontology_to_maintainer om on o.id = om.ontology_id " +
                "inner join maintainers m on m.id = om.maintainer_id " +
                "where m.user_id = ? and r.id = ?",args.toArray(),Long.class);
        return requestsIDs.size() > 0;
    }
    public boolean isOwnerOfRequests(Long userID,Long requestsID){
        List<Object> args = new ArrayList<>();
        args.add(userID);
        args.add(requestsID);
        List<Long> requestsIDs = jdbcTemplate.queryForList("select r.id from requests r " +
                "where r.user_id = ? and r.id = ?",args.toArray(),Long.class);
        return requestsIDs.size() > 0;
    }

    public List<StatusResponse> getMaintainerRequests(Long userid){
        List<Object> args = new ArrayList<>();
        args.add(userid);
        List<Long> requestsIDs = jdbcTemplate.queryForList("select r.id from requests r " +
                "inner join ontologies o on r.assigned_ontology = o.ontology_short " +
                "inner join ontology_to_maintainer om on o.id = om.ontology_id " +
                "inner join maintainers m on m.id = om.maintainer_id " +
                "where m.user_id = ?",args.toArray(),Long.class);
        List<StatusResponse> results = new ArrayList<>();
        for(Long id :requestsIDs){
            results.add(req.TermStatus(id,"maintainer").get(0));
        }
        return results;
    }
    public Long verifyPasswordReset(String token){
        List<Object> args = new ArrayList<>();
        args.add(token);
        Long userID = jdbcTemplate.queryForObject("select user_id from user_details where field = 'reset_key' and value = ?",args.toArray(),Long.class);
        return userID;
    }
}