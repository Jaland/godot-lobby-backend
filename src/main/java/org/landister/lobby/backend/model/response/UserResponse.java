package org.landister.lobby.backend.model.response;

import java.util.Objects;

public class UserResponse extends BaseResponse{
  
  final static String RESPONSE_TYPE = "user";

  String username;
  // Assume connected
  boolean connected = true;


  public UserResponse() {
    type=RESPONSE_TYPE;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public boolean isConnected() {
    return this.connected;
  }

  public boolean getConnected() {
    return this.connected;
  }

  public void setConnected(boolean connected) {
    this.connected = connected;
  }

  public UserResponse username(String username) {
    setUsername(username);
    return this;
  }

  public UserResponse connected(boolean connected) {
    setConnected(connected);
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
        return Objects.equals(username, userResponse.username) && connected == userResponse.connected;
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, connected);
  }

  @Override
  public String toString() {
    return "{" +
      " username='" + getUsername() + "'" +
      ", connected='" + isConnected() + "'" +
      "}";
  }


}
