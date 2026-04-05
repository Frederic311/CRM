package com.codingworld.servicediscovery;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceDiscoveryConfigContractTest {

    @Test
    void applicationYml_shouldKeepRegistryInStandaloneMode() throws IOException {
        String yml = readClasspathResource("application.yml");

        assertTrue(yml.contains("port: 8761"));
        assertTrue(yml.contains("name: service-registry"));
        assertTrue(yml.contains("register-with-eureka: false"));
        assertTrue(yml.contains("fetch-registry: false"));
        assertTrue(yml.contains("defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/"));
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
