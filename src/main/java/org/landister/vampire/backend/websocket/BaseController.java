package org.landister.vampire.backend.websocket;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.websocket.Session;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.landister.vampire.backend.model.request.BaseRequest;
import org.landister.vampire.backend.model.request.auth.InitialRequest;
import org.landister.vampire.backend.model.response.BaseResponse;
import org.landister.vampire.backend.model.session.UserSession;
import org.landister.vampire.backend.services.SessionCacheService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
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
            LOG.error("Error reading json in session: {}\n {}", session.getId(), e);
            throw new RuntimeException(e);
        }
        if(request.getToken() == null) {
            LOG.error("User has not authenticated yet in session:" + session.getId());
        }
        try {
            JsonWebToken token = parser.verify(request.getToken(), jwtSecret);
            request.jwt(token);
        } catch (ParseException e) {
            LOG.error("Error parsing token in session: {}\n {}", session.getId(), e);
            throw new RuntimeException(e);
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

    /**
     * Method should be handled by subclasses
     */
    public void initialRequest(Session session, InitialRequest request) {
        throw new RuntimeException("Initial request not implemented");
    }

    /**
     * Broadcast a message to the owner of a specific session
     * 
     * @param response
     */
    protected void broadcastMessageToUser(Session session, BaseResponse response) {
        try {
            session.getAsyncRemote().sendText(mapper.writeValueAsString(response), result -> {
            if (result.getException() != null) {
                LOG.error("Unable to send message: " + result.getException() + "\n");
            }
        });
        } catch (JsonProcessingException e) {
            LOG.error("Unable to parse message: " + response + "\n");
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
                LOG.error("Unable to parse message: " + response + "\n");
            }
        });
    }
}