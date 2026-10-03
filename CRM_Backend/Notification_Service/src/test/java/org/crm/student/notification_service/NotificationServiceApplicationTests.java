package org.crm.student.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Checks that the application context starts.
 *
 * This service excludes the DataSource auto-configuration, so no database is
 * involved. It did fail everywhere except the author's machine because
 * application.yml declares the Twilio and mail credentials without a default: an
 * unset variable left the placeholder unresolvable and the context never
 * started. The "test" profile supplies placeholders through
 * src/test/resources/application-test.yml.
 *
 * @ActiveProfiles("test") also turns off Eureka registration, which otherwise
 * retries in the background and floods the output.
 */
@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}