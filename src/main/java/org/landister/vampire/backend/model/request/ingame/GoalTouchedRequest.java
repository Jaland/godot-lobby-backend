package org.landister.vampire.backend.model.request.ingame;

import java.util.Objects;

import org.landister.vampire.backend.model.request.BaseRequest;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class GoalTouchedRequest extends BaseRequest {

  String winner;
  String host;


  public GoalTouchedRequest() {
  }


  public GoalTouchedRequest(String winner) {
    this.winner = winner;
  }

  public String getWinner() {
    return this.winner;
  }

  public void setWinner(String winner) {
    this.winner = winner;
  }

  public GoalTouchedRequest winner(String winner) {
    setWinner(winner);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GoalTouchedRequest)) {
            return false;
        }
        GoalTouchedRequest goalTouchedRequest = (GoalTouchedRequest) o;
        return Objects.equals(winner, goalTouchedRequest.winner);
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
