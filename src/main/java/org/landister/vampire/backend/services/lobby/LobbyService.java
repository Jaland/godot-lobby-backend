package org.landister.vampire.backend.services.lobby;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.bson.types.ObjectId;
import org.landister.vampire.backend.model.game.Game;
import org.landister.vampire.backend.model.session.UserSession;

@ApplicationScoped
public class LobbyService {

	public Game createGame(UserSession user) {
		Game game = new Game();
		game.setOwner(user.getUsername());
		game.setUsers(List.of(user.getUsername()));
		game.persist();
		return game;
	}

	public Game joinGame(UserSession user, String gameId) {
		Game game = Game.findById(new ObjectId(gameId));
		game.getUsers().add(user.getUsername());
		game.update();
		return game;
	}
}
