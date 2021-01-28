package edu.miami.schurer.ontolobridge.utilities;
import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import edu.miami.schurer.ontolobridge.library.RequestsLibrary;

import edu.miami.schurer.ontolobridge.models.*;
import edu.miami.schurer.ontolobridge.library.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Session;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service("OntoloUserDetailsServiceImpl")
public class OntoloUserDetailsServiceImpl implements OntoloUserDetailsService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    UserRepository userRepository;

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
    public User findByUserId(long id){
        Session session = (Session)entityManager.unwrap(Session.class);
        User u = userRepository.findById(id).get();
        session.close();
        return u;
    }

    @Transactional
    public User saveUser(User user) {
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
}