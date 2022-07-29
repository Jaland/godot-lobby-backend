package org.landister.vampire.backend.mapper;

import org.landister.vampire.backend.model.game.Game;
import org.landister.vampire.backend.model.response.GameResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface GameMapper {
 
  @Mapping(source = "idHexString", target = "id")
  GameResponse toGameResponse(Game game);
  
}
