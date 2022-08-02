package org.landister.vampire.backend.websocket;

import java.io.IOException;

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

    public void initialRequest(Session session, InitialRequest request) {
        LOG.info("Initial request from " + request.getJwt().getName());
      
        UserSession userSession = new UserSession().session(session).gameId(SessionCacheService.GLOBAL_GAME_ID).username(request.getJwt().getName()).session(session)
            .token(request.getToken());

        //If the GameId is not null, then attempt to restore the user to the game they were in.
        if(request.getGameId() != null) {
            try{ 
                restoreSession(session, request.getGameId(), userSession);
            } catch(Exception e) {
                LOG.error("Error restoring user to game: " + request.getGameId());
            }
        }
        //Otherwise set the user's game to the lobby
        request.setGameId(SessionCacheService.GLOBAL_GAME_ID);
    }


    /*
     * Restore the user to the game they were in.
     * 
     * Note this will just set the request gameId to the game they were in. Logic for restoring needs to happen in the lobby controller
     * 
     */
    private UserSession restoreSession(Session session, String gameId, UserSession userSession) {
        LOG.info("Restoring session " + session.getId() + " to game " + gameId);
        return sessionCacheService.getUserSessionFromUsername(gameId, userSession.getUsername());
    }

    protected void broadcastMessageToGame(String gameId, BaseResponse response) {
        sessionCacheService.getGameSessions(gameId).values().forEach(s -> {
            try {
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

    protected void broadcastMessageToUser(BaseResponse response, Session session) {
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
}