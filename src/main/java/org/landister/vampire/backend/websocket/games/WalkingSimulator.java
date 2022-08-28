package org.landister.vampire.backend.websocket.games;

import java.io.IOException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;
import org.landister.vampire.backend.mapper.GameMapper;
import org.landister.vampire.backend.mapper.LoadAssetsMapper;
import org.landister.vampire.backend.mapper.PlayerUpdateMapper;
import org.landister.vampire.backend.model.dao.game.Game;
import org.landister.vampire.backend.model.enums.GameState;
import org.landister.vampire.backend.model.request.BaseRequest;
import org.landister.vampire.backend.model.request.auth.InitialRequest;
import org.landister.vampire.backend.model.request.ingame.LoadAssetsRequest;
import org.landister.vampire.backend.model.request.ingame.PlayerUpdateRequest;
import org.landister.vampire.backend.model.request.ingame.RestartGameRequest;
import org.landister.vampire.backend.model.response.ingame.MapSetupResponse;
import org.landister.vampire.backend.model.response.ingame.PlayerUpdateResponse;
import org.landister.vampire.backend.model.session.UserSession;
import org.landister.vampire.backend.model.shared.Player;
import org.landister.vampire.backend.services.InGameService;
import org.landister.vampire.backend.services.SessionCacheService;
import org.landister.vampire.backend.util.Colors;
import org.landister.vampire.backend.util.exceptions.GameException;
import org.landister.vampire.backend.websocket.ChatController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonSerializable.Base;

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
    PlayerUpdateMapper playerUpdateMapper;

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
    public void onMessage(Session session, String message) throws IOException {
        LOG.trace("Processing: " + message);
        BaseRequest request = super.onMessageBase(session, message);
        if(request == null) {
            // Issue processing request
            return;
        }
        UserSession userSession = sessionCacheService.getUserSessionFromUsername(request.getGameId(), request.getJwt().getName());
        if(userSession == null) {
            //Probably want to remove this for production release, but useful for debugging
            broadcastMessageToUser(session, 
                infoMessage("Issue finding cached user session when resolving message below, please reconnect to the game \n\n"
                     + message, Colors.RED));
            session.close(new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, "Issue finding cached user session"));
            return;
        }
        onMessageChat(request, userSession, session);
        switch (request.getRequestType()) {
            case LEAVE_GAME:
                leaveGame(session, request.getGameId(), userSession);
                break;
            case LOAD_ASSETS:
                loadAssets(session, (LoadAssetsRequest)request, userSession);
                break;
            case PLAYER_UPDATE:
                updatePlayerInfo(session, (PlayerUpdateRequest)request, userSession);
                break;
            default:
                break;
        }
    }

    //================================================================================
    // Initial Load Request
    //================================================================================


    /**
     * Sends player update information
     * 
     * Note: We are not hitting the database here to hopefully increase performance
     * 
     * @param session
     * @param request
     * @param userSession
     */
    private void updatePlayerInfo(Session session, PlayerUpdateRequest request, UserSession userSession) {
        LOG.trace("Updating player info for " + request);
        PlayerUpdateResponse response = playerUpdateMapper.toResponse(request);
        broadcastMessageToGame(userSession.getGameId(), response, request.getUsername());
    }

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
        game.getUsers().forEach(u -> {
            if (request.getPlayers().containsKey(u.getName())) {
                u.setSpawnPosition(request.getPlayers().get(u.getName()).getPosition());
            }
        });
        game.setGoal(request.getGoal());
        game.setState(GameState.LOADED);
        game.update();
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

        restartGame(game, userSession);
    }


    /**
     * 
     * @param game
     * @param userSession
     * @param restarting
     */  
    private void restartGame(Game game, UserSession userSession, boolean... restarting) {
        Session session = userSession.getSession();
        if(game.getState() == GameState.LOADING && gameService.isHost(game, userSession.getUsername())) {
            broadcastMessageToUser(session, gameMapper.toLoadAssetsResponse(game));
        } else {
            MapSetupResponse response = new MapSetupResponse();
            game.getUsers().forEach(u -> {
                response.getPlayers().put(u.getName(), new Player().position(u.getSpawnPosition()));
            });
            response.setGoal(game.getGoal());
            broadcastMessageToUser(session, response);
        }
    }
    
}