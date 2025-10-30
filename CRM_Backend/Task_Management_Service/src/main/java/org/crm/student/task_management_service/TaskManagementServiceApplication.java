package org.crm.student.task_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
public class TaskManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagementServiceApplication.class, args);
	}

}
