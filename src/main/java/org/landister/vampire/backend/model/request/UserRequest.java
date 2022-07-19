package org.landister.vampire.backend.model.request;

import java.util.Objects;

import org.landister.vampire.backend.util.PropertyBasedDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = PropertyBasedDeserializer.class )
public class UserRequest {

  public enum RequestType {
    LOGIN(LoginRequest.class),
    CHAT(ChatRequest.class),
    AUTH(AuthRequest.class);

    Class<? extends UserRequest> requestClass;

    RequestType(Class<? extends UserRequest> c) {
      requestClass = c;
    }

    public Class<? extends UserRequest> getRequestClass() {
      return requestClass;
    }
  }

  RequestType requestType;

  public UserRequest() {
  }

  public UserRequest(RequestType requestType) {
    this.requestType = requestType;
  }

  public RequestType getRequestType() {
    return this.requestType;
  }

  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }

  public UserRequest requestType(RequestType requestType) {
    setRequestType(requestType);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserRequest)) {
            return false;
        }
        UserRequest userRequest = (UserRequest) o;
        return Objects.equals(requestType, userRequest.requestType);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(requestType);
  }

  @Override
  public String toString() {
    return "{" +
      " requestType='" + getRequestType() + "'" +
      "}";
  }


  
}
