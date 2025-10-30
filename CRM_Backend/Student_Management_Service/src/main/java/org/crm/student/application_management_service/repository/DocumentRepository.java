package org.crm.student.application_management_service.repository;

import org.crm.student.application_management_service.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
