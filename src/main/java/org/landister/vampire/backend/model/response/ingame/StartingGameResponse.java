package org.landister.vampire.backend.model.response.ingame;

import org.landister.vampire.backend.model.response.BaseResponse;

public class StartingGameResponse extends BaseResponse {
  
  final static String RESPONSE_TYPE = "starting_game";

  public StartingGameResponse() {
    type = RESPONSE_TYPE;
  }
}
