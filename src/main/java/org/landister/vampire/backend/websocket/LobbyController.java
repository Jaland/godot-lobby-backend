package org.landister.vampire.backend.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import org.landister.vampire.backend.model.request.UserRequest;
import org.landister.vampire.backend.model.request.lobby.CreateGameRequest;
import org.landister.vampire.backend.model.response.ChatResponse;
import org.landister.vampire.backend.model.response.GameResponse;
import org.landister.vampire.backend.model.response.GetAllGamesResponse;
import org.landister.vampire.backend.model.session.UserSession;
import org.landister.vampire.backend.services.SessionCacheService;

import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint("/lobby")
@ApplicationScoped
public class LobbyController extends ChatController {


    // Maps game Id to Session Ids
    protected Map<String, ArrayList<String>> gameChatSessions = new ConcurrentHashMap<>();

    @Inject
    ObjectMapper mapper;

    @Inject
    GameMapper gameMapper;

    private static final Logger LOG = Logger.getLogger(ChatController.class);

    @OnOpen
    public void onOpen(Session session) {
        super.onOpen(session);
    }

    @OnClose
    public void onClose(Session session) {
        UserSession userSession = super.onCloseBase(session);
        if (userSession != null) {
            broadcastMessageToGame(userSession.getGameId(), new ChatResponse("[color=red][b]" + userSession.getUsername() + "[/b] has left the chat[/color]"));
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        UserSession userSession = super.onErrorBase(session, throwable);
        if (userSession != null) {
            broadcastMessageToGame(userSession.getGameId(), new ChatResponse("[color=red][b]" + userSession.getUsername() + "[/b] has left the chat[/color]"));
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        UserRequest request= super.onMessageBase(session, message);
        UserSession userSession = sessionCacheService.getUserSession(request.getGameId(), session.getId());
        switch (request.getRequestType()) {
            case LOBBY_REFRESH:
                refreshLobby(session, userSession);
                break;
            case CREATE_GAME:
                newGame(session, (CreateGameRequest)request, userSession);
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
        broadcastMessageToUser(SessionCacheService.GLOBAL_GAME_ID, response, userSession);
    }


    private void newGame(Session session, CreateGameRequest request, UserSession userSession) {
        Game game = new Game().name(request.getName()).users(List.of(userSession.getUsername()));
        game.persist();
        LOG.info("Created Game: " + game);
        GameResponse response = gameMapper.toGameResponse(game);
        broadcastMessageToGame(SessionCacheService.GLOBAL_GAME_ID, response);
    }
}