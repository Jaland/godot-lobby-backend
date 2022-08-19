package org.landister.vampire.backend.model.response.utility;

import java.util.List;
import java.util.Objects;

import org.landister.vampire.backend.model.response.BaseResponse;

/**
 * Used to let the client know that the user was not found in the game and should clear the game information from cache
 */
public class UserNotFoundInGame extends BaseResponse {

  final static String RESPONSE_TYPE = "not_found_in_game";

  public UserNotFoundInGame() {
    type=RESPONSE_TYPE;
  } 

}
