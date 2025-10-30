package org.crm.student.application_management_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @NotBlank(message = "Document type cannot be empty")
    private String documentType;

    @NotBlank(message = "File path cannot be empty")
    private String filePath;

    @NotNull(message = "Upload date cannot be null")
    private LocalDate uploadDate;

    @NotNull(message = "Expiry date cannot be null")
    private LocalDate expiryDate;

    private Integer version;

    // Optionally, you could override toString() for better logging
    @Override
    public String toString() {
        return String.format("Document{id=%d, candidate=%s, documentType='%s', filePath='%s', uploadDate=%s, expiryDate=%s, version=%d}",
                id, candidate.getId(), documentType, filePath, uploadDate, expiryDate, version);
    }
}
