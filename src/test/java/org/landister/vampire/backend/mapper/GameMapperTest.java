package org.landister.vampire.backend.mapper;

import java.util.Arrays;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.landister.vampire.backend.model.game.Game;
import org.landister.vampire.backend.model.response.GameResponse;

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
    game.setUsers(Arrays.asList("User1", "User2", "User3"));
    GameResponse response = mapper.toGameResponse(game);
    assert(response.getUsers().size() == 3);
    assert(response.getUsers().get(0).getUsername().equals("User1"));
  }
  
}
