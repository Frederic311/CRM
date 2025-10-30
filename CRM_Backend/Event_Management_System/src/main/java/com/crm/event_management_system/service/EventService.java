package com.crm.event_management_system.service;

import com.crm.event_management_system.models.Event;

import java.util.List;

public interface EventService {
    Event createEvent(Event event);
    Event updateEvent(Long id, Event eventDetails);
    void deleteEvent(Long id);
    List<Event> getAllEvents();
    Event getEventById(Long id);
    Event getEventByName(String name);

}
