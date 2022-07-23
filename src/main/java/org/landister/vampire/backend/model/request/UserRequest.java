package org.landister.vampire.backend.model.request;

import java.util.Objects;

import org.landister.vampire.backend.model.request.auth.AuthRequest;
import org.landister.vampire.backend.model.request.auth.LoginRequest;
import org.landister.vampire.backend.model.request.lobby.CreateGameRequest;
import org.landister.vampire.backend.model.request.lobby.LobbyRefreshRequest;
import org.landister.vampire.backend.services.SessionCacheService;
import org.landister.vampire.backend.util.PropertyBasedDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = PropertyBasedDeserializer.class )
public class UserRequest {

  public enum RequestType {
    LOGIN(LoginRequest.class),
    CHAT(ChatRequest.class),
    AUTH(AuthRequest.class),
    LOBBY_REFRESH(LobbyRefreshRequest.class),
    CREATE_GAME(CreateGameRequest.class),;

  Class<? extends UserRequest> requestClass;


    RequestType(Class<? extends UserRequest> c) {
      requestClass = c;
    }

    public Class<? extends UserRequest> getRequestClass() {
      return requestClass;
    }
  }

  RequestType requestType;

  Integer gameId = SessionCacheService.GLOBAL_GAME_ID;


  public UserRequest() {
  }

  public UserRequest(RequestType requestType, Integer gameId) {
    this.requestType = requestType;
    this.gameId = gameId;
  }

  public RequestType getRequestType() {
    return this.requestType;
  }

  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }

  public Integer getGameId() {
    return this.gameId;
  }

  public void setGameId(Integer gameId) {
    this.gameId = gameId;
  }

  public UserRequest requestType(RequestType requestType) {
    setRequestType(requestType);
    return this;
  }

  public UserRequest gameId(Integer gameId) {
    setGameId(gameId);
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
        return Objects.equals(requestType, userRequest.requestType) && Objects.equals(gameId, userRequest.gameId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestType, gameId);
  }

  @Override
  public String toString() {
    return "{" +
      " requestType='" + getRequestType() + "'" +
      ", gameId='" + getGameId() + "'" +
      "}";
  }

  
}
