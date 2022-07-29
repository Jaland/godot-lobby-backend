package org.landister.vampire.backend.model.request.auth;

import java.util.Objects;

import org.landister.vampire.backend.model.request.BaseRequest;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@JsonDeserialize( using = JsonDeserializer.None.class )
public class LoginRequest extends BaseRequest {

  String username;
  String password;
  boolean register = false;


  public LoginRequest() {
  }

  public LoginRequest(String username, String password, boolean register) {
    this.username = username;
    this.password = password;
    this.register = register;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isRegister() {
    return this.register;
  }

  public boolean getRegister() {
    return this.register;
  }

  public void setRegister(boolean register) {
    this.register = register;
  }

  public LoginRequest username(String username) {
    setUsername(username);
    return this;
  }

  public LoginRequest password(String password) {
    setPassword(password);
    return this;
  }

  public LoginRequest register(boolean register) {
    setRegister(register);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LoginRequest)) {
            return false;
        }
        LoginRequest loginRequest = (LoginRequest) o;
        return Objects.equals(username, loginRequest.username) && Objects.equals(password, loginRequest.password) && register == loginRequest.register;
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password, register);
  }

  @Override
  public String toString() {
    return "{" +
      " username='" + getUsername() + "'" +
      ", password='" + getPassword() + "'" +
      ", register='" + isRegister() + "'" +
      "}";
  }


  
}
