package com.crm.event_management_system.serviceImpl;

import com.crm.event_management_system.models.EventType;
import com.crm.event_management_system.repository.EventTypeRepository;
import com.crm.event_management_system.service.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventTypeServiceImpl implements EventTypeService {

    private final EventTypeRepository eventTypeRepository;

    @Autowired
    public EventTypeServiceImpl(EventTypeRepository eventTypeRepository) {
        this.eventTypeRepository = eventTypeRepository;
    }

    @Override
    public EventType createEventType(EventType eventType) {
        return eventTypeRepository.save(eventType);
    }

    @Override
    public EventType updateEventType(Long id, EventType eventTypeDetails) {
        Optional<EventType> existingEventType = eventTypeRepository.findById(id);
        if (existingEventType.isPresent()) {
            EventType eventType = existingEventType.get();
            eventType.setName(eventTypeDetails.getName());
            return eventTypeRepository.save(eventType);
        }
        throw new RuntimeException("EventType not found");
    }

    @Override
    public void deleteEventType(Long id) {
        eventTypeRepository.deleteById(id);
    }

    @Override
    public List<EventType> getAllEventTypes() {
        return eventTypeRepository.findAll();
    }

    @Override
    public EventType getEventTypeById(Long id) {
        return eventTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("EventType not found"));
    }

    @Override
    public EventType getEventTypeByName(String name) {
        return eventTypeRepository.findByName(name);
    }


    @Override public Long getIdByName(String name) { EventType eventType = eventTypeRepository.findByName(name); if (eventType != null) { return eventType.getId(); } throw new RuntimeException("EventType not found"); }
}
