package org.landister.vampire.backend.model.game;

import java.util.List;
import java.util.Objects;

import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

/**
 * Saved Game State
 * @author Landister
 */

@MongoEntity(collection = "games", database = "vampire")
public class Game extends PanacheMongoEntity{

  ObjectId id;
  String name;
  boolean started = false;
  List<String> users;;


  public String getIdHexString(){
    return id.toHexString();
  }

  // AutoGenerated


  public Game() {
  }

  public Game(ObjectId id, String name, boolean started, List<String> users) {
    this.id = id;
    this.name = name;
    this.started = started;
    this.users = users;
  }

  public ObjectId getId() {
    return this.id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isStarted() {
    return this.started;
  }

  public boolean getStarted() {
    return this.started;
  }

  public void setStarted(boolean started) {
    this.started = started;
  }

  public List<String> getUsers() {
    return this.users;
  }

  public void setUsers(List<String> users) {
    this.users = users;
  }

  public Game id(ObjectId id) {
    setId(id);
    return this;
  }

  public Game name(String name) {
    setName(name);
    return this;
  }

  public Game started(boolean started) {
    setStarted(started);
    return this;
  }

  public Game users(List<String> users) {
    setUsers(users);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Game)) {
            return false;
        }
        Game game = (Game) o;
        return Objects.equals(id, game.id) && Objects.equals(name, game.name) && started == game.started && Objects.equals(users, game.users);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, started, users);
  }

  @Override
  public String toString() {
    return "{" +
      " id='" + getId() + "'" +
      ", name='" + getName() + "'" +
      ", started='" + isStarted() + "'" +
      ", users='" + getUsers() + "'" +
      "}";
  }

  

}
