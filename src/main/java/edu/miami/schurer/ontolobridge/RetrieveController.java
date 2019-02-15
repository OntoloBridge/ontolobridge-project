package edu.miami.schurer.ontolobridge;

import com.sun.deploy.net.HttpResponse;
import edu.miami.schurer.ontolobridge.Responses.ExceptionResponse;
import edu.miami.schurer.ontolobridge.Responses.OperationResponse;
import edu.miami.schurer.ontolobridge.Responses.RequestResponse;
import edu.miami.schurer.ontolobridge.Responses.StatusResponse;
import edu.miami.schurer.ontolobridge.library.RequestsLibrary;
import edu.miami.schurer.ontolobridge.utilities.OntoloException;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Null;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/Retrieve")
public class RetrieveController extends BaseController {

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful requests",response = RequestResponse.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ExceptionResponse.class)
    }
    )
    @RequestMapping(path="/Requests", method= RequestMethod.GET)
    public @ResponseBody ResponseEntity requestTerm(@ApiParam(value = "Ontology to get new terms for" ,required = true) @RequestParam(value="ontology") String ontology,
                               @ApiParam(value = "Minimum status to return",defaultValue = " ",required = false) @RequestParam(value="status") String status,
                               @ApiParam(value = "Type of request to return to return",defaultValue = " ",required = false) @RequestParam(value="type") String type ) throws OntoloException {

        ResponseEntity respEntity = null;


        String sql = "select label,description,uri_ontology,uri_identifier,superclass_uri,superclass_ontology,superclass_id,superclass_id,current_message,type,id from requests where uri_ontology = ? and status != 'rejected'";
        StringBuilder csv = new StringBuilder();
        List<Object> args = new ArrayList<>();
        args.add(ontology);

        HashMap<String,Object> termMap = new HashMap<>();
        ArrayList<String> keys = new ArrayList<>();
        JDBCTemplate.query(sql,
                args.toArray(),
                new RowMapper<Object>() {
                   public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                       ResultSetMetaData metadata = rs.getMetaData();
                       HashMap<String, String> array = new HashMap<>();
                       array.put("termType", "new " + rs.getString("type"));
                       if (rowNum == 1)
                           keys.add("termType");
                       for (int i = 1; i <= metadata.getColumnCount(); i++) {
                           if (metadata.getColumnName(i).equals("superclass_uri") && rowNum != 1) {
                               keys.add("superclass_label");
                               keys.add("superclass");
                           }
                           if (metadata.getColumnName(i).contains("superclass"))
                               continue;
                           array.put(metadata.getColumnName(i), rs.getString(i));
                           if (rowNum != 1)
                               keys.add(metadata.getColumnName(i));
                       }
                       if (rs.getString("superclass_uri") != null && !rs.getString("superclass_uri").isEmpty())
                           array.put("superclass", rs.getString("superclass_uri"));
                       else if (rs.getString("superclass_ontology") != null && !rs.getString("superclass_ontology").isEmpty() && rs.getString("superclass_id") != null && !rs.getString("superclass_id").isEmpty())
                           array.put("superclass", rs.getString("superclass_ontology") + "_" + rs.getString("superclass_id"));
                       else if (rs.getString("superclass_id") != null && isInteger(rs.getString("superclass_id")))
                           array.put("superclass", "ONTB_" + rs.getString("superclass_id"));
                       else
                           array.put("superclass", "");
                       termMap.put("ONTB_" + rs.getString("id"), array);
                       return 0;
                    }
                });
        List<Object> outList = new ArrayList<>();
        int i = 0;
        //loop through excess array and get only those that don't have a parent still in the array and add them to the end of output
        System.out.println("\r\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        for (String key : termMap.keySet()) {
            System.out.println(key);
        }
        System.out.println("\r\n\n\n\n");
        while(termMap.size() > outList.size()){
            for (String key : termMap.keySet()) {
                if(!termMap.get(key).getClass().getName().equals("java.util.HashMap"))
                    throw new OntoloException("Invalid object");
                HashMap o = (HashMap)termMap.get(key);
                if(!outList.contains(o) && (!termMap.containsKey((String)o.get("superclass")) || outList.contains(termMap.get((String)o.get("superclass"))))){
                    if(termMap.containsKey((String)o.get("superclass"))){
                        HashMap p = (HashMap)termMap.get((String)o.get("superclass"));
                        o.put("superclass_label",(String)p.get("label"));
                    }
                    outList.add(o);
                }
            }
            i++;
            if(i>10000){
                throw new OntoloException("Too many iterations");
            }
        }
        for(Object o: outList){
            HashMap<String,String> array =(HashMap)o;
            if(csv.length()==0){
                for(String s: keys){
                    csv.append(s);
                    csv.append(",");
                }
                csv.append("\n");
            }
            for (String s : keys) {
                csv.append(array.get(s));
                csv.append(",");
            }
            csv.append("\n");
        }
        DateFormat formatter = new SimpleDateFormat("ddMMyyyy");

        Date today = new Date();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type","text/csv");
        responseHeaders.add( "content-disposition", "attachment;filename="+ontology+"-newTerms-"+formatter.format(today)+".csv");
        respEntity = new ResponseEntity(csv.toString(), responseHeaders, HttpStatus.OK);
        return respEntity;
    }
}
