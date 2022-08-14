package org.landister.vampire.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.landister.vampire.backend.model.game.Game;
import org.landister.vampire.backend.model.response.GameResponse;
import org.landister.vampire.backend.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface GameMapper {
 
  @Mapping(source = "idHexString", target = "id")
  @Mapping(expression =  "java(getUserResponse(game))", target = "users")
  GameResponse toGameResponse(Game game);
  

  default List<UserResponse> getUserResponse(Game game) {
    return game.getUsers().stream().map(user -> new UserResponse().username(user)).collect(Collectors.toList());
  }
}
