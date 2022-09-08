package org.landister.lobby.backend.model.dao.game;

import java.util.List;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.landister.lobby.backend.model.dao.game.inner.User;
import org.landister.lobby.backend.model.enums.GameState;
import org.landister.lobby.backend.model.shared.Goal;

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
  GameState state;
  // Default values for the game
  int minPlayers = 0, maxPlayers = 6;
  String host;

  List<User> users = List.of();

  Goal goal;


  public String getIdHexString(){
    return id.toHexString();
  }


  public Game() {
  }

  public Game(ObjectId id, String name, GameState state, int minPlayers, int maxPlayers, String host, List<User> users, Goal goal) {
    this.id = id;
    this.name = name;
    this.state = state;
    this.minPlayers = minPlayers;
    this.maxPlayers = maxPlayers;
    this.host = host;
    this.users = users;
    this.goal = goal;
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

  public GameState getState() {
    return this.state;
  }

  public void setState(GameState state) {
    this.state = state;
  }

  public int getMinPlayers() {
    return this.minPlayers;
  }

  public void setMinPlayers(int minPlayers) {
    this.minPlayers = minPlayers;
  }

  public int getMaxPlayers() {
    return this.maxPlayers;
  }

  public void setMaxPlayers(int maxPlayers) {
    this.maxPlayers = maxPlayers;
  }

  public String getHost() {
    return this.host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public List<User> getUsers() {
    return this.users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public Goal getGoal() {
    return this.goal;
  }

  public void setGoal(Goal goal) {
    this.goal = goal;
  }

  public Game id(ObjectId id) {
    setId(id);
    return this;
  }

  public Game name(String name) {
    setName(name);
    return this;
  }

  public Game state(GameState state) {
    setState(state);
    return this;
  }

  public Game minPlayers(int minPlayers) {
    setMinPlayers(minPlayers);
    return this;
  }

  public Game maxPlayers(int maxPlayers) {
    setMaxPlayers(maxPlayers);
    return this;
  }

  public Game host(String host) {
    setHost(host);
    return this;
  }

  public Game users(List<User> users) {
    setUsers(users);
    return this;
  }

  public Game goal(Goal goal) {
    setGoal(goal);
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
        return Objects.equals(id, game.id) && Objects.equals(name, game.name) && Objects.equals(state, game.state) && minPlayers == game.minPlayers && maxPlayers == game.maxPlayers && Objects.equals(host, game.host) && Objects.equals(users, game.users) && Objects.equals(goal, game.goal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, state, minPlayers, maxPlayers, host, users, goal);
  }

  @Override
  public String toString() {
    return "{" +
      " id='" + getId() + "'" +
      ", name='" + getName() + "'" +
      ", state='" + getState() + "'" +
      ", minPlayers='" + getMinPlayers() + "'" +
      ", maxPlayers='" + getMaxPlayers() + "'" +
      ", host='" + getHost() + "'" +
      ", users='" + getUsers() + "'" +
      ", goal='" + getGoal() + "'" +
      "}";
  }

}