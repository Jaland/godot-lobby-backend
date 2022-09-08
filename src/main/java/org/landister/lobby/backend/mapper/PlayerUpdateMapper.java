package org.landister.lobby.backend.mapper;

import org.landister.lobby.backend.model.request.ingame.PlayerUpdateRequest;
import org.landister.lobby.backend.model.response.ingame.PlayerUpdateResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface PlayerUpdateMapper {
 
  PlayerUpdateResponse toResponse(PlayerUpdateRequest request);


}
