package org.landister.vampire.backend.services.login;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PasswordServiceTest {

  @Inject
  PasswordService service;

  @Test
  public void testEncodingPassword() {
    String PASSWORD = "super-secret-password ;)";
    String encodedPayload = service.encryptPassword(PASSWORD);
    assert (service.checkPassword(PASSWORD, encodedPayload));
  }
  
}
