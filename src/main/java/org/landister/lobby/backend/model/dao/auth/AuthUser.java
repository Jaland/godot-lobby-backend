package org.landister.lobby.backend.model.dao.auth;

import java.util.Objects;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "users", database = "vampire")
public class AuthUser extends PanacheMongoEntity {
    
    private String username;
    private String token;


    // Data Retrieval Methods
    public static AuthUser findByUsername(String username){
        return find("username", username).firstResult();
    }


    public AuthUser() {
    }

    public AuthUser(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthUser username(String username) {
        setUsername(username);
        return this;
    }

    public AuthUser token(String token) {
        setToken(token);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AuthUser)) {
            return false;
        }
        AuthUser user = (AuthUser) o;
        return Objects.equals(username, user.username) && Objects.equals(token, user.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, token);
    }

    @Override
    public String toString() {
        return "{" +
            " username='" + getUsername() + "'" +
            ", token='" + getToken() + "'" +
            "}";
    }
    

}
