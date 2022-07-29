package org.landister.vampire.backend.model.request.lobby;

import java.util.Objects;

import org.landister.vampire.backend.model.request.BaseRequest;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class JoinGameRequest extends BaseRequest {  

  String joinGameId;

  public JoinGameRequest() {
  }

  public JoinGameRequest(String joinGameId) {
    this.joinGameId = joinGameId;
  }

  public String getJoinGameId() {
    return this.joinGameId;
  }

  public void setJoinGameId(String joinGameId) {
    this.joinGameId = joinGameId;
  }

  public JoinGameRequest joinGameId(String joinGameId) {
    setJoinGameId(joinGameId);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof JoinGameRequest)) {
            return false;
        }
        JoinGameRequest joinGameRequest = (JoinGameRequest) o;
        return Objects.equals(joinGameId, joinGameRequest.joinGameId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(joinGameId);
  }

  @Override
  public String toString() {
    return "{" +
      " joinGameId='" + getJoinGameId() + "'" +
      "}";
  }

}
