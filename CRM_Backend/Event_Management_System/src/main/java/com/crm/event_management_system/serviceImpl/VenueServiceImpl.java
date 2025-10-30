package com.crm.event_management_system.serviceImpl;

import com.crm.event_management_system.models.Venue;
import com.crm.event_management_system.repository.VenueRepository;
import com.crm.event_management_system.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

    @Autowired
    public VenueServiceImpl(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Override
    public Venue createVenue(Venue venue) {
        return venueRepository.save(venue);
    }

    @Override
    public Venue updateVenue(Long id, Venue venueDetails) {
        Optional<Venue> existingVenue = venueRepository.findById(id);
        if (existingVenue.isPresent()) {
            Venue venue = existingVenue.get();
            venue.setName(venueDetails.getName());
            venue.setAddress(venueDetails.getAddress());
            venue.setCapacity(venueDetails.getCapacity());
            venue.setLatitude(venueDetails.getLatitude());
            venue.setLongitude(venueDetails.getLongitude());
            return venueRepository.save(venue);
        }
        throw new RuntimeException("Venue not found");
    }

    @Override
    public void deleteVenue(Long id) {
        venueRepository.deleteById(id);
    }

    @Override
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    @Override
    public Venue getVenueById(Long id) {
        return venueRepository.findById(id).orElseThrow(() -> new RuntimeException("Venue not found"));
    }

    @Override
    public Venue getVenueByName(String name) {
        return venueRepository.findByName(name);
    }

    @Override public Long getIdByName(String name) { Venue venue = venueRepository.findByName(name); if (venue != null) { return venue.getId(); } throw new RuntimeException("Venue not found"); }
}
