package com.crm.event_management_system.serviceImpl;

import com.crm.event_management_system.models.Event;
import com.crm.event_management_system.repository.EventRepository;
import com.crm.event_management_system.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Long id, Event eventDetails) {
        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isPresent()) {
            Event event = existingEvent.get();
            event.setName(eventDetails.getName());
            event.setStart(eventDetails.getStart());
            event.setEnd(eventDetails.getEnd());
            event.setStart_time(eventDetails.getStart_time());
            event.setEnd_time(eventDetails.getEnd_time());
            event.setExpected_person(eventDetails.getExpected_person());
            event.setBudget(eventDetails.getBudget());
            event.setDescription(eventDetails.getDescription());
            event.setTypeId(eventDetails.getTypeId());
            event.setVenue(eventDetails.getVenue());
            return eventRepository.save(event);
        }
        throw new RuntimeException("Event not found");
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Override
    public Event getEventByName(String name) {
        return eventRepository.findByName(name);
    }
}
