package org.landister.vampire.backend.services;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.jboss.logging.Logger;
import org.landister.vampire.backend.model.game.Game;
import org.landister.vampire.backend.model.session.UserSession;
import org.landister.vampire.backend.util.exceptions.NotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.User;

/**
 * This class is a service that caches the session of a user.
 */
@ApplicationScoped
public class SessionCacheService {

    @Inject
    GameService gameService;

    public static final String GLOBAL_GAME_ID = "0";

    // TODO: Figure out how to cache this map, maybe use redis (https://quarkus.io/guides/redis)? 

    // Also this was public originally but if you try to access the variables
    // directly it is empty for some reason

    /**
     * Used to keep track of all the user sessions for a particular game.
     * Game Id -> Username -> User Information
     */
    private Map<String, Map<String, UserSession>> gameToSessionsMap = new ConcurrentHashMap<>();

    /**
     * Used to allow us to find a user session based on the websocket session.
     */
    private Map<String, UserSession> sessionToUserSessionMap = new ConcurrentHashMap<>();

    ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOG = Logger.getLogger(SessionCacheService.class);

    /**
     * Adds user to the session cache of a specific game.
     * 
     * @param gameId
     * @param userSession
     */
    public void addToSession(String gameId, UserSession userSession) {
        Map<String, UserSession> gameSessions = gameToSessionsMap.get(gameId);
        if (gameSessions == null) {
            gameSessions = new ConcurrentHashMap<>();
            gameToSessionsMap.put(gameId, gameSessions);
        }
        gameSessions.put(userSession.getUsername(), userSession);
        sessionToUserSessionMap.put(userSession.getSession().getId(), userSession);
    }

    public UserSession removeUserFromLobby(String username) {
        return removeUser(GLOBAL_GAME_ID, username);
    }

    public UserSession removeUser(String gameId, String username) {
        Map<String, UserSession> gameSessions = gameToSessionsMap.get(gameId);
        if (gameSessions == null) {
            return null;
        }
        UserSession userSession = gameSessions.remove(username);
        if(userSession != null) {
            sessionToUserSessionMap.remove(userSession.getSession().getId());
        }
        return userSession;
    }

    /**
     * Removes user from the session cache of all games (slower than removeSession).
     * 
     * @param sessionId
     * @return
     */
    public UserSession removeUserFromAllGames(String username) {
        UserSession userSession = null;
        for (String gameId : gameToSessionsMap.keySet()) {
            UserSession userSessionFromGame = removeUser(gameId, username);
            userSession = userSessionFromGame != null ? userSessionFromGame : userSession;
        }
        return userSession;
    }

    /**
     * Gets user session from the session cache of a specific game.
     * 
     * @param gameId
     * @param sessionId
     * @return
     */
    public UserSession getUserSessionFromUsername(String gameId, String username) {
        Map<String, UserSession> gameSessions = gameToSessionsMap.get(gameId);
        if (gameSessions == null) {
            LOG.error("Game not found in cache: " + gameId);
            throw new NotFoundException("Game not found");
        }
        UserSession userSession = gameSessions.get(username);
        if (userSession == null) {
            LOG.error("Session not found in game for user: " + username);
            throw new NotFoundException("Session not found");
        }
        return userSession;
    }

    public void validateUserSession(Session session, UserSession userSession) {
        if (userSession.getSession() != session) {
            LOG.error("Session mismatch: " + session.getId() + " != " + userSession.getSession().getId());
            throw new AuthenticationFailedException("Session mismatch");
        }
    }

    /**
     * Gets user session from the session cache based on session
     * @param sessionId
     * @return
     */
    public UserSession getUserSessionFromSessionId(String sessionId) {
        return sessionToUserSessionMap.get(sessionId);
    }

    /**
     * Gets game session information from the session cache.
     * @param gameId
     * @return
     */
    public Map<String, UserSession> getGameSessions(String gameId) {
        return gameToSessionsMap.get(gameId);
    }
    
    public void changeGames(String oldGameId, String newGameId, UserSession userSession) {
        removeUser(oldGameId, userSession.getUsername());
        addToSession(newGameId, userSession);
    }


}
