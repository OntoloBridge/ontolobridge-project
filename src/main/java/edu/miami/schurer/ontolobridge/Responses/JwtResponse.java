package edu.miami.schurer.ontolobridge.Responses;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private long id;

    public JwtResponse(String accessToken,String username,long id) {
        this.token = accessToken;
        this.username = username;
        this.id = id;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.token = username;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}