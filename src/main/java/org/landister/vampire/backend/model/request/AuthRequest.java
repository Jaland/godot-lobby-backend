package org.landister.vampire.backend.model.request;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class AuthRequest extends UserRequest {

  String token;


  public AuthRequest() {
  }

  public AuthRequest(String token) {
    this.token = token;
  }

  public String getToken() {
    return this.token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public AuthRequest token(String token) {
    setToken(token);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AuthRequest)) {
            return false;
        }
        AuthRequest authenticationRequest = (AuthRequest) o;
        return Objects.equals(token, authenticationRequest.token);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(token);
  }

  @Override
  public String toString() {
    return "{" +
      " token='" + getToken() + "'" +
      "}";
  }
  
  
}
