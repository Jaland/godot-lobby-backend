package org.landister.lobby.backend.websocket;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.Session;

import org.jboss.logging.Logger;
import org.landister.lobby.backend.model.request.BaseRequest;
import org.landister.lobby.backend.model.request.ChatRequest;
import org.landister.lobby.backend.model.response.chat.ChatResponse;
import org.landister.lobby.backend.model.session.UserSession;
import org.landister.lobby.backend.services.SessionCacheService;
import org.landister.lobby.backend.util.Colors;

/**
 * Contains the chat logic related to any websocket connection that extends this class.
 */
@ApplicationScoped
public class ChatController extends BaseController {


    @Inject
    protected SessionCacheService sessionCacheService;

    private static final Logger LOG = Logger.getLogger(ChatController.class);


    //================================================================================
    // Websocket Methods
    //================================================================================


    public UserSession onCloseChat(Session session) {
        UserSession userSession = sessionCacheService.getUserSessionFromSessionId(session.getId());
        if (userSession != null) {
            sessionCacheService.removeUserFromAllGamesCache(userSession.getUsername());
            broadcastMessageToGame(userSession.getGameId(), userMessage(userSession.getUsername(), "Left the chat", "red"));
        }
        return userSession;
    }

    public UserSession onErrorChat(Session session, Throwable throwable) {
        UserSession userSession = sessionCacheService.getUserSessionFromSessionId(session.getId());
        super.onError(session, throwable);
        if (userSession != null) {
            sessionCacheService.removeUserFromAllGamesCache(userSession.getUsername());
            broadcastMessageToGame(userSession.getGameId(), userMessage(userSession.getUsername(), "Errored out", "pink"));
        }
        return userSession;
    }

    public void onMessageChat(BaseRequest request, UserSession userSession, Session session) {
        switch (request.getRequestType()) {
            case INITIAL_REQUEST:
                broadcastMessageToGame(request.getGameId(), new ChatResponse("[color=green][b]" + userSession.getUsername() + "[/b] has joined the chat[/color]"));
                break;
            case CHAT:
                processMessage(session, (ChatRequest)request, userSession);
                break;
            default:
                break;
        }
    }

    public void processMessage(Session session, ChatRequest request, UserSession userSession) {
        // Message is sent to all other players in `white`.
        broadcastMessageToGame(request.getGameId(), 
            userMessage(userSession.getUsername(), request.getMessage(), Colors.WHITE), userSession.getUsername());
        // Message is sent back to the original player in `egg shell white`.
        broadcastMessageToUser(session, 
            userMessage(userSession.getUsername(), request.getMessage(), Colors.GRAY));
    }


    //================================================================================
    // Chat Utility Methods
    //================================================================================


    /**
     * Returns a formatted ChatResponse with the message and the username
     * 
     * @param user username of the user
     * @param message The message to be sent to the game
     * @param color (optional) - the color of the message (also accepts hex values) - https://htmlcolorcodes.com/
     * @return
     */
    protected ChatResponse userMessage(String user, String message, String... color) {
        String response = "[b]" + user + ":[/b] " + message;
        if(color != null && color.length > 0) {
            response = "[color=" + color[0] + "]" + response + "[/color]";
        }
        return new ChatResponse(response);
    }



}