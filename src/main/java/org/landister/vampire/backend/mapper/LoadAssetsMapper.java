package org.landister.vampire.backend.mapper;

import org.landister.vampire.backend.model.request.ingame.LoadAssetsRequest;
import org.landister.vampire.backend.model.response.ingame.MapSetupResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface LoadAssetsMapper {
 
  MapSetupResponse toStartGame(LoadAssetsRequest request);


}
