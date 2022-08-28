package org.landister.vampire.backend.mapper;

import java.util.Arrays;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.landister.vampire.backend.model.dao.game.Game;
import org.landister.vampire.backend.model.response.GameResponse;

import org.landister.vampire.backend.model.dao.game.inner.User;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class GameMapperTest {

  @Inject
  GameMapper mapper;

  @Test
  public void testGameMapper() {
    Game game = new Game();
    game.setId(new ObjectId());
    game.setName("test");
    game.setUsers(Arrays.asList(new User("User1", null), new User("User2", null), new User("User3", null)));
    GameResponse response = mapper.toGameResponse(game);
    assert(response.getUsers().size() == 3);
    assert(response.getUsers().get(0).getUsername().equals("User1"));
  }
  
}
