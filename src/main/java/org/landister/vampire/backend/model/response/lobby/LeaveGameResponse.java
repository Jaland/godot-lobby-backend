package org.landister.vampire.backend.model.response.lobby;

import org.landister.vampire.backend.model.response.BaseResponse;

public class LeaveGameResponse extends BaseResponse {

  final static String RESPONSE_TYPE = "leave_game";

  public LeaveGameResponse() {
    type = RESPONSE_TYPE;
  }  

}
