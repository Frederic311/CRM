package com.crm.event_management_system.repository;

import com.crm.event_management_system.models.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    // Custom queries can be added here
    Venue findByName(String name);
}
