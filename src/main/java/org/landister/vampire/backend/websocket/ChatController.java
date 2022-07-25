package org.landister.vampire.backend.websocket;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.jboss.logging.Logger;
import org.landister.vampire.backend.model.request.ChatRequest;
import org.landister.vampire.backend.model.request.UserRequest;
import org.landister.vampire.backend.model.response.ChatResponse;
import org.landister.vampire.backend.model.session.UserSession;

@ApplicationScoped
public class ChatController extends BaseController {


    // Maps game Id to Session Ids
    protected Map<String, ArrayList<String>> gameChatSessions = new ConcurrentHashMap<>();

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

    public UserRequest onMessageBase(Session session, String message) {
        UserRequest request = super.onMessageBase(session, message);
        UserSession userSession = sessionCacheService.getUserSession(request.getGameId(), session.getId());
        switch (request.getRequestType()) {
            case AUTH:
            broadcastMessageToGame(request.getGameId(), new ChatResponse("[color=green][b]" + userSession.getUsername() + "[/b] has joined the chat[/color]"));
                break;
            case CHAT:
                processMessage(session, (ChatRequest)request, userSession);
                break;
            default:
                break;
        }
        return request;
    }

    public void processMessage(Session session, ChatRequest request, UserSession userSession) {
        broadcastMessageToGame(request.getGameId(), new ChatResponse("[b]" + userSession.getUsername() + ":[/b] " + request.getMessage()));
    }

}