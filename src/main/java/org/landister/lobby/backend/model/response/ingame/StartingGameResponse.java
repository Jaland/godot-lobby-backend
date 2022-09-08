package org.landister.lobby.backend.model.response.ingame;

import org.landister.lobby.backend.model.response.BaseResponse;

public class StartingGameResponse extends BaseResponse {
  
  final static String RESPONSE_TYPE = "starting_game";

  public StartingGameResponse() {
    type = RESPONSE_TYPE;
  }
}
