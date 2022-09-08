package org.landister.lobby.backend.services;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;

import org.jboss.logging.Logger;
import org.landister.lobby.backend.model.dao.game.Game;
import org.landister.lobby.backend.model.session.UserSession;
import org.landister.lobby.backend.util.exceptions.NotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is a service that caches the session of a user.
 */
@ApplicationScoped
public class SessionCacheService {

    @Inject
    BaseGameService gameService;

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
     * Note: gameToSessionMap should be primary way to find a session, this is used for speed when we don't have the game id (i.e. throwing errors)
     */
    private Map<String, UserSession> sessionToUserSessionMap = new ConcurrentHashMap<>();

    ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOG = Logger.getLogger(SessionCacheService.class);

    /**
     * Adds user to the session cache of a specific game.
     * 
     * 
     * @param gameId
     * @param userSession
     *
     * @throws RuntimeException This method will automatically close the existing websocket session if the user is already in the game and use the newly supplied session to prevent issues with multiple sessions for a single user. If the user tries to double connect with the same session somehow it means there is probably an error in the code and the user will be kicked and this error will be thrown
     */
    public void addToGamesCache(String gameId, UserSession userSession) {
        LOG.debug("Adding user to session cache, gameId=" + gameId + ", userSession=" + userSession);
        // Get the game to add the user to
        Map<String, UserSession> gameSessions = getGameSessions(gameId);
        if(gameSessions==null){
            gameSessions = new ConcurrentHashMap<>();
            gameToSessionsMap.put(gameId, gameSessions);
        }
        // Validate the user is not already in the game
        if (gameSessions.containsKey(userSession.getUsername())) {
            LOG.info("User already in session cache, gameId=" + gameId + ", userSession=" + userSession + " Replacing old session");
            UserSession existingSession = gameSessions.get(userSession.getUsername());
            try {
                existingSession.getSession().close(new CloseReason(CloseCodes.VIOLATED_POLICY, "User trying to join a game they are already in"));
            } catch (IOException e) {
                LOG.error("Error closing session", e);
            }
            gameSessions.remove(userSession.getUsername());
            if(userSession.getSession().getId().equals(existingSession.getSession().getId())){
                throw new RuntimeException("User attempted to double join a game using the same websocket session");
            }
        }
        // Important: Make sure to add the user to the game session map AND session to user session map so we can find the user session on error
        gameSessions.put(userSession.getUsername(), userSession);
        sessionToUserSessionMap.put(userSession.getSession().getId(), userSession);
    }

    public UserSession removeFromLobbyCache(String username) {
        return removeFromGamesCache(GLOBAL_GAME_ID, username);
    }


    /**
     * Removes user from the session cache of a specific game. (Do not close session)
     * 
     * @param gameId
     * @param username
     * @return
     */
    public UserSession removeFromGamesCache(String gameId, String username) {
        return removeFromGamesCache(gameId, username, false);
    }


    /**
     * Removes user from the session cache of a specific game.
     * 
     * @param gameId
     * @param username
     * @return
     */
    public UserSession removeFromGamesCache(String gameId, String username, boolean closeSession) {
        LOG.debug("Removing user from session cache, gameId=" + gameId + ", username=" + username);
        Map<String, UserSession> gameSession = getGameSessions(gameId);
        if(gameSession==null){
          LOG.info("Game not found in cache for id=" + gameId);
          return null;
        }
        UserSession userSession = gameSession.remove(username);
        if(userSession != null) {
            sessionToUserSessionMap.remove(userSession.getSession().getId());
            if(closeSession){
                try {
                    userSession.getSession().close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION, "User being removed from cache"));
                } catch (IOException e) {
                    LOG.error("Error closing session", e);
                }
            }
        } else {
            LOG.info("User not found in session cache, gameId=" + gameId + ", username=" + username);
        }
        return userSession;
    }

    /**
     * Removes user from the session cache of all games (slower than removeSession).
     * 
     * @param sessionId
     * @return
     */
    public UserSession removeUserFromAllGamesCache(String username) {
        LOG.debug("Removing user from all games: " + username);
        UserSession userSession = null;
        for (String gameId : gameToSessionsMap.keySet()) {
            UserSession userSessionFromGame = removeFromGamesCache(gameId, username, true);
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
        Map<String, UserSession> gameSessions = getGameSessions(gameId);
        if (gameSessions == null) {
            LOG.error("Game not found in cache: " + gameId);
            return null;
        }
        UserSession userSession = gameSessions.get(username);
        if (userSession == null) {
            LOG.error("Session not found in game for user: " + username + " in game: " + gameId);
            return null;
        }
        return userSession;
    }


    /**
     * Gets user session from the session cache
     * 
     * @param gameId
     * @param sessionId
     * @return
     */
    public UserSession getUserSessionFromUsername(String username) {
        for (String gameId : gameToSessionsMap.keySet()) {
            UserSession userSessionFromGame = gameToSessionsMap.get(gameId).get(username);
            if(userSessionFromGame != null) {
                return userSessionFromGame;
            }
        }
        return null;
    }

    /**
     * Gets user session from the session cache based on session
     * 
     * @param sessionId
     * @return
     */
    public UserSession getUserSessionFromSessionId(String sessionId) {
        return sessionToUserSessionMap.get(sessionId);
    }

    /**
     * Gets all users with valid open sessions in a game.
     * Note: the reverse could be tricky without going to the database since the user may not be in the cache.
     * 
     * @param gameId
     * @return
     */
    public Stream<UserSession> getConnectedUserSessionsFromGame(String gameId) {
        return getUsersSessionsFromGame(gameId).filter(userSesssion -> userSesssion.getSession().isOpen());
    }


    public Stream<UserSession> getUsersSessionsFromGame(String gameId) {
        Map<String, UserSession> gameSessions = getGameSessions(gameId);
        if (gameSessions == null) {
            LOG.error("Game not found in cache: " + gameId);
            throw new NotFoundException("Game not found");
        }
        return gameSessions.values().stream();
    }

    /**
     * Gets game session information from the session cache, or from db if unable to find it is in the cache.
     * 
     * @param gameId
     * @return
     */
    public Map<String, UserSession> getGameSessions(String gameId) {
        Map<String, UserSession> gameSessions = gameToSessionsMap.get(gameId);
        // If the game is not found in cache check if it is a global game or in the database
        if (gameSessions == null) {
            // If the game is the global game then add to cache and return empty map
            if(gameId.equals(GLOBAL_GAME_ID)) {
                gameToSessionsMap.put(GLOBAL_GAME_ID, new ConcurrentHashMap<>());
                return gameToSessionsMap.get(GLOBAL_GAME_ID);
            }
            // If game is not found in session check if it is in the database
            Game game = gameService.getGame(gameId);
            if(game != null) {
                // If game is in the database then add to cache and return empty map
                gameToSessionsMap.put(gameId, new ConcurrentHashMap<>());
                return gameToSessionsMap.get(gameId);
            }
        }
        // If the game is found in cache return it (otherwise it is null)
        return gameSessions;
    }
    
    public void changeGames(String oldGameId, String newGameId, UserSession userSession) {
        removeFromGamesCache(oldGameId, userSession.getUsername());
        addToGamesCache(newGameId, userSession);
    }


}
