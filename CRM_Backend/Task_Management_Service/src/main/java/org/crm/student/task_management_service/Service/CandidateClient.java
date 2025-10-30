package org.crm.student.task_management_service.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class CandidateClient {

    private final RestTemplate restTemplate;


    public CandidateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean validateCandidate(String candidateFullname) {
        String url = "http://localhost:8082/api" + "/candidates/" + candidateFullname + "/exists";
        try {
            return restTemplate.getForObject(url, Boolean.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate candidateId: " + candidateFullname, e);
        }
    }
}


