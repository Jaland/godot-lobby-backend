package org.landister.vampire.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.landister.vampire.backend.mapper.AuthUserMapper;
import org.landister.vampire.backend.model.dao.game.Game;
import org.landister.vampire.backend.model.dao.game.inner.User;
import org.landister.vampire.backend.util.exceptions.NotFoundException;

/**
 * Basic Service used for interacting with Saved Game Information in the Database
 */
public class GameService {
  
  @Inject
  protected SessionCacheService cacheService;

  @Inject
  protected AuthUserMapper authUserMapper;

  private static final Logger LOG = Logger.getLogger(GameService.class);

  public Game getGame(String gameId) {
    return Game.findById(new ObjectId(gameId));
  }

  public List<Game> getGamesByUsername(String username) {
    return Game.find("users.name",username).list();
  }

  public boolean isHost(Game game, String username) {
    return game.getHost().equals(username);
  }


  public Game leaveGame(String gameId, String username) {
    // Remove the user from the game cache
    try {
      cacheService.removeFromGamesCache(gameId, username, false);
    } catch (NotFoundException e) {
      LOG.info("User not found in cache when removing from game for, gameId=" + gameId + ", userName=" + username);
    }
    if(Objects.equals(gameId, SessionCacheService.GLOBAL_GAME_ID)){
      return null;
    }
    Game game=Game.findById(new ObjectId(gameId));
    if(game==null){
      throw new NotFoundException("Game not found in database for id=" + gameId);
    }
    // Remove users from the game (this will remove all copies of the user from the game)
    List<User> users = game.getUsers().stream().filter(u -> !u.getName().equals(username)).collect(Collectors.toList());
    game.users(users);
    if(game.getUsers().isEmpty()) {
        //If the users in our game is empty, we delete the game
        game.delete();
    } else {
        game.update();
    }
    return game;
  }

}
