package org.landister.vampire.backend.websocket.games;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;
import org.landister.vampire.backend.mapper.GameMapper;
import org.landister.vampire.backend.mapper.LoadAssetsMapper;
import org.landister.vampire.backend.model.dao.game.Game;
import org.landister.vampire.backend.model.enums.GameState;
import org.landister.vampire.backend.model.request.BaseRequest;
import org.landister.vampire.backend.model.request.auth.InitialRequest;
import org.landister.vampire.backend.model.request.ingame.LoadAssetsRequest;
import org.landister.vampire.backend.model.response.StartGameResponse;
import org.landister.vampire.backend.model.session.UserSession;
import org.landister.vampire.backend.services.GameService;
import org.landister.vampire.backend.services.InGameService;
import org.landister.vampire.backend.services.SessionCacheService;
import org.landister.vampire.backend.util.exceptions.GameException;
import org.landister.vampire.backend.websocket.ChatController;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Logic for the walking simulator game
 *GameState
 */
@ServerEndpoint("/walkingsimulator")
@ApplicationScoped
public class WalkingSimulator extends ChatController {

    @Inject
    ObjectMapper mapper;

    @Inject
    GameMapper gameMapper;

    @Inject
    LoadAssetsMapper loadAssetsMapper;

    @Inject
    InGameService gameService;

    @Inject
    SessionCacheService cacheService;

    private static final Logger LOG = Logger.getLogger(ChatController.class);


    //================================================================================
    // WebSocket methods
    //================================================================================

    @OnOpen
    public void onOpen(Session session) {
        super.onOpen(session);
    }

    @OnClose
    public void onClose(Session session) {
        UserSession userSession = super.onCloseChat(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        super.onErrorChat(session, throwable);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        LOG.debug("Processing: " + message);
        BaseRequest request = super.onMessageBase(session, message);
        UserSession userSession = sessionCacheService.getUserSessionFromUsername(request.getGameId(), request.getJwt().getName());
        onMessageChat(request, userSession, session);
        switch (request.getRequestType()) {
            case LEAVE_GAME:
                leaveGame(session, request.getGameId(), userSession);
                break;
            case LOAD_ASSETS:
                loadAssets(session, (LoadAssetsRequest)request, userSession);
                break;
            default:
                break;
        }
    }

    //================================================================================
    // Initial Load Request
    //================================================================================


    /**
     * Receives the initial load request from host user and sends information about the game to the rest of the players
     * 
     * @param session
     * @param request
     * @param userSession
     */
    private void loadAssets(Session session, LoadAssetsRequest request, UserSession userSession) {
        LOG.debug("Loading assets for: " + request);
        Game game = gameService.getGame(request.getGameId());
        if (!userSession.getUsername().equals(game.getHost())) {
            throw new GameException("Only the host can load assets");
        }
        // Not going to bother holding on to player data but will load map data here in the future
        broadcastMessageToGame(request.getGameId(), loadAssetsMapper.toStartGame(request));

    }

    /**
     * Removes the user from the game
     * 
     * @param session
     * @param gameId
     * @param userSession
     */
    private void leaveGame(Session session, String gameId, UserSession userSession) {
        gameService.leaveGame(gameId, userSession.getUsername());
    }

    // Adding session to cache
    @Override
    public void initialRequest(Session session, InitialRequest request) {
        LOG.info("Walking simulator init request from " + request.getJwt().getName());
        if(request.getGameId() == null || request.getGameId() == SessionCacheService.GLOBAL_GAME_ID) {
            throw new GameException("Game Id is required  to join a game");
        } 
        List<Game> games = gameService.getGamesByUsername(request.getJwt().getName());
        if(games.isEmpty()) {
            throw new GameException("You are not in the game");
        } else if (games.size() > 1) {
            throw new GameException("You are in more than one game");
        }
        Game game = games.get(0);
        UserSession userSession = new UserSession().gameId(request.getGameId()).username(request.getJwt().getName()).session(session);
        sessionCacheService.addToGamesCache(request.getGameId(), userSession);

        // If the host is the client and the game is not in a state to start, then send request to host to start the game
        if(game.getState() == GameState.LOADING && gameService.isHost(game, userSession.getUsername())) {
            broadcastMessageToUser(session, gameMapper.toLoadAssetsResponse(game));
        }
    }

    
}