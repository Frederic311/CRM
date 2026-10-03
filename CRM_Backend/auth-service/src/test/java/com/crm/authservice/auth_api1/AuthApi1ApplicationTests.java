package com.crm.authservice.auth_api1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Checks that the application context starts.
 *
 * This test needed a live MySQL, mail credentials, a JWT signing key and the
 * activation URL, all of which were declared without a default, so it only
 * passed on the machine where it was written. It now runs against an in-memory
 * H2 in MySQL compatibility mode with placeholder credentials supplied by
 * src/test/resources/application-test.yml.
 *
 * The scope is deliberate: this verifies the context wires up. Tests that need a
 * real MySQL belong to the integration phase and use Testcontainers.
 *
 * application.bootstrap.admin.enabled is false so the tests never create an
 * account with a fixed password.
 */
@SpringBootTest
@ActiveProfiles("test")
class AuthApi1ApplicationTests {

    @Test
    void contextLoads() {
    }
}