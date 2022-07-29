package org.landister.vampire.backend.model.request.lobby;

import java.util.Objects;

import org.landister.vampire.backend.model.request.BaseRequest;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class CreateGameRequest extends BaseRequest {  

  String name;


  public CreateGameRequest() {
  }

  public CreateGameRequest(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CreateGameRequest name(String name) {
    setName(name);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CreateGameRequest)) {
            return false;
        }
        CreateGameRequest createGameRequest = (CreateGameRequest) o;
        return Objects.equals(name, createGameRequest.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }

  @Override
  public String toString() {
    return "{" +
      " name='" + getName() + "'" +
      "}";
  }

  
}
