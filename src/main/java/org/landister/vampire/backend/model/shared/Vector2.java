package org.landister.vampire.backend.model.shared;

import java.util.Objects;

/**
 * Used to mock the Godot Vector2 class.
 *
 */
public class Vector2 {

  int x, y;
  

  public Vector2() {
  }

  public Vector2(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return this.x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return this.y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public Vector2 x(int x) {
    setX(x);
    return this;
  }

  public Vector2 y(int y) {
    setY(y);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Vector2)) {
            return false;
        }
        Vector2 vector2 = (Vector2) o;
        return x == vector2.x && y == vector2.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "{" +
      " x='" + getX() + "'" +
      ", y='" + getY() + "'" +
      "}";
  }

}
