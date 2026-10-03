package com.crm.authservice.auth_api1;

import com.crm.authservice.auth_api1.Service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks that the application context starts, and that the values it wires up
 * are the ones the configuration declares.
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

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    void contextLoads() {
    }

    /**
     * The reset URL used to be a literal, "@Value(\"http://localhost:4200/resertUrl\")",
     * misspelled and pointing at a route that does not exist. Reading it here
     * from the wired bean is what tells the two apart: the field now holds what
     * application.yml declares.
     *
     * That the placeholder itself resolves is already covered by contextLoads,
     * which would not start on a key that matches nothing.
     */
    @Test
    @DisplayName("both mailed URLs are read from configuration and are absolute")
    void bothMailedUrlsAreReadFromConfiguration() {
        String resetUrl = (String) ReflectionTestUtils.getField(authenticationService, "resetUrl");
        String activationUrl = (String) ReflectionTestUtils.getField(authenticationService, "activationUrl");

        assertThat(resetUrl)
                .isNotNull()
                .isNotEqualTo("http://localhost:4200/resertUrl")
                .startsWith("http")
                .contains("/forget-password");

        assertThat(activationUrl)
                .isNotNull()
                .startsWith("http")
                .doesNotContain("activate-account");
    }
}