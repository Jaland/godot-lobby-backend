package org.landister.lobby.backend.model.response.lobby;

import java.util.List;
import java.util.Objects;

import org.landister.lobby.backend.model.response.BaseResponse;
import org.landister.lobby.backend.model.response.GameResponse;

public class GetAllGamesResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "game_list";

  public List<GameResponse> games;

  public GetAllGamesResponse() {
    type = RESPONSE_TYPE;
  }

  public List<GameResponse> getGames() {
    return this.games;
  }

  public void setGames(List<GameResponse> games) {
    this.games = games;
  }

  public GetAllGamesResponse games(List<GameResponse> games) {
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
