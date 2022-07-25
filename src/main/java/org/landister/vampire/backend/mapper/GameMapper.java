package org.landister.vampire.backend.mapper;

import org.landister.vampire.backend.model.game.Game;
import org.landister.vampire.backend.model.response.JoinGameResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface GameMapper {
 
  @Mapping(source = "idHexString", target = "id")
  JoinGameResponse toGameResponse(Game game);
  
}
