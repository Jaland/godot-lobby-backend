package org.landister.lobby.backend.model.dao.game.inner;

import java.util.Objects;

import org.landister.lobby.backend.model.shared.Vector2;

/**
 * This class is a model class for the in-game representation of a User.
 * 
 * @author Landister
 */
public class User {

  private String name;
  private Vector2 spawnPosition;


  public User() {
  }

  public User(String name, Vector2 spawnPosition) {
    this.name = name;
    this.spawnPosition = spawnPosition;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Vector2 getSpawnPosition() {
    return this.spawnPosition;
  }

  public void setSpawnPosition(Vector2 spawnPosition) {
    this.spawnPosition = spawnPosition;
  }

  public User name(String name) {
    setName(name);
    return this;
  }

  public User spawnPosition(Vector2 spawnPosition) {
    setSpawnPosition(spawnPosition);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(spawnPosition, user.spawnPosition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, spawnPosition);
  }

  @Override
  public String toString() {
    return "{" +
      " name='" + getName() + "'" +
      ", spawnPosition='" + getSpawnPosition() + "'" +
      "}";
  }

  
}
