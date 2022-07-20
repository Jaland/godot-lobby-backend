package org.landister.vampire.backend.websocket;

import java.util.Base64;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;
import org.landister.vampire.backend.model.request.LoginRequest;
import org.landister.vampire.backend.model.request.UserRequest;
import org.landister.vampire.backend.services.login.LoginService;

//This login can be externalized as a separate service in the future.
@ServerEndpoint("/login")
@RequestScoped
public class LoginController extends BaseController{

    @Inject
    LoginService loginService;

    private static final Logger LOG = Logger.getLogger(LoginController.class);

    @OnOpen
    public void onOpen(Session session) {
        LOG.info("Open Session: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        LOG.info("Removing client: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        super.onErrorBase(session, throwable);
    }

    @OnMessage
    public void onMessage(Session session, String encodedMessage) {
        try {
            LOG.debug("Message for: " + session.getId() + ": " + encodedMessage);
            String message = new String(Base64.getDecoder().decode(encodedMessage));
            LoginRequest request = (LoginRequest) mapper.readValue(message, UserRequest.class);
            if(request.getRegister()) {
                loginService.register(request);
            }
            String jwtToken = loginService.login(request.getUsername(), request.getPassword());
            session.getAsyncRemote().sendText(Base64.getEncoder().encodeToString(jwtToken.getBytes()));
        } catch (Exception e) {
            LOG.error("Invalid Message passed to login", e);
            sessionCacheService.closeSession(session, "Invalid login");
        }
    }

}