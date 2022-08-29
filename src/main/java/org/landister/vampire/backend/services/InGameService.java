package org.landister.vampire.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.landister.vampire.backend.model.dao.game.Game;
import org.landister.vampire.backend.model.dao.game.inner.User;
import org.landister.vampire.backend.model.enums.GameState;
import org.landister.vampire.backend.util.exceptions.NotFoundException;

/**
 * Used for retrieving ingame specific information
 */
@ApplicationScoped
public class InGameService extends GameService {


  private static final Logger LOG = Logger.getLogger(InGameService.class);

  /**
   * Sets game state to started
   * @param game
   * @return
   */
  public Game startGame(Game game) {
    game.setState(GameState.STARTED);
    game.update();
    return game;

  }

  public Game resetGame(Game game) {
    //Remove position data from all users
    game.getUsers().forEach(user -> user.spawnPosition(null));
    game.state(GameState.LOADING);
    game.update();
    return game;
  }


}
