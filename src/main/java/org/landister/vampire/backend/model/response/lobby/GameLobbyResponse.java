package org.landister.vampire.backend.model.response.lobby;

import java.util.List;
import java.util.Objects;

import org.landister.vampire.backend.model.response.BaseResponse;
import org.landister.vampire.backend.model.response.GameResponse;
import org.landister.vampire.backend.model.response.UserResponse;

public class GameLobbyResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "join_game";

  GameResponse game;

  List<UserResponse> users;


  public GameLobbyResponse() {
    type = RESPONSE_TYPE;
  }

  public GameResponse getGame() {
    return this.game;
  }

  public void setGame(GameResponse game) {
    this.game = game;
  }

  public List<UserResponse> getUsers() {
    return this.users;
  }

  public void setUsers(List<UserResponse> users) {
    this.users = users;
  }

  public GameLobbyResponse game(GameResponse game) {
    setGame(game);
    return this;
  }

  public GameLobbyResponse users(List<UserResponse> users) {
    setUsers(users);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GameLobbyResponse)) {
            return false;
        }
        GameLobbyResponse gameLobbyResponse = (GameLobbyResponse) o;
        return Objects.equals(game, gameLobbyResponse.game) && Objects.equals(users, gameLobbyResponse.users);
  }

  @Override
  public int hashCode() {
    return Objects.hash(game, users);
  }

  @Override
  public String toString() {
    return "{" +
      " game='" + getGame() + "'" +
      ", users='" + getUsers() + "'" +
      "}";
  }

  

}
