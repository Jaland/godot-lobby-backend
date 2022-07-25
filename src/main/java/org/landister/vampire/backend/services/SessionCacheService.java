package org.landister.vampire.backend.services;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;

import org.jboss.logging.Logger;
import org.landister.vampire.backend.model.session.UserSession;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is a service that caches the session of a user.
 */
@ApplicationScoped
public class SessionCacheService {

  public static final String GLOBAL_GAME_ID = "0";

  // TODO: Figure out how to cache this map
  // Game Id -> Session Id -> User Information
  // Also this was public originally but if you try to access the variable directly it is empty for some reason
  private Map<String, Map<String, UserSession>> sessions = new ConcurrentHashMap<>();

  ObjectMapper mapper = new ObjectMapper();

  private static final Logger LOG = Logger.getLogger(SessionCacheService.class);
  
  public UserSession removeAllSessions(String sessionId) {
        UserSession userSession = null;
        for (Map<String, UserSession> gameSessions : sessions.values()) {
            UserSession userSessionFromGame = gameSessions.remove(sessionId);
            userSession = userSessionFromGame != null ? userSessionFromGame : userSession;
        }
        return userSession;
    }

  public UserSession removeSession(String gameId, String sessionId) {
      Map<String, UserSession> gameSessions = sessions.get(gameId);
      if (gameSessions == null) {
          return null;
      }
      return gameSessions.remove(sessionId);
  }

  public UserSession getUserSession(String gameId, String sessionId){
      Map<String, UserSession> gameSessions = sessions.get(gameId);
      if (gameSessions == null) {
          LOG.error("Game not found: " + sessionId);
          throw new RuntimeException("Game not found");
      }
      UserSession userSession = gameSessions.get(sessionId);
      if (userSession == null) {
          LOG.error("Session not found in game: " + sessionId);
          throw new RuntimeException("Session not found");
      }
      return userSession;
  }

  public Map<String, UserSession> getGameSessions(String gameId) {
      return sessions.get(gameId);
  }

  public void putGameSession(String gameId, String sessionId, UserSession userSession) {
      Map<String, UserSession> gameSessions = sessions.get(gameId);
      if (gameSessions == null) {
          gameSessions = new ConcurrentHashMap<>();
          sessions.put(gameId, gameSessions);
      }
      gameSessions.put(sessionId, userSession);
  }

  public void addToSession(String gameId, String sessionId, UserSession userSession) {
      Map<String, UserSession> gameSessions = sessions.get(gameId);
      if (gameSessions == null) {
          gameSessions = new ConcurrentHashMap<>();
          sessions.put(gameId, gameSessions);
      }
      gameSessions.put(sessionId, userSession);
  }

  public void closeSession(Session session, String reason) {
    removeAllSessions(session.getId());
    try {
        session.close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION, reason));
    } catch (IOException e) {
        LOG.error("Error closing session: {}\n {}", session.getId(), e);
        throw new RuntimeException(e);
    }
}


}
