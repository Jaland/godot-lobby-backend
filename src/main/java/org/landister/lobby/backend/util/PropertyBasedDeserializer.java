package org.landister.lobby.backend.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.landister.lobby.backend.model.request.BaseRequest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class PropertyBasedDeserializer<T> extends StdDeserializer<BaseRequest> {

  public PropertyBasedDeserializer() {
    super(BaseRequest.class);
  }

  private Map<String, Class<? extends BaseRequest>> deserializationClasses;

  public PropertyBasedDeserializer(Class<BaseRequest> baseClass) {
      super(baseClass);
      deserializationClasses = new HashMap<String, Class<? extends BaseRequest>>();
  }

  public void register(String property, Class<? extends BaseRequest> deserializationClass) {
      deserializationClasses.put(property, deserializationClass);
  }

  @Override
  public BaseRequest deserialize(JsonParser p, DeserializationContext ctxt)
          throws IOException, JsonProcessingException {

      ObjectMapper mapper = (ObjectMapper) p.getCodec();
      JsonNode tree = mapper.readTree(p);
      
      Class<? extends BaseRequest> deserializationClass = findDeserializationClass(tree);
      if (deserializationClass == null) {
          throw JsonMappingException.from(ctxt, 
             "No registered unique properties found for polymorphic deserialization");
      }

      return mapper.treeToValue(tree, deserializationClass);
  }
  
  private Class<? extends BaseRequest> findDeserializationClass(JsonNode tree) {
    JsonNode requestTypeNode = tree.get("requestType");
    if(requestTypeNode == null || requestTypeNode.asText().isEmpty())
        throw new IllegalArgumentException("No requestType found in request");

    BaseRequest.RequestType requestType = BaseRequest.RequestType.valueOf(requestTypeNode.asText());
    
    if(requestType.getRequestClass() == null) 
        throw new IllegalArgumentException("No deserialization class found for requestType " + requestType);

    return requestType.getRequestClass();
  }
}
