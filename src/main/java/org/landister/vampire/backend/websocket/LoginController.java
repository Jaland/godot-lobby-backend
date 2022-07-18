package org.landister.vampire.backend.websocket;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;
import org.landister.vampire.backend.request.LoginRequest;
import org.landister.vampire.backend.services.login.LoginService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;

//This login can be externalized as a separate service in the future.
@ServerEndpoint("/login/{clientId}")
@RequestScoped
public class LoginController {

    @Inject
    LoginService loginService;

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
    public void onMessage(Session session, String encodedMessage, @PathParam("clientId") String clientId) throws IOException {
        try {
            LOG.info("Message for: " + clientId + ": " + encodedMessage);
            String message = new String(Base64.getDecoder().decode(encodedMessage));
            LoginRequest request = mapper.readValue(message, LoginRequest.class);
            if(request.getRegister()) {
                loginService.register(request);
            }
            String jwtToken = loginService.login(request.getUsername(), request.getPassword());
            session.getAsyncRemote().sendText(Base64.getEncoder().encodeToString(jwtToken.getBytes()));
        } catch (Exception e) {
            LOG.error("Invalid Message passed to login", e);
            session.close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION, "Invalid login"));
        }
    }

}