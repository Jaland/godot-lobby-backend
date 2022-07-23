package org.landister.vampire.backend.model.response;

import java.util.Objects;

public class GameResponse extends BaseResponse {



  String id;
  String name;
  

  public GameResponse() {
    type="game";
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public GameResponse name(String name) {
    setName(name);
    return this;
  }

  public GameResponse id(String id) {
    setId(id);
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
        return Objects.equals(name, gameResponse.name) && Objects.equals(id, gameResponse.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, id);
  }

  @Override
  public String toString() {
    return "{" +
      " name='" + getName() + "'" +
      ", id='" + getId() + "'" +
      "}";
  }

}
