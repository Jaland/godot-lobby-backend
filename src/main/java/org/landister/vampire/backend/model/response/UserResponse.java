package org.landister.vampire.backend.model.response;

import java.util.Objects;

public class UserResponse extends BaseResponse{

  String username;
  

  public UserResponse() {
  }

  public UserResponse(String username) {
    this.username = username;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserResponse username(String username) {
    setUsername(username);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserResponse)) {
            return false;
        }
        UserResponse userResponse = (UserResponse) o;
        return Objects.equals(username, userResponse.username);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(username);
  }

  @Override
  public String toString() {
    return "{" +
      " username='" + getUsername() + "'" +
      "}";
  }
  
  
}
