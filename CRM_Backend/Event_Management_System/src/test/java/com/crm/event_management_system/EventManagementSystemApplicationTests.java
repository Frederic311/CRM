package com.crm.event_management_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Checks that the application context starts.
 *
 * This test relied on a MySQL already listening on localhost:3306, so it passed
 * only on the machine where it was written. It now runs against an in-memory H2
 * in MySQL compatibility mode, selected by the "test" profile.
 *
 * The scope is deliberate: this verifies the context wires up. Tests that need a
 * real MySQL belong to the integration phase and use Testcontainers.
 *
 * @ActiveProfiles("test") also turns off Eureka registration, which otherwise
 * retries in the background and floods the output.
 */
@SpringBootTest
@ActiveProfiles("test")
class EventManagementSystemApplicationTests {

    @Test
    void contextLoads() {
    }
}