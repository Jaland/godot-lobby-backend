package org.landister.lobby.backend.model.request.ingame;

import org.landister.lobby.backend.model.request.BaseRequest;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class StartGameRequest extends BaseRequest {

}
