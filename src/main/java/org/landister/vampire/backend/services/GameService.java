package org.landister.vampire.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.landister.vampire.backend.model.game.Game;
import org.landister.vampire.backend.model.request.lobby.CreateGameRequest;
import org.landister.vampire.backend.model.session.UserSession;
import org.landister.vampire.backend.util.exceptions.NotFoundException;

/**
 * Used for interacting with Saved Game Information
 */
@ApplicationScoped
public class GameService {
  
  @Inject
  SessionCacheService cacheService;

  private static final Logger LOG = Logger.getLogger(GameService.class);

  public Game createGame(UserSession user, CreateGameRequest request) {
		Game game = new Game();
		game.setOwner(user.getUsername());
		game.setUsers(List.of(user.getUsername()));
    game.setName(request.getName());
		game.persist();
		return game;
	}

	public Game joinGame(UserSession user, String gameId) {
		Game game = Game.findById(new ObjectId(gameId));
		game.getUsers().add(user.getUsername());
		game.update();
		return game;
	}
  
  public Game leaveGame(String gameId, String userName) {
    // Remove the user from the game cache
    try {
      UserSession userSession = cacheService.removeUser(gameId, userName);
    } catch (NotFoundException e) {
      LOG.info("User not found in cache when removing from game for, gameId=" + gameId + ", userName=" + userName);
    }
    // 
    if(Objects.equals(gameId, SessionCacheService.GLOBAL_GAME_ID)){
      return null;
    }
    Game game=Game.findById(new ObjectId(gameId));
    if(game==null){
      throw new NotFoundException("Game not found in database for id=" + gameId);
    }
    // Remove users from the game (this will remove all copies of the user from the game)
    game.users(game.getUsers().stream()
        .filter(user -> !Objects.equals(user, userName))
        .collect(Collectors.toList()));
    if(game.getUsers().isEmpty()) {
        //If the users in our game is empty, we delete the game
        game.delete();
    } else {
        game.update();
    }
    return game;
  }

}
