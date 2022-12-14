package org.landister.lobby.backend.model.response.ingame;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.landister.lobby.backend.model.response.BaseResponse;
import org.landister.lobby.backend.model.shared.Goal;
import org.landister.lobby.backend.model.shared.Player;

public class MapSetupResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "map_setup";

  private Map<String, Player> players = new HashMap<String, Player>();

  private String host;

  private Goal goal;


  public MapSetupResponse() {
    type = RESPONSE_TYPE;
  }
  

  public MapSetupResponse(Map<String,Player> players, String host, Goal goal) {
    this.players = players;
    this.host = host;
    this.goal = goal;
  }

  public Map<String,Player> getPlayers() {
    return this.players;
  }

  public void setPlayers(Map<String,Player> players) {
    this.players = players;
  }

  public String getHost() {
    return this.host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Goal getGoal() {
    return this.goal;
  }

  public void setGoal(Goal goal) {
    this.goal = goal;
  }

  public MapSetupResponse players(Map<String,Player> players) {
    setPlayers(players);
    return this;
  }

  public MapSetupResponse host(String host) {
    setHost(host);
    return this;
  }

  public MapSetupResponse goal(Goal goal) {
    setGoal(goal);
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
        return Objects.equals(players, mapSetupResponse.players) && Objects.equals(host, mapSetupResponse.host) && Objects.equals(goal, mapSetupResponse.goal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(players, host, goal);
  }

  @Override
  public String toString() {
    return "{" +
      " players='" + getPlayers() + "'" +
      ", host='" + getHost() + "'" +
      ", goal='" + getGoal() + "'" +
      "}";
  }


}
