package org.landister.lobby.backend.model.request.lobby;

import java.util.Objects;

import org.landister.lobby.backend.model.request.BaseRequest;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class LeaveGameRequest extends BaseRequest {

  public LeaveGameRequest() {
  }

 }
