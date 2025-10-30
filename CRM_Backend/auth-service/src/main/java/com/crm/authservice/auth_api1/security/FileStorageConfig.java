package com.crm.authservice.auth_api1.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageConfig {
    private String uploadDir;

}
