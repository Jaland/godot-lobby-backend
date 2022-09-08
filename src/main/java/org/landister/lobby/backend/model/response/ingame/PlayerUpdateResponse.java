package org.landister.lobby.backend.model.response.ingame;

import java.util.Objects;

import org.landister.lobby.backend.model.response.BaseResponse;
import org.landister.lobby.backend.model.shared.Vector2;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerUpdateResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "player_update";
  String username;
  Vector2 position, velocity;

  public PlayerUpdateResponse() {
    type = RESPONSE_TYPE;
  }


  public PlayerUpdateResponse(String username, Vector2 position, Vector2 velocity) {
    this.username = username;
    this.position = position;
    this.velocity = velocity;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Vector2 getPosition() {
    return this.position;
  }

  public void setPosition(Vector2 position) {
    this.position = position;
  }

  public Vector2 getVelocity() {
    return this.velocity;
  }

  public void setVelocity(Vector2 velocity) {
    this.velocity = velocity;
  }

  public PlayerUpdateResponse username(String username) {
    setUsername(username);
    return this;
  }

  public PlayerUpdateResponse position(Vector2 position) {
    setPosition(position);
    return this;
  }

  public PlayerUpdateResponse velocity(Vector2 velocity) {
    setVelocity(velocity);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PlayerUpdateResponse)) {
            return false;
        }
        PlayerUpdateResponse playerUpdateResponse = (PlayerUpdateResponse) o;
        return Objects.equals(username, playerUpdateResponse.username) && Objects.equals(position, playerUpdateResponse.position) && Objects.equals(velocity, playerUpdateResponse.velocity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, position, velocity);
  }

  @Override
  public String toString() {
    return "{" +
      " username='" + getUsername() + "'" +
      ", position='" + getPosition() + "'" +
      ", velocity='" + getVelocity() + "'" +
      "}";
  }

}