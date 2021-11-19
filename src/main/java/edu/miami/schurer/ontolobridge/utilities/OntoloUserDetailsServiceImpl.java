package edu.miami.schurer.ontolobridge.utilities;
import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import edu.miami.schurer.ontolobridge.library.RequestsLibrary;

import edu.miami.schurer.ontolobridge.models.*;
import edu.miami.schurer.ontolobridge.library.*;
import io.sentry.Sentry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static edu.miami.schurer.ontolobridge.utilities.DbUtil.genRandomString;

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
                null,
                null);
    }

    @PersistenceContext
    private EntityManager entityManager;

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

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
        Optional<User> u = userRepository.findByEmail(email);
        if(!u.isPresent())
            return null;
        session.close();
        return u.get();
    }

    @Transactional
    public User saveUser(User user) {
        if(!user.getPasswordEncrypted())
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
    public Long verifyPasswordReset(String token) throws EmptyResultDataAccessException {
        List<Object> args = new ArrayList<>();
        args.add(token);
        return jdbcTemplate.queryForObject("select user_id from user_details where field = 'reset_key' and value = ?",args.toArray(),Long.class);
    }

    public List<Map<String,Object>> getAppPass(Long userid) throws EmptyResultDataAccessException {
        List<Object> args = new ArrayList<>();
        args.add(userid);
        return jdbcTemplate.queryForList("select id,app,create_date,comment from application_passwords where user_id =  ?",args.toArray());
    }

    public Long checkAppPass(String token) throws EmptyResultDataAccessException {
        List<Object> args = new ArrayList<>();
        args.add(token);
        Long id = 0L;
        //check if the application password was added manually and update it to a secure version
        try {
           id = jdbcTemplate.queryForObject("select application_passwords.id from application_passwords where application_passwords.password = ?", args.toArray(), Long.class);
        } catch (EmptyResultDataAccessException ignored){

        }
        byte[] hash;
        if(id != null && 0 != id){
            args = new ArrayList<>();

            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            }catch (NoSuchAlgorithmException ex){
                System.out.println("NO SHA256");
                Sentry.capture(ex);
                return 0L;

            }
            args.add("$"+bytesToHex(hash));
            args.add(id);
            jdbcTemplate.update("UPDATE application_passwords set password = ? where id = ?",args.toArray());
            args = new ArrayList<>();
        }
        args = new ArrayList<>();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
        }catch (NoSuchAlgorithmException ex){
            System.out.println("NO SHA256");
            Sentry.capture(ex);
            return 0L;

        }
        args.add("$"+bytesToHex(hash));
        return jdbcTemplate.queryForObject("select application_passwords.user_id from application_passwords where application_passwords.password = ?",args.toArray(),Long.class);
    }


    public String addAppPass(Long userid,String App,String comment) throws NoSuchAlgorithmException {
        List<Object> args = new ArrayList<>();
        String Password = "";
        Password = genRandomString(32);
        args.add(userid);
        args.add(App);
        args.add(comment);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(Password.getBytes(StandardCharsets.UTF_8));
        args.add("$"+bytesToHex(hash));
        jdbcTemplate.update("insert into application_passwords (user_id, app, comment, password) VALUES (?,?,?,?);",args.toArray());
        return Password;
    }

    public void deleteAppPass(Long id,Long user_id){
        List<Object> args = new ArrayList<>();
        args.add(id);
        args.add(user_id);
        jdbcTemplate.update("delete from application_passwords where id = ? and user_id = ?",args);
    }
}