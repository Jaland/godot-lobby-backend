package org.landister.vampire.backend.services.lobby;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.landister.vampire.backend.model.dao.game.Game;
import org.landister.vampire.backend.model.enums.GameState;
import org.landister.vampire.backend.model.dao.game.inner.User;
import org.landister.vampire.backend.model.request.lobby.CreateGameRequest;
import org.landister.vampire.backend.model.session.UserSession;
import org.landister.vampire.backend.services.GameService;
import org.landister.vampire.backend.util.exceptions.GameException;
import org.landister.vampire.backend.util.exceptions.NotFoundException;

/**
 * Used for retrieving lobby specific information
 */
@ApplicationScoped
public class LobbyGameService extends GameService {


  private static final Logger LOG = Logger.getLogger(LobbyGameService.class);

  public Game createGame(UserSession user, CreateGameRequest request) {
		Game game = new Game();
		game.host(user.getUsername())
      .users(List.of(new User(user.getUsername())))
      .state(GameState.WAITING)
      .name(request.getName());
		game.persist();
		return game;
	}

	public Game joinGame(UserSession user, String gameId) {
		Game game = Game.findById(new ObjectId(gameId));
    if (game.getUsers().stream().map(User::getName).anyMatch(user.getUsername()::equals)) {
      LOG.info("User " + user.getUsername() + " is already in game " + gameId);
      return game;
    }
		game.getUsers().add(new User(user.getUsername()));
		game.update();
		return game;
	}

  public Game startGame(String gameId, String username) throws GameException {
    Game game= getGame(gameId);
    validateGameReadyToStart(game);
    game.state(GameState.LOADING);
    game.update();
    return game;
  }

  /**
   * Validates that a game is ready to start, returns a string with the error if it is not ready or null if it is ready
   * 
   * @param game
   * @return
   */
  private void validateGameReadyToStart(Game game) throws GameException {
    if(game == null) {
      throw new GameException("Game not found");
    }
    List<User> users = game.getUsers();
    if(game.getUsers().size()<game.getMinPlayers()) {
      throw new GameException( "Not enough players to start game");
    } else if(game.getUsers().size()>game.getMaxPlayers()) {
      throw new GameException( "Too many players to start game");
    }
    // Not the best way to do this validation, but it works for now
    if(users.size() != cacheService.getConnectedUserSessionsFromGame(game.getIdHexString()).count()) {
      throw new GameException( "Not all users are connected");
    }

    if(game.getState() != GameState.WAITING) {
      throw new GameException( "Game not in lobby state");
    }

  }
}
