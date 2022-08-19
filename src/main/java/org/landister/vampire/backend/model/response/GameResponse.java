package org.landister.vampire.backend.model.response;

import java.util.List;
import java.util.Objects;

import org.landister.vampire.backend.model.enums.GameState;

public class GameResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "game";

  String id;
  String name;
  GameState state;
  List<UserResponse> users;
  String host;
  


  public GameResponse() {
  }

  public GameResponse(String id, String name, GameState state, List<UserResponse> users, String host) {
    this.id = id;
    this.name = name;
    this.state = state;
    this.users = users;
    this.host = host;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public GameState getState() {
    return this.state;
  }

  public void setState(GameState state) {
    this.state = state;
  }

  public List<UserResponse> getUsers() {
    return this.users;
  }

  public void setUsers(List<UserResponse> users) {
    this.users = users;
  }

  public String getHost() {
    return this.host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public GameResponse id(String id) {
    setId(id);
    return this;
  }

  public GameResponse name(String name) {
    setName(name);
    return this;
  }

  public GameResponse state(GameState state) {
    setState(state);
    return this;
  }

  public GameResponse users(List<UserResponse> users) {
    setUsers(users);
    return this;
  }

  public GameResponse host(String host) {
    setHost(host);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GameResponse)) {
            return false;
        }
        GameResponse gameResponse = (GameResponse) o;
        return Objects.equals(id, gameResponse.id) && Objects.equals(name, gameResponse.name) && Objects.equals(state, gameResponse.state) && Objects.equals(users, gameResponse.users) && Objects.equals(host, gameResponse.host);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, state, users, host);
  }

  @Override
  public String toString() {
    return "{" +
      " id='" + getId() + "'" +
      ", name='" + getName() + "'" +
      ", state='" + getState() + "'" +
      ", users='" + getUsers() + "'" +
      ", host='" + getHost() + "'" +
      "}";
  }


}
