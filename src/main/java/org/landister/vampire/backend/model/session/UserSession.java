package org.landister.vampire.backend.model.session;

import java.util.Objects;
import javax.websocket.Session;

import org.landister.vampire.backend.services.SessionCacheService;


public class UserSession {

  Session session;
  String username;
  String token;
  String gameId = SessionCacheService.GLOBAL_GAME_ID;


  public UserSession() {
  }

  public UserSession(Session session, String username, String token, String gameId) {
    this.session = session;
    this.username = username;
    this.token = token;
    this.gameId = gameId;
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

  public String getGameId() {
    return this.gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
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

  public UserSession gameId(String gameId) {
    setGameId(gameId);
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
        return Objects.equals(session, userSession.session) && Objects.equals(username, userSession.username) && Objects.equals(token, userSession.token) && Objects.equals(gameId, userSession.gameId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(session, username, token, gameId);
  }

  @Override
  public String toString() {
    return "{" +
      " session='" + getSession() + "'" +
      ", username='" + getUsername() + "'" +
      ", token='" + getToken() + "'" +
      ", gameId='" + getGameId() + "'" +
      "}";
  }
  
}
