package org.landister.vampire.backend.model.response.ingame;

import java.util.Map;
import java.util.Objects;

import org.landister.vampire.backend.model.response.BaseResponse;
import org.landister.vampire.backend.model.response.ingame.inner.Player;

public class MapSetupResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "map_setup";

  public Map<String, Player> players;


  public MapSetupResponse() {
    type = RESPONSE_TYPE;
  }

  public Map<String,Player> getPlayers() {
    return this.players;
  }

  public void setPlayers(Map<String,Player> players) {
    this.players = players;
  }

  public MapSetupResponse players(Map<String,Player> players) {
    setPlayers(players);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MapSetupResponse)) {
            return false;
        }
        MapSetupResponse mapSetupResponse = (MapSetupResponse) o;
        return Objects.equals(players, mapSetupResponse.players);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(players);
  }

  @Override
  public String toString() {
    return "{" +
      " players='" + getPlayers() + "'" +
      "}";
  }

}
