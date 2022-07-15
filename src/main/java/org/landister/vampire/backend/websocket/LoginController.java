package org.landister.vampire.backend.websocket;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;
import org.landister.vampire.backend.request.LoginRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//This login can be externalized as a separate service in the future.
@ServerEndpoint("/login/{clientId}")
@ApplicationScoped
public class LoginController {

    ObjectMapper mapper = new ObjectMapper();

    Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final Logger LOG = Logger.getLogger(LoginController.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("clientId") String clientId) {
        LOG.info("Open Session for Client: " + clientId);
        sessions.put(clientId, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("clientId") String clientId) {
        LOG.info("Removing client: " + clientId);
        sessions.remove(clientId);
    }

    @OnError
    public void onError(Session session, @PathParam("clientId") String clientId, Throwable throwable) {
        LOG.info("Error for: " + clientId, throwable);
        sessions.remove(clientId);
    }

    @OnMessage
    public void onMessage(String encodedMessage, @PathParam("clientId") String clientId) {
        try {
            String message = new String(Base64.getDecoder().decode(encodedMessage));
            LoginRequest request = mapper.readValue(message, LoginRequest.class);
        } catch (JsonProcessingException e) {
            LOG.error("Invalid Message passed to login");
            throw new RuntimeException(e);
        }
    }

}