package org.landister.vampire.backend.model.response.ingame.inner;

import java.util.Objects;

public class Player {  

  // Current player position
  int x,y;


  public Player() {
  }

  public Player(String username, int x, int y) {
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

  public Player x(int x) {
    setX(x);
    return this;
  }

  public Player y(int y) {
    setY(y);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Player)) {
            return false;
        }
        Player player = (Player) o;
        return x == player.x && y == player.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "{" +
      ", x='" + getX() + "'" +
      ", y='" + getY() + "'" +
      "}";
  }

  
}
