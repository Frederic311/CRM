package com.crm.authservice.auth_api1;

import com.crm.authservice.auth_api1.Repository.RoleRepository;
import com.crm.authservice.auth_api1.Repository.UserRepository;
import com.crm.authservice.auth_api1.models.Role;
import com.crm.authservice.auth_api1.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class AuthApi1Application {

    private static final Logger log = LoggerFactory.getLogger(AuthApi1Application.class);

    public static void main(String[] args) {
        SpringApplication.run(AuthApi1Application.class, args);
    }

    /**
     * Creates the USER and ADMIN roles, and optionally one administrator.
     *
     * The roles are always created. The administrator is only created when
     * explicitly requested, because a credential baked into the source made
     * every deployment reachable with the same known password.
     */
    @Bean
    public CommandLineRunner bootstrapRoles(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${application.bootstrap.admin.enabled:false}") boolean adminEnabled,
            @Value("${application.bootstrap.admin.email:}") String adminEmail,
            @Value("${application.bootstrap.admin.password:}") String adminPassword) {

        return args -> {
            var userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

            var adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));

            if (!adminEnabled) {
                return;
            }

            if (!StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
                log.warn("Administrator bootstrap is enabled but BOOTSTRAP_ADMIN_EMAIL or "
                        + "BOOTSTRAP_ADMIN_PASSWORD is missing. No account created.");
                return;
            }

            if (userRepository.findByEmail(adminEmail).isPresent()) {
                log.info("Administrator {} already exists, nothing to do.", adminEmail);
                return;
            }

            userRepository.save(User.builder()
                    .firstname("System")
                    .lastname("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .accountLocked(false)
                    .enabled(true)
                    .isTemporaryPassword(true)
                    .roles(List.of(userRole, adminRole))
                    .build());

            log.info("Administrator {} created.", adminEmail);
        };
    }

}
