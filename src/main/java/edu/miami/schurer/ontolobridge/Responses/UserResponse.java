package edu.miami.schurer.ontolobridge.Responses;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.sql.ResultSet;
import java.sql.SQLException;

@ApiModel(description = "Response returned for all term status requests")
public class UserResponse{

    @ApiModelProperty(required = true, example = "0")
    public int user_id;

    @ApiModelProperty(required = true, example = "test")
    public String username;

    @ApiModelProperty(required = true, example = "test@example.com")
    public String email;

    @ApiModelProperty(required = true, example = "true")
    public boolean verified;


    public UserResponse() {
    }

    public UserResponse(ResultSet data) throws SQLException {
        this.user_id = (int)data.getInt("id");
        this.username = (String)data.getString("email");
        this.email = (String)data.getString("email");;
        this.verified = data.getBoolean("verified");
    }

    public UserResponse(int user_id, String username, String email, boolean verified) {
        this.user_id = user_id;
        this.username = email;
        this.email = email;
        this.verified = verified;
    }
}