package org.landister.vampire.backend.websocket;

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
import org.landister.vampire.backend.model.game.Game;
import org.landister.vampire.backend.model.request.BaseRequest;
import org.landister.vampire.backend.model.request.BaseRequest.RequestType;
import org.landister.vampire.backend.model.request.auth.InitialRequest;
import org.landister.vampire.backend.model.request.lobby.CreateGameRequest;
import org.landister.vampire.backend.model.request.lobby.JoinGameRequest;
import org.landister.vampire.backend.model.response.GameResponse;
import org.landister.vampire.backend.model.response.chat.ChatResponse;
import org.landister.vampire.backend.model.response.lobby.GameLobbyResponse;
import org.landister.vampire.backend.model.response.lobby.GetAllGamesResponse;
import org.landister.vampire.backend.model.response.lobby.LeaveGameResponse;
import org.landister.vampire.backend.model.response.lobby.NewGameResponse;
import org.landister.vampire.backend.model.session.UserSession;
import org.landister.vampire.backend.services.GameService;
import org.landister.vampire.backend.services.SessionCacheService;
import org.landister.vampire.backend.util.Colors;

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
    GameService gameService;

    @Inject
    SessionCacheService cacheService;

    private static final Logger LOG = Logger.getLogger(ChatController.class);


    @OnOpen
    public void onOpen(Session session) {
        super.onOpen(session);
    }

    @OnClose
    public void onClose(Session session) {
        UserSession userSession = super.onCloseChat(session);
        if(userSession != null) {
            cacheService.removeUserFromAllGames(userSession.getUsername());
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        UserSession userSession = super.onErrorChat(session, throwable);
        if (userSession != null) {
            broadcastMessageToGame(userSession.getGameId(), new ChatResponse("[color=red][b]" + userSession.getUsername() + "[/b] has left the chat[/color]"));
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        LOG.debug("Processing: " + message);
        BaseRequest request = super.onMessageBase(session, message);
        // Need to handle initial request separately since user info not in cache yet
        if(request.getRequestType() == RequestType.INITIAL_REQUEST) {
            handleInitialRequest(session, (InitialRequest)request);
            return;
        }
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
            default:
                break;
        }
    }

    private void refreshLobby(Session session, UserSession userSession) {
        Stream<Game> games = Game.streamAll();
        GetAllGamesResponse response = new GetAllGamesResponse()
            .games(
                games.map(gameMapper::toGameResponse).collect(Collectors.toList())
            );
        broadcastMessageToUser(response, session);
    }

    private void createGame(Session session, CreateGameRequest request, UserSession userSession) {
        Game game = gameService.createGame(userSession, request);
        cacheService.changeGames(SessionCacheService.GLOBAL_GAME_ID, game.getIdHexString(), userSession);
        LOG.info("User " + userSession.getUsername() + " created game: " + game.getIdHexString());
        GameResponse gameResponse = gameMapper.toGameResponse(game);
        // Send a join game request to the user who created the game.
        broadcastMessageToUser(new GameLobbyResponse().game(gameResponse), session);
        // Send a new game request to all users in the lobby.
        broadcastMessageToGame(SessionCacheService.GLOBAL_GAME_ID, new NewGameResponse().game(gameResponse));
    }


    private void joinGame(Session session, String gameIdToJoin, UserSession userSession) {
        Game game = gameService.joinGame(userSession, gameIdToJoin);
        cacheService.changeGames(SessionCacheService.GLOBAL_GAME_ID, game.getIdHexString(), userSession);
        LOG.debug("User " + userSession.getUsername() + " joined game: " + game.getIdHexString());
        GameResponse gameResponse = gameMapper.toGameResponse(game);
        // Send a join game request to the user who joined the game.
        broadcastMessageToUser(new GameLobbyResponse().game(gameResponse), session);
        // Send a message to all users in the game that the user joined.
        broadcastMessageToGame(game.getIdHexString(), userMessage(userSession.getUsername(), "Joined the Game Chat", Colors.AQUAMARINE));
    }


    private void joinLobby(Session session, String gameIdLeaving, UserSession userSession) {
        if(gameIdLeaving != null) {     
            gameService.leaveGame(gameIdLeaving, userSession.getUsername());
            broadcastMessageToGame(gameIdLeaving, userMessage(userSession.getUsername(), "Left the game", Colors.DARK_MAGENTA));
            cacheService.changeGames(gameIdLeaving, SessionCacheService.GLOBAL_GAME_ID, userSession);
        } else {
            cacheService.addToSession(SessionCacheService.GLOBAL_GAME_ID, userSession);
        }
        // Return the user to the lobby.
        broadcastMessageToUser(new LeaveGameResponse(), session);
        // Send a message to all users in the game that the user joined.
        broadcastMessageToGame(SessionCacheService.GLOBAL_GAME_ID, userMessage(userSession.getUsername(), "Joined the Lobby", Colors.DARK_GREEN));
    }


    private void handleInitialRequest(Session session, InitialRequest request) {
        UserSession userSession = new UserSession().username(request.getJwt().getName()).gameId(request.getGameId()).session(session);
        if(request.getGameId() != SessionCacheService.GLOBAL_GAME_ID) {
            joinGame(session, request.getGameId(), userSession);
        } else {
            joinLobby(session, null, userSession);
        }
    }
}