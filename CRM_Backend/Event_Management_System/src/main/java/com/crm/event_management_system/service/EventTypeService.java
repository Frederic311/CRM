package com.crm.event_management_system.service;

import com.crm.event_management_system.models.EventType;

import java.util.List;

public interface EventTypeService {
    EventType createEventType(EventType eventType);
    EventType updateEventType(Long id, EventType eventTypeDetails);
    void deleteEventType(Long id);
    List<EventType> getAllEventTypes();
    EventType getEventTypeById(Long id);
    EventType getEventTypeByName(String name);
    Long getIdByName(String name);
}
