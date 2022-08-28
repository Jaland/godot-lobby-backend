package org.landister.vampire.backend.model.shared;

import java.util.Objects;

/**
 * This class is a model class for the in-game representation of a Goal information.
 * 
 * @author Landister
 */
public class Goal {

  private Vector2 position = new Vector2(0, 0);


  public Goal() {
  }

  public Goal(Vector2 position) {
    this.position = position;
  }

  public Vector2 getPosition() {
    return this.position;
  }

  public void setPosition(Vector2 position) {
    this.position = position;
  }

  public Goal position(Vector2 position) {
    setPosition(position);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Goal)) {
            return false;
        }
        Goal goal = (Goal) o;
        return Objects.equals(position, goal.position);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(position);
  }

  @Override
  public String toString() {
    return "{" +
      " position='" + getPosition() + "'" +
      "}";
  }
  
}
