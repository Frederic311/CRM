package org.crm.student.application_management_service.repository;

import org.crm.student.application_management_service.model.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, Long> {
    Optional<ProfilePhoto> findByCandidateId(Long candidateId);
}