package org.crm.student.application_management_service.service;

import org.crm.student.application_management_service.model.Candidate;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CSVHelperTest {

    @Test
    void parseCSV_shouldMapCandidateAndNestedDetails() throws Exception {
        String csv = "firstName,lastName,country,city,email,phoneNumber,applicationSource,field,applicationDate,parentFullName,parentEmail,parentPhone,parentRelationship,companyName,responsibilities,highestEducation,institutionName,graduationYear,fieldOfStudy\n"
                + "John,Doe,CM,YA,john.doe@example.com,123456789,Website,Engineering,2026-01-20T10:15:30,Jane Doe,jane@example.com,987654321,Mother,ACME,Develop features,Master,Polytech,2024,Software\n";

        CSVHelper helper = new CSVHelper();
        ByteArrayInputStream input = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        List<Candidate> candidates = helper.parseCSV(input);

        assertEquals(1, candidates.size());
        Candidate candidate = candidates.get(0);

        assertEquals("John", candidate.getFirstName());
        assertEquals("Doe", candidate.getLastName());
        assertEquals("john.doe@example.com", candidate.getEmail());
        assertEquals("Engineering", candidate.getField());
        assertEquals(LocalDateTime.of(2026, 1, 20, 10, 15, 30), candidate.getApplicationDate());

        assertNotNull(candidate.getParentDetail());
        assertEquals("Jane Doe", candidate.getParentDetail().getFullName());
        assertEquals("jane@example.com", candidate.getParentDetail().getEmail());

        assertNotNull(candidate.getEmploymentDetail());
        assertEquals("ACME", candidate.getEmploymentDetail().getCompanyName());

        assertNotNull(candidate.getEducationDetail());
        assertEquals("Master", candidate.getEducationDetail().getHighestEducation());
        assertEquals(2024, candidate.getEducationDetail().getGraduationYear());
    }
}
