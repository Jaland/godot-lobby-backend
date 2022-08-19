package org.landister.vampire.backend.model.response.ingame;

import org.landister.vampire.backend.model.response.GameResponse;

public class LoadAssetsResponse extends GameResponse {

  final static String RESPONSE_TYPE = "load_assets";

  public LoadAssetsResponse() {
    type = RESPONSE_TYPE;
  }
}
