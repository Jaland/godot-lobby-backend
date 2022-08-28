package org.landister.vampire.backend.model.shared;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Player {  

  // Current player position
  Vector2 position, velocity;
  String username;


  public Player() {
  }


  public Player(Vector2 position, Vector2 velocity, String username) {
    this.position = position;
    this.velocity = velocity;
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

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Player position(Vector2 position) {
    setPosition(position);
    return this;
  }

  public Player velocity(Vector2 velocity) {
    setVelocity(velocity);
    return this;
  }

  public Player username(String username) {
    setUsername(username);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Player)) {
            return false;
        }
        Player player = (Player) o;
        return Objects.equals(position, player.position) && Objects.equals(velocity, player.velocity) && Objects.equals(username, player.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(position, velocity, username);
  }

  @Override
  public String toString() {
    return "{" +
      " position='" + getPosition() + "'" +
      ", velocity='" + getVelocity() + "'" +
      ", username='" + getUsername() + "'" +
      "}";
  }

}
