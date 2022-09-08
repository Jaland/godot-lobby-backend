package org.landister.lobby.backend.services.login;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.landister.lobby.backend.model.dao.auth.AuthUser;
import org.landister.lobby.backend.model.request.auth.LoginRequest;
import org.landister.lobby.backend.util.exceptions.AuthException;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;

@ApplicationScoped
public class LoginService {

	@ConfigProperty(name = "jwt.secret")
	String jwtSecret;

	@ConfigProperty(name = "jwt.timeout")
	int jwtExpirationTime;

	/**
	 * Authenticates a user and returns a JWT token if the credentials are valid.
	 * @param username
	 * @param password
	 * @return
	 */
	public String login(String username, String password) {
		AuthUser user = AuthUser.findByUsername(username);
		if(user == null) {
			throw new AuthException("User not found");
		}
		if(!BcryptUtil.matches(password, user.getToken())){
			throw new AuthException("Invalid password");
		}
		return Jwt.upn(username).expiresIn(jwtExpirationTime).signWithSecret(jwtSecret) ;
	}

	public void register(LoginRequest request) {
		AuthUser user = AuthUser.findByUsername(request.getUsername());
		if(user != null) {
			throw new AuthException("User already exists");
		}
		user = new AuthUser();
		user.setUsername(request.getUsername());
		user.setToken(BcryptUtil.bcryptHash(request.getPassword()));
		user.persist();
	}

	

}
