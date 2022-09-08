package org.landister.lobby.backend.websocket;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.landister.lobby.backend.model.request.BaseRequest;
import org.landister.lobby.backend.model.request.auth.InitialRequest;
import org.landister.lobby.backend.model.response.BaseResponse;
import org.landister.lobby.backend.model.response.chat.ChatResponse;
import org.landister.lobby.backend.model.session.UserSession;
import org.landister.lobby.backend.services.SessionCacheService;
import org.landister.lobby.backend.util.Colors;
import org.landister.lobby.backend.util.exceptions.NotImplementedException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.mutiny.subscription.DemandPacer.Request;
/**
 * Extended by all sockets that require authentication(i.e. not login/register)
 * 
 * @author Landister
 */
public class BaseController {

    @Inject
    JWTParser parser;

    @Inject
    SessionCacheService sessionCacheService;

    @Inject
    ObjectMapper mapper;

    @Inject
    @ConfigProperty(name = "jwt.secret")
    String jwtSecret;

    private static final Logger LOG = Logger.getLogger(BaseController.class);
    

    // This is just a convenience method when debugging so you can filter out messages for broadcast
    private static final List DEBUG_IGNORED_RESPONSE_TYPES = List.of("player_update");

    //================================================================================
    // WebSocket methods
    //================================================================================

    public void onOpen(Session session) {
        LOG.info("Session " + session.getId() + " opened");
    }

    public void onClose(Session session) {
        LOG.info("Session " + session.getId() + " closed");
    }

    public void onError(Session session, Throwable throwable) {
        LOG.error("Error in session: {}\n {}", session.getId(), throwable);
    }

    public BaseRequest onMessageBase(Session session, String message) {
        BaseRequest request;
        try {
            request = mapper.readValue(message, BaseRequest.class);
        } catch (Exception e) {
            LOG.error("Error reading json in session: " + session.getId() + " " + e.getMessage());
            broadcastMessageToUser(session, infoMessage("Exception reading message \n Error: " + e.getMessage() , Colors.RED));
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Error reading json"));
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //TODO: Send a message to client to restart. (Should probably be a separate method that can be called from anywhere)
            return null;
        }
        if(request.getToken() == null) {
            LOG.error("User has not authenticated yet in session:" + session.getId());
        }
        try {
            JsonWebToken token = parser.verify(request.getToken(), jwtSecret);
            request.jwt(token);
        } catch (ParseException e) {
            broadcastMessageToUser(session,
                infoMessage("Authentication error, please try disconnecting and logging back in", Colors.RED));
                LOG.error("Error parsing token in session: {}\n {}", session.getId(), e);
        }
        switch (request.getRequestType()){
            case INITIAL_REQUEST:
                initialRequest(session, (InitialRequest)request);
                break;
            default:
                break;
        }
        return request;
    }


    //================================================================================
    // Broadcast Methods
    //================================================================================

    /**
     * Broadcast a message to the owner of a specific session
     * 
     * @param response
     */
    protected void broadcastMessageToUser(Session session, BaseResponse response) {
        if(LOG.isDebugEnabled() && !DEBUG_IGNORED_RESPONSE_TYPES.contains(response.getType())) {
            LOG.debug("Broadcasting " + response.getType() + " message to user: " + response);
        }
        try {
            session.getAsyncRemote().sendText(mapper.writeValueAsString(response), result -> {
            if (result.getException() != null) {
                LOG.error("Unable to send message: " + result.getException() + "\n");
            }
        });
        } catch (JsonProcessingException e) {
            LOG.error("Unable to parse message: " + response + "\n" + e.getMessage());
        }
    }


    /**
     * Broadcast a message to all users in a game
     * TODO: Make the reverse of this method for private chats, note the session method above is more efficent if just responding to a knownsession (I think)
     * 
     * @param gameId
     * @param response
     * @param username - if not included, broadcast to all users in game
     */
    protected void broadcastMessageToGame(String gameId, BaseResponse response, String... excludeUsernames) {
        if(LOG.isDebugEnabled() && !DEBUG_IGNORED_RESPONSE_TYPES.contains(response.getType())) {
            LOG.debug("Broadcasting " + response.getType() + " message to game " + gameId + ": " + response);
        }
        Map<String, UserSession> gameSession = sessionCacheService.getGameSessions(gameId);
        if(gameSession == null) {
            LOG.error("Broadcast Error: Game session not found for game: " + gameId);
            return;
        }
        gameSession.values().forEach(s -> {
            try {
                //If the username is part of the exclude list, don't send the message
                if(excludeUsernames.length > 0 && Arrays.stream(excludeUsernames).anyMatch(s.getUsername()::equals)) {
                    return;
                }
                // Send the message to the user
                s.getSession().getAsyncRemote().sendText(mapper.writeValueAsString(response), result -> {
                    if (result.getException() != null) {
                        LOG.error("Unable to send message: " + result.getException() + "\n");
                    }
                });
            } catch (JsonProcessingException e) {
                LOG.error("Unable to parse message: " + response + "\n" + e.getMessage());
            }
        });
    }

    //================================================================================
    // Methods to Override
    //================================================================================

    /**
     * Method should be handled by subclasses
     */
    public void initialRequest(Session session, InitialRequest request) throws NotImplementedException {
        throw new NotImplementedException("Initial request not implemented, this should always be overwritten by subclasses");
    }



    /**
     * Returns a formatted ChatResponse with the message, used when not related to a specific user
     * @param user
     * @param message
     * @param color
     * @return
     */
    protected ChatResponse infoMessage(String message, String... color) {
        String response = message;
        if(color != null && color.length > 0) {
            response = "\n[color=" + color[0] + "]" + response + "[/color]\n";
        }
        return new ChatResponse(response);
    }

}