package org.landister.vampire.backend.websocket;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.landister.vampire.backend.model.dao.game.Game;
import org.landister.vampire.backend.model.request.BaseRequest;
import org.landister.vampire.backend.model.request.auth.InitialRequest;
import org.landister.vampire.backend.model.request.lobby.CreateGameRequest;
import org.landister.vampire.backend.model.request.lobby.JoinGameRequest;
import org.landister.vampire.backend.model.response.GameResponse;
import org.landister.vampire.backend.model.response.StartGameResponse;
import org.landister.vampire.backend.model.response.lobby.GameLobbyResponse;
import org.landister.vampire.backend.model.response.lobby.GetAllGamesResponse;
import org.landister.vampire.backend.model.response.lobby.LeaveGameResponse;
import org.landister.vampire.backend.model.response.lobby.NewGameResponse;
import org.landister.vampire.backend.model.session.UserSession;
import org.landister.vampire.backend.services.SessionCacheService;
import org.landister.vampire.backend.services.lobby.LobbyGameService;
import org.landister.vampire.backend.util.Colors;
import org.landister.vampire.backend.util.exceptions.GameException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Logic for lobby related websocket connections.
 *
 */
@ServerEndpoint("/lobby")
@ApplicationScoped
public class LobbyController extends ChatController {

    @Inject
    ObjectMapper mapper;

    @Inject
    GameMapper gameMapper;

    @Inject
    LobbyGameService gameService;

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
        if(userSession != null) {
            sessionCacheService.removeUserFromAllGamesCache(userSession.getUsername());
        }
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
            case LOBBY_REFRESH:
                refreshLobby(session, userSession);
                break;
            case CREATE_GAME:
                createGame(session, (CreateGameRequest)request, userSession);
                break;  
            case JOIN_GAME:
                joinGame(session, ((JoinGameRequest)request).getJoinGameId() , userSession);
                break;
            case LEAVE_GAME:
                joinLobby(session, request.getGameId(), userSession);
                break;
            case START_GAME:
                startGame(session, request.getGameId(), userSession);
                break;
            default:
                break;
        }
    }


    //================================================================================
    // Main Lobby methods
    //================================================================================

    private void refreshLobby(Session session, UserSession userSession) {
        Stream<Game> games = Game.streamAll();
        if(games == null) {
            return;
        }
        GetAllGamesResponse response = new GetAllGamesResponse()
            .games(
                games.map(gameMapper::toGameResponse).collect(Collectors.toList())
            );
        broadcastMessageToUser(session, response);
    }

    //================================================================================
    // Game Lobby methods
    //================================================================================

    //TODO: Validate this method
    private void createGame(Session session, CreateGameRequest request, UserSession userSession) {
        Game game = gameService.createGame(userSession, request);
        sessionCacheService.changeGames(SessionCacheService.GLOBAL_GAME_ID, game.getIdHexString(), userSession);
        LOG.info("User " + userSession.getUsername() + " created game: " + game.getIdHexString());
        GameResponse gameResponse = gameMapper.toGameResponse(game);
        // Send a join game request to the user who created the game.
        broadcastMessageToUser(session, new GameLobbyResponse().game(gameResponse));
        // Send a chat message to everyone letting them know they have a new friend.
        broadcastMessageToGame(SessionCacheService.GLOBAL_GAME_ID, new NewGameResponse().game(gameResponse));
    }


    private void joinGame(Session session, String gameIdToJoin, UserSession userSession) {
        Game game = gameService.joinGame(userSession, gameIdToJoin);
        sessionCacheService.changeGames(SessionCacheService.GLOBAL_GAME_ID, game.getIdHexString(), userSession);
        LOG.info("User " + userSession.getUsername() + " joined game: " + game.getIdHexString());
        GameResponse gameResponse = gameResponseFromGame(game);
        // Send a join game request to the user who joined the game.
        broadcastMessageToUser(session, new GameLobbyResponse().game(gameResponse));
        // Send an update to all other user's game info in the game.
        broadcastMessageToGame(game.getIdHexString(), gameResponse, userSession.getUsername());
        // Send a message to all users in the game that the user joined.
        broadcastMessageToGame(game.getIdHexString(), userMessage(userSession.getUsername(), "Joined the Game", Colors.AQUAMARINE));
    }


    private void joinLobby(Session session, String gameIdLeaving, UserSession userSession) {
        LOG.info("User: " + userSession.getUsername() + " Joining Lobby, leaving game: " + gameIdLeaving);
        if(gameIdLeaving != null) {     
            Game game = gameService.leaveGame(gameIdLeaving, userSession.getUsername());
            broadcastMessageToGame(gameIdLeaving, userMessage(userSession.getUsername(), "Left the Game", Colors.DARK_MAGENTA));
            sessionCacheService.changeGames(gameIdLeaving, SessionCacheService.GLOBAL_GAME_ID, userSession);
            // Send an update to all other user's game info in the game.
            broadcastMessageToGame(gameIdLeaving, gameResponseFromGame(game), userSession.getUsername());
        } else {
            sessionCacheService.addToGamesCache(SessionCacheService.GLOBAL_GAME_ID, userSession);
        }
        // Return the user to the lobby.
        broadcastMessageToUser(session, new LeaveGameResponse());
        // Send a message to all users in the game that the user joined.
        broadcastMessageToGame(SessionCacheService.GLOBAL_GAME_ID, userMessage(userSession.getUsername(), "Joined the Lobby", Colors.DARK_GREEN));
    }

    private void startGame(Session session, String gameId, UserSession userSession) {
        try {
            Game game = gameService.startGame(gameId, userSession.getUsername());
        } catch (GameException e) {
            broadcastMessageToGame(gameId, infoMessage(e.getMessageToClient(), Colors.RED));
        }
        broadcastMessageToGame(gameId, new StartGameResponse().gameId(gameId));
    }


    //================================================================================
    // Initial Request Logic methods
    //================================================================================


    @Override
    public void initialRequest(Session session, InitialRequest request) {
        LOG.info("Initial request from " + request.getJwt().getName());
      
        // Check to see if the user is in a game.
        UserSession userSession = sessionCacheService.getUserSessionFromUsername(request.getJwt().getName());
        if(userSession != null) {
            // Since the userSession was retrieved from the cache, need to do a deep copy to create a new user session.
            userSession = UserSession.clone(userSession);
            userSession.session(session);
            if(userSession.getGameId() != SessionCacheService.GLOBAL_GAME_ID) {
                // If the user is in a game try to allow them to rejoin.
                joinGame(session, userSession.getGameId(), userSession);
                return;
            } else {
                // If the user is in the lobby, send them to the lobby.
                joinLobby(session, null, userSession);
                return;
            }
        } else {
            checkDatabaseForGame(session, request);
        }
    }



    //================================================================================
    // Utility Methods
    //================================================================================

    /**
     * Gets game response from game and checks cache for disconnected users
     * 
     * @param game
     * @return
     */
    private GameResponse gameResponseFromGame(Game game) {
        GameResponse gameResponse = gameMapper.toGameResponse(game);
        List <String> connectedUsers = sessionCacheService.getConnectedUserSessionsFromGame(game.getIdHexString()).map(UserSession::getUsername).collect(Collectors.toList());
        gameResponse.getUsers().forEach(user -> {
            if(connectedUsers.contains(user.getUsername())) {
                user.setConnected(true);
            } else {
                user.setConnected(false);
            }
        });
        return gameResponse;
    }

    private void checkDatabaseForGame(Session session, BaseRequest request) {
        String username = request.getJwt().getName();
        List<Game> games = gameService.getGamesByUsername(username);
        if(games.size() > 1) {
            LOG.error("User " + username + " has more than one game in the database. Removing them from all games.");
            games.forEach(game -> gameService.leaveGame(game.getIdHexString(), username));
        } else if (games.size() == 1) {
            // If the user is in a game, send them to the game.
            LOG.info("User " + username + " is in game " + games.get(0).getIdHexString() + " restoring session.");
            joinGame(session, games.get(0).getIdHexString(), new UserSession().username(username).gameId(games.get(0).getIdHexString()).session(session));
            return;
        }
        // If the user is not in a game, send them to the lobby.
        joinLobby(session, null, 
            new UserSession().username(username).session(session));
        // If we can't find the game need to modify the request so that we are pointing back to the lobby
        request.gameId(SessionCacheService.GLOBAL_GAME_ID);
    }
}