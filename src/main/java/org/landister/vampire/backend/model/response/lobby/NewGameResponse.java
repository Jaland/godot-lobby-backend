package org.landister.vampire.backend.model.response.lobby;

import java.util.Objects;

import org.landister.vampire.backend.model.response.BaseResponse;
import org.landister.vampire.backend.model.response.GameResponse;

public class NewGameResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "new_game";

  public GameResponse game;

  public NewGameResponse() {
    type = RESPONSE_TYPE;
  }

  public GameResponse getGame() {
    return this.game;
  }

  public void setGame(GameResponse game) {
    this.game = game;
  }

  public NewGameResponse game(GameResponse game) {
    setGame(game);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof NewGameResponse)) {
            return false;
        }
        NewGameResponse newGameResponse = (NewGameResponse) o;
        return Objects.equals(game, newGameResponse.game);
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
