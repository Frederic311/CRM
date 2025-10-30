package org.crm.student.application_management_service.repository;

import org.crm.student.application_management_service.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Integer> {
}
