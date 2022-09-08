package org.landister.lobby.backend.websocket;

import java.io.IOException;
import java.util.Base64;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;
import org.landister.lobby.backend.model.request.BaseRequest;
import org.landister.lobby.backend.model.request.auth.LoginRequest;
import org.landister.lobby.backend.services.login.LoginService;

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
        super.onError(session, throwable);
    }
 
    @OnMessage
    public void onMessage(Session session, String encodedMessage) throws IOException {
        try {
            LOG.debug("Message for: " + session.getId() + ": " + encodedMessage);
            String message = new String(Base64.getDecoder().decode(encodedMessage));
            LoginRequest request = (LoginRequest) mapper.readValue(message, BaseRequest.class);
            if(request.getRegister()) {
                loginService.register(request);
            }

            // Create our Java Web Token and return it to the client.
            String jwtToken = loginService.login(request.getUsername(), request.getPassword());
            session.getAsyncRemote().sendText(Base64.getEncoder().encodeToString(jwtToken.getBytes()));
            
        } catch (Exception e) {
            LOG.error("Invalid Message passed to login", e);
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid login"));
        }
    }

}