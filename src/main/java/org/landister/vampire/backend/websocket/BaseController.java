package org.landister.vampire.backend.websocket;

import javax.inject.Inject;
import javax.websocket.Session;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.landister.vampire.backend.model.request.AuthRequest;
import org.landister.vampire.backend.model.request.UserRequest;
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
        sessionCacheService.addToSession(SessionCacheService.GLOBAL_GAME_ID, session.getId(), 
                            new UserSession().session(session).gameId(SessionCacheService.GLOBAL_GAME_ID));
    }

    public UserSession onCloseBase(Session session) {
        return sessionCacheService.removeAllSessions(session.getId());
    }

    public UserSession onErrorBase(Session session, Throwable throwable) {
        LOG.error("Error in session: {}\n {}", session.getId(), throwable);
        return sessionCacheService.removeAllSessions(session.getId());
    }

    public UserRequest onMessageBase(Session session, String message) {
        UserRequest userRequest;
        try {
            userRequest = mapper.readValue(message, UserRequest.class);
        } catch (Exception e) {
            LOG.error("Error reading json in session: {}\n {}", session.getId(), e);
            throw new RuntimeException(e);
        }
        UserSession userSession = sessionCacheService.getUserSession(userRequest.getGameId(), session.getId());
        if (userSession == null) {
            LOG.error("Session not found: " + session.getId());
            sessionCacheService.closeSession(session, "Session not found");
        }
        if(userSession.getToken() == null) {
            if(userRequest.getClass() != AuthRequest.class)
                throw new IllegalArgumentException("User Not Yet Authenticated");
            AuthRequest authRequest = (AuthRequest) userRequest;
            if(authRequest.getToken() == null)
                throw new IllegalArgumentException("Token Not Present");
                
            // Validated that `parse.verify` will actually check to make sure the token isn't expired.
            try {
                JsonWebToken token = parser.verify(authRequest.getToken(), jwtSecret);
                userSession.setUsername(token.getName());
            } catch (ParseException e) {
                LOG.error("Invalid Token: " + authRequest.getToken(), e);
                sessionCacheService.closeSession(session, "Invalid Token");
            }
            userSession.setToken(authRequest.getToken());
        }
        return userRequest;
    }


    protected void broadcastMessageToGame(int gameId, BaseResponse response) {
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
}