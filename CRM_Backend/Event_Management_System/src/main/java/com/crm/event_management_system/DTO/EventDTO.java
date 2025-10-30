package com.crm.event_management_system.DTO;

import com.crm.event_management_system.models.Event;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventDTO {
    private Long Id;
    private String Subject;
    private LocalDateTime StartTime;
    private LocalDateTime EndTime;

    public EventDTO(Event event) {
        this.Id = event.getId();
        this.Subject = event.getName();
        this.StartTime = LocalDateTime.of(event.getStart(), event.getStart_time());
        this.EndTime = LocalDateTime.of(event.getEnd(), event.getEnd_time());
    }
}

