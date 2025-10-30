package com.crm.event_management_system.service;

import com.crm.event_management_system.models.Contact;

import java.util.List;

public interface ContactService {
    Contact createContact(Contact contact);
    Contact updateContact(Long id, Contact contactDetails);
    void deleteContact(Long id);
    List<Contact> getAllContacts();
    Contact getContactById(Long id);
    Contact getContactByEmail(String email);
    Contact getContactByPhoneNumber(String phoneNumber);
}
