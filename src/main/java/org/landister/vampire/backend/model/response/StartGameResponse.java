package org.landister.vampire.backend.model.response;

import java.util.Objects;

public class StartGameResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "start_game";

  String gameId;


  public StartGameResponse() {
    this.type = RESPONSE_TYPE;
  }

  public String getGameId() {
    return this.gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }

  public StartGameResponse gameId(String gameId) {
    setGameId(gameId);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof StartGameResponse)) {
            return false;
        }
        StartGameResponse startGameResponse = (StartGameResponse) o;
        return Objects.equals(gameId, startGameResponse.gameId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(gameId);
  }

  @Override
  public String toString() {
    return "{" +
      " gameId='" + getGameId() + "'" +
      "}";
  }


}
