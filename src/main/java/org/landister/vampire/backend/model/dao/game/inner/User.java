package org.landister.vampire.backend.model.dao.game.inner;

import java.util.Objects;

/**
 * This class is a model class for the in-game representation of a User.
 * 
 * @author Landister
 */
public class User {

  private String name;


  public User() {
  }

  public User(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public User name(String name) {
    setName(name);
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
        return Objects.equals(name, user.name);
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
