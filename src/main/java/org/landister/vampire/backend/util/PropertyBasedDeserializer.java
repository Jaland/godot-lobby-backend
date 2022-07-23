package org.landister.vampire.backend.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.landister.vampire.backend.model.request.ChatRequest;
import org.landister.vampire.backend.model.request.UserRequest;
import org.landister.vampire.backend.model.request.auth.LoginRequest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class PropertyBasedDeserializer<T> extends StdDeserializer<UserRequest> {

  public PropertyBasedDeserializer() {
    super(UserRequest.class);
  }

  private Map<String, Class<? extends UserRequest>> deserializationClasses;

  public PropertyBasedDeserializer(Class<UserRequest> baseClass) {
      super(baseClass);
      deserializationClasses = new HashMap<String, Class<? extends UserRequest>>();
  }

  public void register(String property, Class<? extends UserRequest> deserializationClass) {
      deserializationClasses.put(property, deserializationClass);
  }

  @Override
  public UserRequest deserialize(JsonParser p, DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

      ObjectMapper mapper = (ObjectMapper) p.getCodec();
      JsonNode tree = mapper.readTree(p);
      
      Class<? extends UserRequest> deserializationClass = findDeserializationClass(tree);
      if (deserializationClass == null) {
          throw JsonMappingException.from(ctxt, 
             "No registered unique properties found for polymorphic deserialization");
      }

      return mapper.treeToValue(tree, deserializationClass);
  }
  
  private Class<? extends UserRequest> findDeserializationClass(JsonNode tree) {
    JsonNode requestTypeNode = tree.get("requestType");
    if(requestTypeNode == null || requestTypeNode.asText().isEmpty())
        throw new IllegalArgumentException("No requestType found in request");

    UserRequest.RequestType requestType = UserRequest.RequestType.valueOf(requestTypeNode.asText());
    
    if(requestType.getRequestClass() == null) 
        throw new IllegalArgumentException("No deserialization class found for requestType " + requestType);

    return requestType.getRequestClass();
  }
}
