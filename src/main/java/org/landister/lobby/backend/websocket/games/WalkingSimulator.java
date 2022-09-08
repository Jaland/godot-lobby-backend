package org.landister.lobby.backend.websocket.games;

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
import org.landister.lobby.backend.mapper.GameMapper;
import org.landister.lobby.backend.mapper.LoadAssetsMapper;
import org.landister.lobby.backend.mapper.PlayerUpdateMapper;
import org.landister.lobby.backend.model.dao.game.Game;
import org.landister.lobby.backend.model.enums.GameState;
import org.landister.lobby.backend.model.request.BaseRequest;
import org.landister.lobby.backend.model.request.auth.InitialRequest;
import org.landister.lobby.backend.model.request.ingame.GoalTouchedRequest;
import org.landister.lobby.backend.model.request.ingame.LoadAssetsRequest;
import org.landister.lobby.backend.model.request.ingame.PlayerUpdateRequest;
import org.landister.lobby.backend.model.request.ingame.RestartGameRequest;
import org.landister.lobby.backend.model.request.ingame.StartGameRequest;
import org.landister.lobby.backend.model.response.ingame.GameOverResponse;
import org.landister.lobby.backend.model.response.ingame.MapSetupResponse;
import org.landister.lobby.backend.model.response.ingame.PlayerUpdateResponse;
import org.landister.lobby.backend.model.response.ingame.StartingGameResponse;
import org.landister.lobby.backend.model.session.UserSession;
import org.landister.lobby.backend.model.shared.Player;
import org.landister.lobby.backend.services.InGameService;
import org.landister.lobby.backend.services.SessionCacheService;
import org.landister.lobby.backend.util.Colors;
import org.landister.lobby.backend.util.exceptions.GameException;
import org.landister.lobby.backend.websocket.ChatController;

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
            case START_GAME:
                startGame(session, (StartGameRequest)request, userSession);
                break;
            case RESTART_GAME:
                restartGame(session, (RestartGameRequest)request, userSession);
                break;
            case GOAL_TOUCHED:
                goalTouched(session, (GoalTouchedRequest)request, userSession);
                break;
            default:
                break;
        }
    }

    //================================================================================
    // Process Request methods
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

        // Only allow host to load assets
        if (!userSession.getUsername().equals(game.getHost())) {
            broadcastMessageToUser(session, infoMessage("Only the host can load assets", Colors.LIGHT_YELLOW));
            return;
        }

        game.getUsers().forEach(u -> {
            if (request.getPlayers().containsKey(u.getName())) {
                u.setSpawnPosition(request.getPlayers().get(u.getName()).getPosition());
            }
        });
        game.setGoal(request.getGoal());
        game.setState(GameState.LOADED);
        game.update();
        // Pass back the loaded asset info and the host information
        MapSetupResponse response = loadAssetsMapper.toStartGame(request);
        response.host(game.getHost());
        broadcastMessageToGame(request.getGameId(), response);
    }

    private void goalTouched(Session session, GoalTouchedRequest request, UserSession userSession) {
        Game game = gameService.getGame(request.getGameId());
        if(game.getState().isCompleted) {
            LOG.debug("Game already completed, ignoring goal touched request");
            return;
        }
        if(!game.getState().isCurrentlyPlaying) {
            LOG.error("Goal touched while game in state: " + game.getState());
            broadcastMessageToGame(request.getGameId(), infoMessage("Someone touched the goal before the game has started", Colors.RED));
            return;
        }
        game.setState(GameState.FINISHED);
        game.update();
        broadcastMessageToGame(request.getGameId(), new GameOverResponse().winner(request.getWinner()));

    }


    private void restartGame(Session session, RestartGameRequest request, UserSession userSession) {
        Game game = gameService.getGame(request.getGameId());
        if(!gameService.isHost(game, userSession.getUsername())) {
            broadcastMessageToUser(session, infoMessage("Only host can restart a game", Colors.RED));
        }
        gameService.resetGame(game);
        restartGame(game, userSession);
    }



    private void startGame(Session session, StartGameRequest request, UserSession userSession) {
        Game game = gameService.getGame(request.getGameId());
        // Only allow host to start games
        if (!userSession.getUsername().equals(game.getHost())) {
            broadcastMessageToUser(session, infoMessage("Only the host can start game", Colors.LIGHT_YELLOW));
            return;
        }

        game = gameService.startGame(game);


        // Broadcast to all players that the game has started
        broadcastMessageToGame(request.getGameId(), infoMessage("Game starting!", Colors.LIME));
        broadcastMessageToGame(request.getGameId(), new StartingGameResponse());
        
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
    private void restartGame(Game game, UserSession userSession) {
        Session session = userSession.getSession();
        // If we are in a loading state and the request is from the host, then tell the host to load assets (i.e. Create spawn points for everything)
        if(game.getState() == GameState.LOADING && gameService.isHost(game, userSession.getUsername())) {
            broadcastMessageToUser(session, gameMapper.toLoadAssetsResponse(game));
        }
        // If we are in a loading state and the request is not from the host ignore it
        else if(game.getState() == GameState.LOADING){ 
            return;        
        }
        // If we are in any other state just send back the map information
        else {
            MapSetupResponse response = new MapSetupResponse();
            game.getUsers().forEach(u -> {
                response.getPlayers().put(u.getName(), new Player().position(u.getSpawnPosition()));
            });
            response.host(game.getHost()).setGoal(game.getGoal());
            if(game.getState().isLoading)
                broadcastMessageToUser(session, response);
        }
    }
    
}