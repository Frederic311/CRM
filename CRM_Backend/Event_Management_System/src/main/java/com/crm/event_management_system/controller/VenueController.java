package com.crm.event_management_system.controller;

import com.crm.event_management_system.models.Venue;
import com.crm.event_management_system.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")  // Allow CORS for this controller
@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    @Autowired
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    // Create a new venue
    @PostMapping
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        Venue createdVenue = venueService.createVenue(venue);
        return new ResponseEntity<>(createdVenue, HttpStatus.CREATED);
    }

    // Get all venues
    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        List<Venue> venues = venueService.getAllVenues();
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }

    // Get venue by id
    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable Long id) {
        Venue venue = venueService.getVenueById(id);
        return new ResponseEntity<>(venue, HttpStatus.OK);
    }

    // Update a venue
    @PutMapping("/{id}")
    public ResponseEntity<Venue> updateVenue(@PathVariable Long id, @RequestBody Venue venueDetails) {
        Venue updatedVenue = venueService.updateVenue(id, venueDetails);
        return new ResponseEntity<>(updatedVenue, HttpStatus.OK);
    }

    // Delete a venue
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

// Get venue ID by name
 @GetMapping("/name/{name}/id") public ResponseEntity<Long> getIdByName(@PathVariable String name)
  { Long id = venueService.getIdByName(name); return new ResponseEntity<>(id, HttpStatus.OK); }
}
