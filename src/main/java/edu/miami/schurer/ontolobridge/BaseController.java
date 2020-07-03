package edu.miami.schurer.ontolobridge;

import edu.miami.schurer.ontolobridge.library.AuthLibrary;
import edu.miami.schurer.ontolobridge.library.NotificationLibrary;
import edu.miami.schurer.ontolobridge.library.RequestsLibrary;
import edu.miami.schurer.ontolobridge.utilities.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

public class BaseController {

    //Named template to allow insertion of array into query in SigCSmallMoleculeLibrary
    @Autowired
    protected JdbcTemplate JDBCTemplate;

    @Value("${api.cpanel.apitoken}")
    String cpanelApiKey;

    @Autowired
    private AppProperties appProp;

    NotificationLibrary notLib ;
    AuthLibrary auth;
    RequestsLibrary req;


    @PostConstruct
    void Init(){
        notLib = new NotificationLibrary(appProp);
        auth = new AuthLibrary(JDBCTemplate);
        req = new RequestsLibrary(JDBCTemplate,cpanelApiKey);
    }

    protected HashMap<String, Object> formatResults(HashMap<String, Object> results, List<?> data){
        results.put("data",data);
        results.put("count",data.size());
        return results;
    }
    protected HashMap<String, Object> formatResults(List<?> data){
        HashMap<String, Object> results = new HashMap<>();
        results.put("data",data);
        results.put("count",data.size());
        return results;
    }
    protected HashMap<String, Object> formatResults(HashMap<?,?> data){
        HashMap<String, Object> results = new HashMap<>();
        results.put("data",data);
        results.put("count",data.size());
        return results;
    }

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}