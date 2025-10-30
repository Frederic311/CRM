package org.crm.student.application_management_service.repository;


import org.crm.student.application_management_service.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    Optional<Candidate> findByFirstNameAndLastName(String firstName, String lastName);
    Optional<Candidate> findByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
    int countByCandidateIdStartingWith(String candidateIdPrefix);

    Optional<Candidate> findById(Long candidateId);
}
