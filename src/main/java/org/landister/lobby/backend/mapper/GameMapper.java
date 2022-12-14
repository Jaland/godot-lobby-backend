package org.landister.lobby.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.landister.lobby.backend.model.dao.game.Game;
import org.landister.lobby.backend.model.response.GameResponse;
import org.landister.lobby.backend.model.response.UserResponse;
import org.landister.lobby.backend.model.response.ingame.LoadAssetsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface GameMapper {
 
  @Mapping(source = "idHexString", target = "id")
  @Mapping(expression =  "java(getUserResponse(game))", target = "users")
  GameResponse toGameResponse(Game game);


  @Mapping(source = "idHexString", target = "id")
  @Mapping(expression =  "java(getUserResponse(game))", target = "users")
  LoadAssetsResponse toLoadAssetsResponse(Game game);
  

  default List<UserResponse> getUserResponse(Game game) {
    return game.getUsers().stream().map(user -> new UserResponse().username(user.getName())).collect(Collectors.toList());
  }
}
