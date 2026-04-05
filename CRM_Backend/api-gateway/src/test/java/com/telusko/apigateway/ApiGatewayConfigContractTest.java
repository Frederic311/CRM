package com.telusko.apigateway;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiGatewayConfigContractTest {

    @Test
    void applicationYml_shouldContainConfiguredRoutesAndCors() throws IOException {
        String yml = readClasspathResource("application.yml");

        assertTrue(yml.contains("port: 8060"));
        assertTrue(yml.contains("Path=/tasks/**"));
        assertTrue(yml.contains("Path=/api/candidates/**"));
        assertTrue(yml.contains("Path=/api/v1/**"));
        assertTrue(yml.contains("Path=/api/notifications/**"));
        assertTrue(yml.contains("allowedOrigins: \"http://localhost:4200\""));
    }

    private String readClasspathResource(String resourcePath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
