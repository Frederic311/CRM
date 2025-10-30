package com.crm.event_management_system.repository;

import com.crm.event_management_system.models.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long> {
    // Custom queries can be added here
    EventType findByName(String name);
}
