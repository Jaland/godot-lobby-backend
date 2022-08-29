package org.landister.vampire.backend.model.request;

import java.util.Objects;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.landister.vampire.backend.model.request.auth.InitialRequest;
import org.landister.vampire.backend.model.request.auth.LoginRequest;
import org.landister.vampire.backend.model.request.ingame.GoalTouchedRequest;
import org.landister.vampire.backend.model.request.ingame.LoadAssetsRequest;
import org.landister.vampire.backend.model.request.ingame.PlayerUpdateRequest;
import org.landister.vampire.backend.model.request.ingame.RestartGameRequest;
import org.landister.vampire.backend.model.request.ingame.StartGameRequest;
import org.landister.vampire.backend.model.request.lobby.CreateGameRequest;
import org.landister.vampire.backend.model.request.lobby.JoinGameRequest;
import org.landister.vampire.backend.model.request.lobby.JoinLobbyRequest;
import org.landister.vampire.backend.model.request.lobby.LeaveGameRequest;
import org.landister.vampire.backend.model.request.lobby.LobbyRefreshRequest;
import org.landister.vampire.backend.services.SessionCacheService;
import org.landister.vampire.backend.util.PropertyBasedDeserializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Base class for all requests.
 * 
 * Important: Make sure to override the @JsonDeserialize annotation with @JsonDeserialize( using = JsonDeserializer.None.class )
 * Otherwise you will end up in an infinite recursion situation.
 * 
 * @author Landister

 */
@JsonDeserialize( using = PropertyBasedDeserializer.class )
public class BaseRequest {

  public enum RequestType {
    LOGIN(LoginRequest.class),
    CHAT(ChatRequest.class),
    INITIAL_REQUEST(InitialRequest.class),
    JOIN_LOBBY(JoinLobbyRequest.class),
    LOBBY_REFRESH(LobbyRefreshRequest.class),
    CREATE_GAME(CreateGameRequest.class), 
    JOIN_GAME(JoinGameRequest.class),
    LEAVE_GAME(LeaveGameRequest.class), 
    START_GAME(StartGameRequest.class),
    RESTART_GAME(RestartGameRequest.class),
    LOAD_ASSETS(LoadAssetsRequest.class),
    PLAYER_UPDATE(PlayerUpdateRequest.class),
    GOAL_TOUCHED(GoalTouchedRequest.class),
    ;

  Class<? extends BaseRequest> requestClass;

    RequestType(Class<? extends BaseRequest> c) {
      requestClass = c;
    }

    public Class<? extends BaseRequest> getRequestClass() {
      return requestClass;
    }
  }

  RequestType requestType;

  String gameId = SessionCacheService.GLOBAL_GAME_ID;
  String token;

  // Populated in BaseResponse and contains user info
  @JsonIgnore
  JsonWebToken jwt;


  public BaseRequest() {
  }

  public BaseRequest(RequestType requestType, String gameId, String token, JsonWebToken jwt) {
    this.requestType = requestType;
    this.gameId = gameId;
    this.token = token;
    this.jwt = jwt;
  }

  public RequestType getRequestType() {
    return this.requestType;
  }

  public void setRequestType(RequestType requestType) {
    this.requestType = requestType;
  }

  public String getGameId() {
    return this.gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }

  public String getToken() {
    return this.token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public JsonWebToken getJwt() {
    return this.jwt;
  }

  public void setJwt(JsonWebToken jwt) {
    this.jwt = jwt;
  }

  public BaseRequest requestType(RequestType requestType) {
    setRequestType(requestType);
    return this;
  }

  public BaseRequest gameId(String gameId) {
    setGameId(gameId);
    return this;
  }

  public BaseRequest token(String token) {
    setToken(token);
    return this;
  }

  public BaseRequest jwt(JsonWebToken jwt) {
    setJwt(jwt);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BaseRequest)) {
            return false;
        }
        BaseRequest baseRequest = (BaseRequest) o;
        return Objects.equals(requestType, baseRequest.requestType) && Objects.equals(gameId, baseRequest.gameId) && Objects.equals(token, baseRequest.token) && Objects.equals(jwt, baseRequest.jwt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestType, gameId, token, jwt);
  }

  @Override
  public String toString() {
    return "{" +
      " requestType='" + getRequestType() + "'" +
      ", gameId='" + getGameId() + "'" +
      ", token='" + getToken() + "'" +
      ", jwt='" + getJwt() + "'" +
      "}";
  }
  
}
