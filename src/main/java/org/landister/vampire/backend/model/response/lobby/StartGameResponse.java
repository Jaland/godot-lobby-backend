package org.landister.vampire.backend.model.response.lobby;

import org.landister.vampire.backend.model.response.GameResponse;

public class StartGameResponse extends GameResponse {

  final static String RESPONSE_TYPE = "start_game";

  public StartGameResponse() {
    type = RESPONSE_TYPE;
  }
}
