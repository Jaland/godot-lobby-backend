package org.landister.lobby.backend.model.response.ingame;

import org.landister.lobby.backend.model.response.GameResponse;

public class LoadAssetsResponse extends GameResponse {

  final static String RESPONSE_TYPE = "load_assets";

  public LoadAssetsResponse() {
    type = RESPONSE_TYPE;
  }
}
