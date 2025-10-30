package org.crm.student.application_management_service.service;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.crm.student.application_management_service.model.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CSVHelper {

    public List<Candidate> parseCSV(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        CsvToBean<CandidateCSV> csvToBean = new CsvToBeanBuilder<CandidateCSV>(reader)
                .withType(CandidateCSV.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<CandidateCSV> csvRecords = csvToBean.parse();
        List<Candidate> candidates = new ArrayList<>();

        for (CandidateCSV record : csvRecords) {
            Candidate candidate = new Candidate();
            candidate.setFirstName(record.getFirstName());
            candidate.setLastName(record.getLastName());
            candidate.setCountry(record.getCountry());
            candidate.setCity(record.getCity());
            candidate.setEmail(record.getEmail());
            candidate.setPhoneNumber(record.getPhoneNumber());
            candidate.setApplicationSource(record.getApplicationSource());
            candidate.setField(record.getField());
            candidate.setApplicationDate(record.getApplicationDate());

            // Set Parent details
            Parent parent = new Parent();
            parent.setFullName(record.getParentFullName());
            parent.setEmail(record.getParentEmail());
            parent.setPhoneNumber(record.getParentPhone());
            parent.setRelationshipToCandidate(record.getParentRelationship());
            candidate.setParentDetail(parent);

            // Set Employment details
            EmploymentDetails employmentDetails = new EmploymentDetails();
            employmentDetails.setCompanyName(record.getCompanyName());
            employmentDetails.setResponsibilities(record.getResponsibilities());
            candidate.setEmploymentDetail(employmentDetails);

            // Set Education details
            EducationDetails educationDetails = new EducationDetails();
            educationDetails.setHighestEducation(record.getHighestEducation());
            educationDetails.setInstitutionName(record.getInstitutionName());
            educationDetails.setGraduationYear(record.getGraduationYear());
            educationDetails.setFieldOfStudy(record.getFieldOfStudy());
            candidate.setEducationDetail(educationDetails);

            candidates.add(candidate);
        }
        return candidates;
    }

}
