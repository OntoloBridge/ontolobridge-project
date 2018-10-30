package edu.miami.schurer.ontologbridge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;

public class BaseController {

    //Named template to allow insertion of array into query in SigCSmallMoleculeLibrary
    @Autowired
    protected JdbcTemplate JDBCTemplate;

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
}