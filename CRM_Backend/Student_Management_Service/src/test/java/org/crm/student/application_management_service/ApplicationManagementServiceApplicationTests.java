package org.crm.student.application_management_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Checks that the application context starts.
 *
 * This test relied on a MySQL already listening on localhost:3306, so it passed
 * on the machine where it was written and failed everywhere else, which
 * includes any CI runner.
 *
 * It now runs against an in-memory H2 in MySQL compatibility mode, selected by
 * the "test" profile through src/test/resources/application-test.yml. That is
 * the right scope for this test: it verifies the context wires up, it never
 * exercised the MySQL dialect.
 *
 * Tests that genuinely need a real MySQL belong to the integration phase and
 * use Testcontainers. They are deliberately not mixed in here, because a
 * container per module would make the smoke tests slow and would make them fail
 * on any machine without a working Docker.
 *
 * @ActiveProfiles("test") also turns off Eureka registration, which otherwise
 * retries in the background and floods the output.
 */
@SpringBootTest
@ActiveProfiles("test")
class ApplicationManagementServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}