package org.landister.vampire.backend.model.enums;

public enum GameState {
  

  WAITING(true,false,false,false),
  LOADING(false,true,false, false),
  LOADED(false,true,false, false),
  STARTED(false,true,true, false),
  FINISHED(false,false,false, true),
  ABORTED(false,false,false, true),
  ERROR(false,false,false, true),
  ;

  // Using these as more common states we care about to avoid having to check for each one every time
  boolean inLobby = false;
  boolean isLoading = false;
  boolean isCurrentlyPlaying = false;
  boolean isCompleted = false;

  private GameState(Boolean inLobby, Boolean isLoading, Boolean isCurrentlyPlaying, boolean isCompleted) {
    this.inLobby = inLobby;
    this.isLoading = isLoading;
    this.isCurrentlyPlaying = isCurrentlyPlaying;
    this.isCompleted = isCompleted;
  }

}
