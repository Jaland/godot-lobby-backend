package org.landister.vampire.backend.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;
import org.landister.vampire.backend.model.request.AuthRequest;
import org.landister.vampire.backend.model.request.UserRequest;
import org.landister.vampire.backend.model.session.UserSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.jwt.auth.principal.JWTParser;
/**
 * Extended by all sockets that require authentication(i.e. not login/register)
 * 
 * @author Landister
 */
public class BaseController {

    protected Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    ObjectMapper mapper = new ObjectMapper();

    @Inject 
    JWTParser parser;

    @ConfigProperty(name = "jwt.secret")
    String jwtSecret;

    private static final Logger LOG = Logger.getLogger(LoginController.class);

    public void onOpen(Session session) {
        sessions.put(session.getId(), new UserSession().session(session));
    }

    public void onClose(Session session) {
        sessions.remove(session.getId());
    }

    public void onError(Session session, Throwable throwable) {
        sessions.remove(session.getId());
        LOG.error("Error in session: {}\n {}", session.getId(), throwable);
    }

    public void onMessage(Session session, String message) {
        UserSession userSession = sessions.get(session.getId());
        if (userSession == null) {
            LOG.error("Session not found: " + session.getId());
            closeSession(session, "Session not found");
        }
        if(userSession.getToken() == null) {
            try {
                UserRequest userRequest = mapper.readValue(message, UserRequest.class);
                if(userRequest.getClass() != AuthRequest.class)
                    throw new IllegalArgumentException("User Not Yet Authenticated");
                AuthRequest authRequest = (AuthRequest) userRequest;
                if(authRequest.getToken() == null)
                    throw new IllegalArgumentException("Token Not Present");
                 
                // Validated that `parse.verify` will actually check to make sure the token isn't expired.
                parser.verify(authRequest.getToken(), jwtSecret);
                userSession.setToken(authRequest.getToken());
            } catch (Exception e) {
                LOG.error("Error reading json in se session: {}\n {}", session.getId(), e);
                closeSession(session, "Error reading json");
            }
            LOG.error("User session is null");
            return;
        }
    }

    protected void closeSession(Session session, String reason) {
        sessions.remove(session.getId());
        try {
            session.close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION, reason));
        } catch (IOException e) {
            LOG.error("Error closing session: {}\n {}", session.getId(), e);
            throw new RuntimeException(e);
        }
    }

}