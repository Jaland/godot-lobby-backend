package org.landister.lobby.backend.model.response.lobby;

import org.landister.lobby.backend.model.response.GameResponse;

public class StartGameResponse extends GameResponse {

  final static String RESPONSE_TYPE = "start_game";

  public StartGameResponse() {
    type = RESPONSE_TYPE;
  }
}
