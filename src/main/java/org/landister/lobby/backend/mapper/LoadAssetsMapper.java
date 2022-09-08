package org.landister.lobby.backend.mapper;

import org.landister.lobby.backend.model.request.ingame.LoadAssetsRequest;
import org.landister.lobby.backend.model.response.ingame.MapSetupResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface LoadAssetsMapper {
 
  MapSetupResponse toStartGame(LoadAssetsRequest request);


}
