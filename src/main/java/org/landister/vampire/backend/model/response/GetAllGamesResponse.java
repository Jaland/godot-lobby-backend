package org.landister.vampire.backend.model.response;

import java.util.List;
import java.util.Objects;

public class GetAllGamesResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "game_list";

  public List<JoinGameResponse> games;

  public GetAllGamesResponse() {
    type = RESPONSE_TYPE;
  }

  public List<JoinGameResponse> getGames() {
    return this.games;
  }

  public void setGames(List<JoinGameResponse> games) {
    this.games = games;
  }

  public GetAllGamesResponse games(List<JoinGameResponse> games) {
    setGames(games);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GetAllGamesResponse)) {
            return false;
        }
        GetAllGamesResponse getAllGamesResponse = (GetAllGamesResponse) o;
        return Objects.equals(games, getAllGamesResponse.games);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(games);
  }

  @Override
  public String toString() {
    return "{" +
      " games='" + getGames() + "'" +
      "}";
  }

  
}
