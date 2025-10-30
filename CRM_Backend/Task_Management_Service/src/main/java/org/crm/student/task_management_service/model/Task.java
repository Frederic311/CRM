package org.crm.student.task_management_service.model;

import lombok.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;


@Data
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String candidateFullname = "no association";

    private String CreatedBy;
    @NotBlank
    private String type; // e.g., "Call", "Email", "Appointment"

    @NotBlank
    private String description;

    @NotNull
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private boolean completed = false;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @NotNull
    private String assignedTo;
    private LocalDate assignedDate;
    private String assignedToEmail;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED
    }
    @PrePersist
    public void prePersist() {
        if (this.assignedDate == null) {
            this.assignedDate = LocalDate.now();
        }
    }

}


