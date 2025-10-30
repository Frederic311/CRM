package com.crm.event_management_system.controller;

import com.crm.event_management_system.models.EventType;
import com.crm.event_management_system.service.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")  // Allow Angular frontend to access this API
@RestController
@RequestMapping("/api/event-types")
public class EventTypeController {

    private final EventTypeService eventTypeService;

    @Autowired
    public EventTypeController(EventTypeService eventTypeService) {
        this.eventTypeService = eventTypeService;
    }

    // Create a new event type
    @PostMapping
    public ResponseEntity<EventType> createEventType(@RequestBody EventType eventType) {
        EventType createdEventType = eventTypeService.createEventType(eventType);
        return new ResponseEntity<>(createdEventType, HttpStatus.CREATED);
    }

    // Get all event types
    @GetMapping
    public ResponseEntity<List<EventType>> getAllEventTypes() {
        List<EventType> eventTypes = eventTypeService.getAllEventTypes();
        return new ResponseEntity<>(eventTypes, HttpStatus.OK);
    }

    // Get event type by id
    @GetMapping("/{id}")
    public ResponseEntity<EventType> getEventTypeById(@PathVariable Long id) {
        EventType eventType = eventTypeService.getEventTypeById(id);
        return new ResponseEntity<>(eventType, HttpStatus.OK);
    }

    // Get event type by name
    @GetMapping("/name/{name}")
    public ResponseEntity<EventType> getEventTypeByName(@PathVariable String name) {
        EventType eventType = eventTypeService.getEventTypeByName(name);
        return new ResponseEntity<>(eventType, HttpStatus.OK);
    }

    // Update an event type
    @PutMapping("/{id}")
    public ResponseEntity<EventType> updateEventType(@PathVariable Long id, @RequestBody EventType eventTypeDetails) {
        EventType updatedEventType = eventTypeService.updateEventType(id, eventTypeDetails);
        return new ResponseEntity<>(updatedEventType, HttpStatus.OK);
    }

    // Delete an event type
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventType(@PathVariable Long id) {
        eventTypeService.deleteEventType(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get event type ID by name
     @GetMapping("/name/{name}/id") public ResponseEntity<Long> getIdByName(@PathVariable String name)
     { Long id = eventTypeService.getIdByName(name); return new ResponseEntity<>(id, HttpStatus.OK); }
}
