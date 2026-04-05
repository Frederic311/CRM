package com.crm.event_management_system.serviceImpl;

import com.crm.event_management_system.models.Event;
import com.crm.event_management_system.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void updateEvent_shouldCopyFieldsAndPersist() {
        Event existing = new Event();
        existing.setId(1L);
        existing.setName("Old Name");

        Event details = new Event();
        details.setName("New Name");
        details.setStart(LocalDate.of(2026, 4, 5));
        details.setEnd(LocalDate.of(2026, 4, 6));
        details.setStart_time(LocalTime.of(9, 0));
        details.setEnd_time(LocalTime.of(12, 0));
        details.setExpected_person(120);
        details.setBudget(4500f);
        details.setDescription("Updated event");
        details.setTypeId(2L);
        details.setVenue("Main Hall");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event updated = eventService.updateEvent(1L, details);

        assertEquals("New Name", updated.getName());
        assertEquals(LocalDate.of(2026, 4, 5), updated.getStart());
        assertEquals(120, updated.getExpected_person());
        assertEquals("Main Hall", updated.getVenue());
        verify(eventRepository).save(existing);
    }

    @Test
    void getEventById_shouldThrowWhenMissing() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> eventService.getEventById(99L));

        assertEquals("Event not found", exception.getMessage());
    }
}
