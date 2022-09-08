package org.landister.lobby.backend.services.login;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;

@QuarkusTest
public class LoginServiceTest {

  @Inject
  LoginService service;

  @Inject
  JWTParser parser;

  @Test
  public void testJwt() throws ParseException {
    String jwt = Jwt.upn("TEST_USERNAME").expiresIn(18000).signWithSecret("fasdkjlfjksdjfkl;dajlwereakljnvkldarfe");
    System.out.println(jwt);
    JsonWebToken token = parser.verify(jwt, "fasdkjlfjksdjfkl;dajlwereakljnvkldarfe");
    assertNotNull(token);
  }
  
}
