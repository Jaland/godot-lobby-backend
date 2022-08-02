package org.landister.vampire.backend.model.request;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize( using = JsonDeserializer.None.class )
public class ChatRequest extends BaseRequest {

  String message;

  
  public ChatRequest() {
  }

  public ChatRequest(String message) {
    this.message = message;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ChatRequest message(String message) {
    setMessage(message);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ChatRequest)) {
            return false;
        }
        ChatRequest chatRequest = (ChatRequest) o;
        return Objects.equals(message, chatRequest.message);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(message);
  }

  @Override
  public String toString() {
    return "{" +
      " message='" + getMessage() + "'" +
      "}";
  }

  
}
