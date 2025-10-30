package com.crm.event_management_system.service;

import com.crm.event_management_system.models.Venue;

import java.util.List;

public interface VenueService {
    Venue createVenue(Venue venue);
    Venue updateVenue(Long id, Venue venueDetails);
    void deleteVenue(Long id);
    List<Venue> getAllVenues();
    Venue getVenueById(Long id);
    Venue getVenueByName(String name);
    Long getIdByName(String name);
}
