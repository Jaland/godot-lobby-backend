package org.landister.vampire.backend.model.request.lobby;

import org.landister.vampire.backend.model.request.UserRequest;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class LobbyRefreshRequest extends UserRequest {  
  
}
