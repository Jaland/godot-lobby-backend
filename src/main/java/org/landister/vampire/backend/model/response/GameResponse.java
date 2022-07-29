package org.landister.vampire.backend.model.response;

import java.util.List;
import java.util.Objects;

public class GameResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "game";

  String id;
  String name;
  List<String> users;
  

  public GameResponse() {
    type=RESPONSE_TYPE;
  }
  

  public GameResponse(String id, String name, List<String> users) {
    this.id = id;
    this.name = name;
    this.users = users;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getUsers() {
    return this.users;
  }

  public void setUsers(List<String> users) {
    this.users = users;
  }

  public GameResponse id(String id) {
    setId(id);
    return this;
  }

  public GameResponse name(String name) {
    setName(name);
    return this;
  }

  public GameResponse users(List<String> users) {
    setUsers(users);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof GameResponse)) {
            return false;
        }
        GameResponse gameResponse = (GameResponse) o;
        return Objects.equals(id, gameResponse.id) && Objects.equals(name, gameResponse.name) && Objects.equals(users, gameResponse.users);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, users);
  }

  @Override
  public String toString() {
    return "{" +
      " id='" + getId() + "'" +
      ", name='" + getName() + "'" +
      ", users='" + getUsers() + "'" +
      "}";
  }

}
