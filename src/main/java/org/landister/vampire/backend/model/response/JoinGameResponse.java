package org.landister.vampire.backend.model.response;

import java.util.Objects;

public class JoinGameResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "join_game";

  String id;
  String name;
  

  public JoinGameResponse() {
    type=RESPONSE_TYPE;
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

  public JoinGameResponse name(String name) {
    setName(name);
    return this;
  }

  public JoinGameResponse id(String id) {
    setId(id);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof JoinGameResponse)) {
            return false;
        }
        JoinGameResponse gameResponse = (JoinGameResponse) o;
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
