package org.crm.student.task_management_service.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserClient {

    private final RestTemplate restTemplate;


    public UserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean validateUser(String username) {
        String url = "http://localhost:8060/api/v1/auth/" + username + "/exists";
        try {
            return restTemplate.getForObject(url, Boolean.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate username: " + username, e);
        }
    }
}
