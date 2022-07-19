package org.landister.vampire.backend.model.session;

import java.util.Objects;
import javax.websocket.Session;


public class UserSession {

  Session session;
  String username;
  String token;


  public UserSession() {
  }

  public UserSession(Session session, String username, String token) {
    this.session = session;
    this.username = username;
    this.token = token;
  }

  public Session getSession() {
    return this.session;
  }

  public void setSession(Session session) {
    this.session = session;
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

  public UserSession session(Session session) {
    setSession(session);
    return this;
  }

  public UserSession username(String username) {
    setUsername(username);
    return this;
  }

  public UserSession token(String token) {
    setToken(token);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserSession)) {
            return false;
        }
        UserSession userSession = (UserSession) o;
        return Objects.equals(session, userSession.session) && Objects.equals(username, userSession.username) && Objects.equals(token, userSession.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(session, username, token);
  }

  @Override
  public String toString() {
    return "{" +
      " session='" + getSession() + "'" +
      ", username='" + getUsername() + "'" +
      ", token='" + getToken() + "'" +
      "}";
  }
  
  
}
