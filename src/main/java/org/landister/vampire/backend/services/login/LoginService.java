package org.landister.vampire.backend.services.login;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.landister.vampire.backend.model.auth.User;
import org.landister.vampire.backend.model.request.LoginRequest;
import org.landister.vampire.backend.util.PasswordService;

import io.smallrye.jwt.build.Jwt;

@ApplicationScoped
public class LoginService {

	@Inject
	PasswordService passwordService;

	@ConfigProperty(name = "jwt.secret")
	String jwtSecret;

	// int jwtExpirationTime = 18000;
	int jwtExpirationTime = 0;

	/**
	 * Authenticates a user and returns a JWT token if the credentials are valid.
	 * @param username
	 * @param password
	 * @return
	 */
	public String login(String username, String password) {
		User user = User.findByUsername(username);
		if(user == null) {
			throw new IllegalArgumentException("User not found");
		}
		if(!passwordService.checkPassword(password, user.getToken())){
			throw new IllegalArgumentException("Invalid password");
		}
		return Jwt.upn(username).expiresIn(jwtExpirationTime).signWithSecret(jwtSecret) ;
	}

	public void register(LoginRequest request) {
		User user = User.findByUsername(request.getUsername());
		if(user != null) {
			throw new IllegalArgumentException("User already exists");
		}
		user = new User();
		user.setUsername(request.getUsername());
		user.setToken(passwordService.encryptPassword(request.getPassword()));
		user.persist();
	}

	

}