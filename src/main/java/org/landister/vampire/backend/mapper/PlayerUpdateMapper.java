package org.landister.vampire.backend.mapper;

import org.landister.vampire.backend.model.request.ingame.PlayerUpdateRequest;
import org.landister.vampire.backend.model.response.ingame.PlayerUpdateResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface PlayerUpdateMapper {
 
  PlayerUpdateResponse toResponse(PlayerUpdateRequest request);


}
