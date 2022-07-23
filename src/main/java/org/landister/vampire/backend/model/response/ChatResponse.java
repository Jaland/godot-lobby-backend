package org.landister.vampire.backend.model.response;

import java.util.Objects;

public class ChatResponse extends BaseResponse {

  public String message;

  public ChatResponse() {
    type = "chat";
  }

  public ChatResponse(String message) {
    type = "chat";
    this.message = message;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ChatResponse message(String message) {
    setMessage(message);
    return this;
  }

  @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ChatResponse)) {
            return false;
        }
        ChatResponse chatResponse = (ChatResponse) o;
        return Objects.equals(message, chatResponse.message);
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
