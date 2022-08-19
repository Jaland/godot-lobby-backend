package org.landister.vampire.backend.services.login;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.landister.vampire.backend.model.dao.auth.AuthUser;
import org.landister.vampire.backend.model.request.auth.LoginRequest;
import org.landister.vampire.backend.util.PasswordService;

import io.smallrye.jwt.build.Jwt;

@ApplicationScoped
public class LoginService {

	@Inject
	PasswordService passwordService;

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
			throw new IllegalArgumentException("User not found");
		}
		if(!passwordService.checkPassword(password, user.getToken())){
			throw new IllegalArgumentException("Invalid password");
		}
		return Jwt.upn(username).expiresIn(jwtExpirationTime).signWithSecret(jwtSecret) ;
	}

	public void register(LoginRequest request) {
		AuthUser user = AuthUser.findByUsername(request.getUsername());
		if(user != null) {
			throw new IllegalArgumentException("User already exists");
		}
		user = new AuthUser();
		user.setUsername(request.getUsername());
		user.setToken(passwordService.encryptPassword(request.getPassword()));
		user.persist();
	}

	

}
