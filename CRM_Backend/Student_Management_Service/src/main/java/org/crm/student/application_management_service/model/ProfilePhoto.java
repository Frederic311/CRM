package org.crm.student.application_management_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
public class ProfilePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Back reference to the owning Candidate.
     *
     * JsonIgnore, not JsonManagedReference. Candidate holds the owning side
     * through profilePhoto, and this field points back, so serializing a
     * Candidate produced candidate -> photo -> candidate -> photo until Jackson
     * aborted with Infinite recursion (StackOverflowError). That is what made
     * GET /api/candidates fail.
     *
     * JsonManagedReference was imported in this file but never applied, which is
     * why the relation was never actually broken.
     *
     * JsonIgnore is the right choice over JsonBackReference here: this side is
     * only ever reached through Candidate, so no consumer of the API needs it,
     * and ignoring it keeps a photo usable on its own.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "candidate_id", referencedColumnName = "id")
    private Candidate candidate;

    @Lob
    @Column(name = "photo_data", columnDefinition = "BLOB")
    private byte[] photoData;  // Store the image data as a byte array
}