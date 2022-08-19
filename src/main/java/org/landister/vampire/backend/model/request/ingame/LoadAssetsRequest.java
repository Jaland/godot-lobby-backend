package org.landister.vampire.backend.model.request.ingame;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.landister.vampire.backend.model.request.BaseRequest;
import org.landister.vampire.backend.model.request.ingame.inner.Player;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class LoadAssetsRequest extends BaseRequest {  

  Map<String, Player> players;


  public LoadAssetsRequest() {
  }

  public LoadAssetsRequest(Map<String,Player> players) {
    this.players = players;
  }

  public Map<String,Player> getPlayers() {
    return this.players;
  }

  public void setPlayers(Map<String,Player> players) {
    this.players = players;
  }

  public LoadAssetsRequest players(Map<String,Player> players) {
    setPlayers(players);
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
        return Objects.equals(players, loadAssetsRequest.players);
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
