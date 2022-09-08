package org.landister.lobby.backend.model.request.ingame;

import java.util.Map;
import java.util.Objects;

import org.landister.lobby.backend.model.request.BaseRequest;
import org.landister.lobby.backend.model.shared.Goal;
import org.landister.lobby.backend.model.shared.Player;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class LoadAssetsRequest extends BaseRequest {  

  Map<String, Player> players;
  
  Goal goal;


  public LoadAssetsRequest() {
  }

  public LoadAssetsRequest(Map<String,Player> players, Goal goal) {
    this.players = players;
    this.goal = goal;
  }

  public Map<String,Player> getPlayers() {
    return this.players;
  }

  public void setPlayers(Map<String,Player> players) {
    this.players = players;
  }

  public Goal getGoal() {
    return this.goal;
  }

  public void setGoal(Goal goal) {
    this.goal = goal;
  }

  public LoadAssetsRequest players(Map<String,Player> players) {
    setPlayers(players);
    return this;
  }

  public LoadAssetsRequest goal(Goal goal) {
    setGoal(goal);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LoadAssetsRequest)) {
            return false;
        }
        LoadAssetsRequest loadAssetsRequest = (LoadAssetsRequest) o;
        return Objects.equals(players, loadAssetsRequest.players) && Objects.equals(goal, loadAssetsRequest.goal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(players, goal);
  }

  @Override
  public String toString() {
    return "{" +
      " players='" + getPlayers() + "'" +
      ", goal='" + getGoal() + "'" +
      "}";
  }

  
}
