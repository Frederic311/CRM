package com.crm.event_management_system.repository;

import com.crm.event_management_system.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // Custom queries can be added here, e.g., find events by name, email, etc.
    Event findByName(String name);

}
