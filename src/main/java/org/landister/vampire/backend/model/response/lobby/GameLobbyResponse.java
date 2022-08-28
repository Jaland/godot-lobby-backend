package org.landister.vampire.backend.model.response.lobby;

import java.util.Objects;

import org.landister.vampire.backend.model.response.BaseResponse;
import org.landister.vampire.backend.model.response.GameResponse;

public class GameLobbyResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "join_game";

  GameResponse game;

  public GameLobbyResponse() {
    type = RESPONSE_TYPE;
  }

  public GameResponse getGame() {
    return this.game;
  }

  public void setGame(GameResponse game) {
    this.game = game;
  }

  public GameLobbyResponse game(GameResponse game) {
    setGame(game);
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
        return Objects.equals(game, gameLobbyResponse.game);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(game);
  }

  @Override
  public String toString() {
    return "{" +
      " game='" + getGame() + "'" +
      "}";
  }
  

}
