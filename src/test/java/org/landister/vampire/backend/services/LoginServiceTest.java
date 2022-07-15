package org.landister.vampire.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;

@QuarkusTest
public class LoginServiceTest {

  @Inject
  LoginService service;

  @Test
  public void testEncodingPassword() {
    String PASSWORD = "super-secret-password ;)";
    String encodedPayload = service.encryptPassword(PASSWORD);
    assert (service.checkPassword(PASSWORD, encodedPayload));
  }
  
}
