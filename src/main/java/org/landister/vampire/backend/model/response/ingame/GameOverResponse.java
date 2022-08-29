package org.landister.vampire.backend.model.response.ingame;

import java.util.Objects;

import org.landister.vampire.backend.model.response.BaseResponse;

public class GameOverResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "game_over";

  String winner;
  

  public GameOverResponse() {
    type = RESPONSE_TYPE;
  }

  public String getWinner() {
    return this.winner;
  }

  public void setWinner(String winner) {
    this.winner = winner;
  }

  public GameOverResponse winner(String winner) {
    setWinner(winner);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GameOverResponse)) {
            return false;
        }
        GameOverResponse gameOverResponse = (GameOverResponse) o;
        return Objects.equals(winner, gameOverResponse.winner);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(winner);
  }

  @Override
  public String toString() {
    return "{" +
      " winner='" + getWinner() + "'" +
      "}";
  }

}
