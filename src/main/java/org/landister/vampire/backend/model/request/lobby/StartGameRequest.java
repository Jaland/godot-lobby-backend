package org.landister.vampire.backend.model.request.lobby;

import org.landister.vampire.backend.model.request.BaseRequest;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class StartGameRequest extends BaseRequest {

  public StartGameRequest() {
  }
 }
