package com.crm.event_management_system.repository;

import com.crm.event_management_system.models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    // Custom queries can be added here
    Contact findByEmail(String email);
    Contact findByPhoneNumber(String phoneNumber);
}
