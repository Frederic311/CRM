package com.crm.event_management_system.serviceImpl;

import com.crm.event_management_system.models.Contact;
import com.crm.event_management_system.repository.ContactRepository;
import com.crm.event_management_system.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public Contact updateContact(Long id, Contact contactDetails) {
        Optional<Contact> existingContact = contactRepository.findById(id);
        if (existingContact.isPresent()) {
            Contact contact = existingContact.get();
            contact.setFirstName(contactDetails.getFirstName());
            contact.setLastName(contactDetails.getLastName());
            contact.setEmail(contactDetails.getEmail());
            contact.setPhoneNumber(contactDetails.getPhoneNumber());
            contact.setAddress(contactDetails.getAddress());
            return contactRepository.save(contact);
        }
        throw new RuntimeException("Contact not found");
    }

    @Override
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }

    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public Contact getContactById(Long id) {
        return contactRepository.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public Contact getContactByEmail(String email) {
        return contactRepository.findByEmail(email);
    }

    @Override
    public Contact getContactByPhoneNumber(String phoneNumber) {
        return contactRepository.findByPhoneNumber(phoneNumber);
    }
}
