package org.crm.student.application_management_service.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "candidate_details")
@Data
public class Candidate{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    private String country;

    private String city;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Column(unique = true, nullable = false)
    private String phoneNumber;

    private LocalDateTime applicationDate;

    private String applicationSource;

    private String field;
    // New field for generated candidate ID
    private String candidateId;

    // Relationships
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_detail_id")
    private Parent parentDetail;



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employment_detail_id")
    private EmploymentDetails employmentDetail;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "education_detail_id")
    private EducationDetails educationDetail;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_image_id")
    private ProfilePhoto profilePhoto;





    @Enumerated(EnumType.STRING)
    private Status status;
    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = Status.NEW;
        }
    }
    public void setInstitutionName(String institutionName) {
    }
    public void setGraduationYear(int graduationYear) {
    }
    // Logic to generate Candidate ID
    private void generateCandidateId() {
        // Get the current year (e.g., 2024)
        String currentYear = String.valueOf(LocalDate.now().getYear());

        // Use the first three letters of the field (e.g., "ING" for "Engineering")
        String fieldCode = field != null ? field.substring(0, 3).toUpperCase() : "UNK";

        // Get the latest candidate number for the same year and field
        int candidateCount = getCandidateCountForYearAndField(currentYear, fieldCode);

        // Generate the candidate ID
        String incrementedId = String.format("%04d", candidateCount + 1);
        this.candidateId = currentYear + fieldCode + incrementedId;
    }

    // Custom method to get the candidate count for the current year and field
    private int getCandidateCountForYearAndField(String year, String field) {
        // Query the database to count the number of candidates with the same year and field
        // Example query: SELECT COUNT(*) FROM Candidate WHERE candidateId LIKE '2024ING%'
        return 0; // Replace with actual query count logic
    }
}
