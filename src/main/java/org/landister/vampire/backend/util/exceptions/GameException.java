package org.landister.vampire.backend.util.exceptions;

public class GameException extends RuntimeException{

  /**
   * Message that should be reported back to the
   */
  String messageToClient;

  public GameException(String message) {
    super(message);
    this.messageToClient = message;
  }

  public GameException(String message, String userMessage) {
    super(message);
    this.messageToClient = userMessage;
  }

  public String getMessageToClient() {
    return messageToClient;
  }
  
}
