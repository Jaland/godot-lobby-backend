package org.landister.lobby.backend.model.request.ingame;

import java.util.Objects;

import org.landister.lobby.backend.model.request.BaseRequest;
import org.landister.lobby.backend.model.shared.Vector2;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class PlayerUpdateRequest extends BaseRequest {

  String username;
  Vector2 position, velocity;
  
  public PlayerUpdateRequest() {
  }

  public PlayerUpdateRequest(String username, Vector2 position, Vector2 velocity) {
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

  public PlayerUpdateRequest username(String username) {
    setUsername(username);
    return this;
  }

  public PlayerUpdateRequest position(Vector2 position) {
    setPosition(position);
    return this;
  }

  public PlayerUpdateRequest velocity(Vector2 velocity) {
    setVelocity(velocity);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PlayerUpdateRequest)) {
            return false;
        }
        PlayerUpdateRequest playerUpdateRequest = (PlayerUpdateRequest) o;
        return Objects.equals(username, playerUpdateRequest.username) && Objects.equals(position, playerUpdateRequest.position) && Objects.equals(velocity, playerUpdateRequest.velocity);
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