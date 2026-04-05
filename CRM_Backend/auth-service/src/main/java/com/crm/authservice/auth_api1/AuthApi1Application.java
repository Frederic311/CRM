package com.crm.authservice.auth_api1;

import com.crm.authservice.auth_api1.Repository.RoleRepository;
import com.crm.authservice.auth_api1.Repository.UserRepository;
import com.crm.authservice.auth_api1.models.Role;
import com.crm.authservice.auth_api1.models.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class AuthApi1Application {
    public static void main(String[] args) {
        SpringApplication.run(AuthApi1Application.class, args);
    }

    @Bean
	public CommandLineRunner runner(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			var userRole = roleRepository.findByName("USER")
					.orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

			var adminRole = roleRepository.findByName("ADMIN")
					.orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));

			if (userRepository.findByEmail("onana.frederic@saintjeaningenieur.org").isEmpty()) {
				var adminUser = User.builder()
						.firstname("System")
						.lastname("Admin")
						.email("onana.frederic@saintjeaningenieur.org")
						.password(passwordEncoder.encode("Admin@12345"))
						.accountLocked(false)
						.enabled(true)
						.isTemporaryPassword(true)
						.roles(List.of(userRole, adminRole))
						.build();

				userRepository.save(adminUser);
			}
		};
	}

}
